package controller.net.protocol;

import common.annotations.NotNull;
import common.annotations.Nullable;
import data.*;

import java.nio.ByteBuffer;

/**
 * Implements game state network protocol, version 8.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class GameStateProtocol8 extends GameStateProtocol
{
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
                SPLCoachMessage.SPL_COACH_MESSAGE_SIZE + // coach's message
                (TeamState.NUM_PLAYERS_IN_GAME_STATE_MESSAGE + 1) * playerSize; // +1 for the coach

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
    public byte[] toBytes(@NotNull GameStateSnapshot state)
    {
        ByteBuffer buffer = writeHeader();

        buffer.put(getVersionNumber());
        buffer.put(nextPacketNumber);
        buffer.put((byte)league.settings().teamSize);
        buffer.put(state.playMode.getValue());
        buffer.put(state.firstHalf ? (byte)1 : 0);
        buffer.put(state.nextKickOffColor == null ? 2 : state.nextKickOffColor.getValue());
        buffer.put(state.period.getValue());
        // V8 sends '0' (blue) when no drop in has occurred. This is addressed in V9.
        buffer.put(state.lastDropInColor == null ? 0 : state.lastDropInColor.getValue());
        buffer.putShort(state.dropInTime);
        buffer.putShort(state.secsRemaining);
        buffer.putShort(state.secondaryTime);

        for (TeamState team : state.team) {
            writeTeamInfo(buffer, team);
        }

        return buffer.array();
    }

    @Nullable
    @Override
    public GameStateSnapshot fromBytes(@NotNull ByteBuffer buffer)
    {
        if (!verifyHeader(buffer))
            return null;

        GameStateSnapshot data = new GameStateSnapshot(null);

        buffer.get(); // packet number (ignored when decoding)
        buffer.get(); // players per team (ignored when decoding)
        data.playMode = PlayMode.fromValue(buffer.get());
        data.firstHalf = buffer.get() != 0;
        data.nextKickOffColor = TeamColor.fromValue(buffer.get());
        data.period = Period.fromValue(buffer.get());
        data.lastDropInColor = TeamColor.fromValue(buffer.get());
        data.dropInTime = buffer.getShort();
        data.secsRemaining = buffer.getShort();
        data.secondaryTime = buffer.getShort();

        for (TeamState t : data.team) {
            t.teamNumber = buffer.get();
            t.teamColor = TeamColor.fromValue(buffer.get());
            t.score = buffer.get();
            t.penaltyShot = buffer.get();
            t.singleShots = buffer.getShort();
            buffer.get(t.coachMessage);
            t.coach.penalty = Penalty.fromValue(league, buffer.get());
            t.coach.secsTillUnpenalised = buffer.get();
            for (PlayerState p : t.player) {
                p.penalty = Penalty.fromValue(league, buffer.get());
                p.secsTillUnpenalised = buffer.get();
            }
        }

        return data;
    }

    private static void writeTeamInfo(ByteBuffer buffer, TeamState teamState)
    {
        buffer.put((byte)teamState.teamNumber);
        buffer.put(teamState.teamColor.getValue());
        buffer.put(teamState.score);
        buffer.put(teamState.penaltyShot);
        buffer.putShort(teamState.singleShots);
        buffer.put(teamState.coachMessage);

        writePlayerInfo(buffer, teamState.coach);

        for (int i = 0; i < TeamState.NUM_PLAYERS_IN_GAME_STATE_MESSAGE; i++) {
            writePlayerInfo(buffer, teamState.player[i]);
        }
    }

    private static void writePlayerInfo(ByteBuffer buffer, PlayerState playerState)
    {
        buffer.put(playerState.penalty.getValue());
        buffer.put(playerState.secsTillUnpenalised);
    }
}
