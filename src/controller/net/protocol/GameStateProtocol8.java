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
                (TeamInfo.NUM_PLAYERS_IN_GAME_STATE_MESSAGE + 1) * playerSize; // +1 for the coach

        return  4 + // header
                1 + // version
                1 + // packet number
                1 + // numPlayers
                1 + // playMode
                1 + // firstHalf
                1 + // kickOffTeam
                1 + // period
                1 + // dropInTeam
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
        buffer.put(state.kickOffTeam == null ? 2 : state.kickOffTeam.getValue());
        buffer.put(state.period.getValue());
        // V8 sends '0' (blue) when no drop in has occurred. This is addressed in V9.
        buffer.put(state.dropInTeam == null ? 0 : state.dropInTeam.getValue());
        buffer.putShort(state.dropInTime);
        buffer.putShort(state.secsRemaining);
        buffer.putShort(state.secondaryTime);

        for (TeamInfo team : state.team) {
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
        data.kickOffTeam = TeamColor.fromValue(buffer.get());
        data.period = Period.fromValue(buffer.get());
        data.dropInTeam = TeamColor.fromValue(buffer.get());
        data.dropInTime = buffer.getShort();
        data.secsRemaining = buffer.getShort();
        data.secondaryTime = buffer.getShort();

        for (TeamInfo t : data.team) {
            t.teamNumber = buffer.get();
            t.teamColor = TeamColor.fromValue(buffer.get());
            t.score = buffer.get();
            t.penaltyShot = buffer.get();
            t.singleShots = buffer.getShort();
            buffer.get(t.coachMessage);
            t.coach.penalty = Penalty.fromValue(league, buffer.get());
            t.coach.secsTillUnpenalised = buffer.get();
            for (PlayerInfo p : t.player) {
                p.penalty = Penalty.fromValue(league, buffer.get());
                p.secsTillUnpenalised = buffer.get();
            }
        }

        return data;
    }

    private static void writeTeamInfo(ByteBuffer buffer, TeamInfo teamInfo)
    {
        buffer.put(teamInfo.teamNumber);
        buffer.put(teamInfo.teamColor.getValue());
        buffer.put(teamInfo.score);
        buffer.put(teamInfo.penaltyShot);
        buffer.putShort(teamInfo.singleShots);
        buffer.put(teamInfo.coachMessage);

        writePlayerInfo(buffer, teamInfo.coach);

        for (int i = 0; i < TeamInfo.NUM_PLAYERS_IN_GAME_STATE_MESSAGE; i++) {
            writePlayerInfo(buffer, teamInfo.player[i]);
        }
    }

    private static void writePlayerInfo(ByteBuffer buffer, PlayerInfo playerInfo)
    {
        buffer.put(playerInfo.penalty.getValue());
        buffer.put(playerInfo.secsTillUnpenalised);
    }
}
