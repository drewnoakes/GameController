package controller.net.protocol;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.*;
import data.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements game state network protocol, version 8.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class GameStateProtocol8 extends GameStateProtocol
{
    /**
     * The number of player entries in a game state message.
     * This may be more than the number of players allowed by the rules.
     */
    private static final byte NUM_PLAYERS_IN_GAME_STATE_MESSAGE = 11;

    private final League league;

    public GameStateProtocol8(@NotNull League league)
    {
        super((byte) 8);
        this.league = league;
    }

    @Override
    public int getMessageSize()
    {
        final int playerSize =
                1 + // penalty
                1;  // secsToUnpenalize

        final int teamSize =
                1 + // teamNumber
                1 + // teamColor
                1 + // score
                1 + // penaltyShot
                2 + // singleShots
                SPLCoachMessage.SIZE + // coach's message
                (NUM_PLAYERS_IN_GAME_STATE_MESSAGE + 1) * playerSize; // +1 for the coach

        return  4 + // header
                1 + // version
                1 + // packet number
                1 + // numPlayers
                1 + // playMode
                1 + // firstHalf
                1 + // nextKickOffColor
                1 + // period
                1 + // lastDropInColor
                2 + // dropInTime
                2 + // secsRemaining
                2 + // secondaryTime
                2 * teamSize;
    }

    @NotNull
    @Override
    public byte[] toBytes(@NotNull ReadOnlyGameState state)
    {
        ByteBuffer buffer = writeHeader();

        buffer.put(getVersionNumber());
        buffer.put(nextPacketNumber);
        buffer.put((byte)league.settings().teamSize);
        buffer.put(state.getPlayMode().getValue());
        buffer.put(state.isFirstHalf() ? (byte)1 : 0);
        buffer.put(state.getNextKickOffColor() == null ? 2 : state.getNextKickOffColor().getValue());
        buffer.put(state.getPeriod().getValue());
        // V8 sends '0' (blue) when no drop in has occurred. This is addressed in V9.
        buffer.put(state.getLastDropInColor() == null ? 0 : state.getLastDropInColor().getValue());
        buffer.putShort((short)state.getDropInTime());
        buffer.putShort((short)state.getSecsRemaining());
        buffer.putShort((short)state.getSecondaryTime());

        writeTeamInfo(buffer, state.getTeam(UISide.Left));
        writeTeamInfo(buffer, state.getTeam(UISide.Right));

        return buffer.array();
    }

    @Nullable
    @Override
    public GameStateSnapshot fromBytes(@NotNull ByteBuffer buffer)
    {
        if (!verifyHeader(buffer))
            return null;

        buffer.get(); // packet number (ignored when decoding)
        buffer.get(); // players per team (ignored when decoding -- should equal league.settings().teamSize)
        PlayMode playMode = PlayMode.fromValue(buffer.get());
        boolean firstHalf = buffer.get() != 0;
        TeamColor nextKickOffColor = TeamColor.fromValue(buffer.get());
        Period period = Period.fromValue(buffer.get());
        TeamColor lastDropInColor = TeamColor.fromValue(buffer.get());
        short dropInTime = buffer.getShort();
        short secsRemaining = buffer.getShort();
        short secondaryTime = buffer.getShort();

        TeamStateSnapshot team1 = teamFromBytes(buffer);
        TeamStateSnapshot team2 = teamFromBytes(buffer);

        return new GameStateSnapshot(
                playMode, firstHalf, nextKickOffColor, period, lastDropInColor, dropInTime,
                secsRemaining, team1, team2, secondaryTime, -1);
    }

    @NotNull
    private TeamStateSnapshot teamFromBytes(@NotNull ByteBuffer buffer)
    {
        byte teamNumber = buffer.get();
        TeamColor teamColor = TeamColor.fromValue(buffer.get());
        byte score = buffer.get();
        byte penaltyShot = buffer.get();
        short singleShots = buffer.getShort();

        byte[] coachMessage = new byte[SPLCoachMessage.SIZE];
        buffer.get(coachMessage);
        PlayerStateSnapshot coach = playerFromBytes(buffer);

        List<PlayerStateSnapshot> players = new ArrayList<PlayerStateSnapshot>(league.settings().teamSize);

        for (int uniformNumber = 1; uniformNumber <= league.settings().teamSize; uniformNumber++) {
            Penalty penalty = Penalty.fromValue(league, buffer.get());
            byte secondsUntilUnpenalised = buffer.get();
            players.add(new PlayerStateSnapshot(penalty, secondsUntilUnpenalised));
        }

        return new TeamStateSnapshot(teamNumber, teamColor, score, penaltyShot, singleShots, players, coachMessage, coach);
    }

    private PlayerStateSnapshot playerFromBytes(ByteBuffer buffer)
    {
        Penalty penalty = Penalty.fromValue(league, buffer.get());
        byte secsTillUnpenalised = buffer.get();

        return new PlayerStateSnapshot(penalty, secsTillUnpenalised);
    }

    private void writeTeamInfo(ByteBuffer buffer, @NotNull ReadOnlyTeamState teamState)
    {
        buffer.put((byte)teamState.getTeamNumber());
        buffer.put(teamState.getTeamColor().getValue());
        buffer.put((byte)teamState.getScore());
        buffer.put((byte)teamState.getPenaltyShotCount());
        buffer.putShort(teamState.getPenaltyShotFlags());

        if (league.settings().isCoachAvailable) {
            buffer.put(teamState.getCoachMessage());
            writePlayerInfo(buffer, teamState.getCoach());
        } else {
            buffer.put(new byte[SPLCoachMessage.SIZE]);
            writePlayerInfo(buffer, null);
        }

        for (int uniformNumber = 1; uniformNumber <= NUM_PLAYERS_IN_GAME_STATE_MESSAGE; uniformNumber++) {
            writePlayerInfo(buffer,
                            uniformNumber <= teamState.getPlayerCount()
                                ? teamState.getPlayer(uniformNumber)
                                : null);
        }
    }

    private static void writePlayerInfo(ByteBuffer buffer, @Nullable ReadOnlyPlayerState playerState)
    {
        if (playerState == null) {
            buffer.put((byte)0);
            buffer.put((byte)0);
        } else {
            buffer.put(playerState.getPenalty().getValue());
            buffer.put((byte)playerState.getRemainingPenaltyTime());
        }
    }
}
