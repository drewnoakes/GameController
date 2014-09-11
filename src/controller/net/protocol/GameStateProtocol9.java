package controller.net.protocol;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.*;
import data.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements game state network protocol, version 9.
 *
 * <ul>
 *     <li>
 *         Adds field 'game ID' to the message, which can be used to prevent
 *         against problems seen when multiple game controllers are running.
 *     </li>
 *     <li>Adds a value indicating whether the game is a play-off (SPL) or knockout (HL) game.</li>
 *     <li>Omits SPL coach data from non-SPL games.</li>
 *     <li>
 *         Adds a byte indicating the league being played in. Allows teams that play in
 *         multiple leagues to behave accordingly (eg. both HL kid-size and teen-size).
 *     </li>
 *     <li>
 *         When no drop in has yet occurred, 'lastDropInColor' has value 2 instead of 0.
 *     </li>
 * </ul>
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class GameStateProtocol9 extends GameStateProtocol
{
    /**
     * The number of player entries in a game state message.
     * This may be more than the number of players allowed by the rules.
     */
    private static final byte NUM_PLAYERS_IN_GAME_STATE_MESSAGE = 11;

    /**
     * A number that uniquely identifies the game.
     *
     * Can be used by robots to defend against problems seen when multiple game controllers
     * are running.
     */
    private final int gameId;

    private final League league;

    public GameStateProtocol9(@NotNull League league, int gameId)
    {
        super((byte) 9);
        this.league = league;
        this.gameId = gameId;
    }

    @Override
    public int getMessageSize()
    {
        final boolean hasCoach = this.league.isSPLFamily();

        final int playerSize =
                1 + // penalty
                1;  // secsToUnpenalize

        final int playerCount = NUM_PLAYERS_IN_GAME_STATE_MESSAGE + (hasCoach ? 1 : 0);

        final int teamSize =
                1 + // teamNumber
                1 + // teamColor
                1 + // score
                1 + // penaltyShot
                2 + // singleShots
                (hasCoach ? SPLCoachMessage.SIZE : 0) + // coach's message
                playerCount * playerSize; // player data

        return  4 + // header
                1 + // version
                1 + // league number
                1 + // packet number
                4 + // game ID
                1 + // numPlayers
                1 + // playMode
                1 + // firstHalf
                1 + // nextKickOffColor
                1 + // period
                1 + // lastDropInColor
                1 + // isKnockOutGame
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
        buffer.put(league.number());
        buffer.put(nextPacketNumber);
        buffer.put((byte)league.settings().teamSize);
        buffer.putInt(gameId);
        buffer.put(state.getPlayMode().getValue());
        buffer.put(state.isFirstHalf() ? (byte)1 : 0);
        buffer.put(state.getNextKickOffColor() == null ? 2 : state.getNextKickOffColor().getValue());
        buffer.put(state.getPeriod().getValue());
        buffer.put(state.getLastDropInColor() == null ? 2 : state.getLastDropInColor().getValue());
        buffer.put(state.isPlayOff() ? (byte)1 : (byte)0);
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

        League league = League.findByNumber(buffer.get());

        // Ensure the message applies to the current league
        // TODO don't return null, and decode the message according to the advertised league
        if (league != this.league)
            return null;

        byte packetNumber = buffer.get();
        byte playersPerTeam = buffer.get();
        int gameId = buffer.getInt();
        PlayMode playMode = PlayMode.fromValue(buffer.get());
        boolean firstHalf = buffer.get() != 0;
        TeamColor nextKickOffColor = TeamColor.fromValue(buffer.get());
        Period period = Period.fromValue(buffer.get());
        TeamColor lastDropInColor = TeamColor.fromValue(buffer.get());
        boolean isDropInGame = buffer.get() != 0;
        short dropInTime = buffer.getShort();
        short secsRemaining = buffer.getShort();
        short secondaryTime = buffer.getShort();

        TeamStateSnapshot team1 = teamFromBytes(buffer);
        TeamStateSnapshot team2 = teamFromBytes(buffer);

        return new GameStateSnapshot(
                playMode, firstHalf, nextKickOffColor, period, lastDropInColor, dropInTime,
                secsRemaining, team1, team2, secondaryTime, gameId, league, packetNumber, playersPerTeam, isDropInGame);
    }

    @NotNull
    private TeamStateSnapshot teamFromBytes(@NotNull ByteBuffer buffer)
    {
        final boolean hasCoach = this.league.isSPLFamily();

        byte teamNumber = buffer.get();
        TeamColor teamColor = TeamColor.fromValue(buffer.get());
        byte score = buffer.get();
        byte penaltyShot = buffer.get();
        short singleShots = buffer.getShort();

        byte[] coachMessage = null;
        PlayerStateSnapshot coach = null;
        if (hasCoach) {
            coachMessage = new byte[SPLCoachMessage.SIZE];
            buffer.get(coachMessage);
            coach = playerFromBytes(buffer);
        }

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

    private void writeTeamInfo(@NotNull ByteBuffer buffer, @NotNull ReadOnlyTeamState teamState)
    {
        buffer.put((byte)teamState.getTeamNumber());
        buffer.put(teamState.getTeamColor().getValue());
        buffer.put((byte)teamState.getScore());
        buffer.put((byte)teamState.getPenaltyShotCount());
        buffer.putShort(teamState.getPenaltyShotFlags());

        final boolean hasCoach = this.league.isSPLFamily();

        if (hasCoach) {
            buffer.put(teamState.getCoachMessage());
            writePlayerInfo(buffer, teamState.getCoach());
        }

        for (int uniformNumber = 1; uniformNumber <= NUM_PLAYERS_IN_GAME_STATE_MESSAGE; uniformNumber++) {
            writePlayerInfo(buffer,
                    uniformNumber <= teamState.getPlayerCount()
                            ? teamState.getPlayer(uniformNumber)
                            : null);
        }
    }

    private static void writePlayerInfo(@NotNull ByteBuffer buffer, @Nullable ReadOnlyPlayerState playerState)
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
