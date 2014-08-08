package controller.net.protocol;

import common.annotations.NotNull;
import common.annotations.Nullable;
import data.*;
import rules.Rules;

import java.nio.ByteBuffer;

/**
 * Implements game state network protocol, version 8.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class GameStateProtocol8 extends GameStateProtocol
{
    public GameStateProtocol8()
    {
        super((byte) 8);
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
                (TeamInfo.MAX_NUM_PLAYERS + 1) * playerSize; // +1 for the coach

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
    public byte[] toBytes(GameState data)
    {
        ByteBuffer buffer = writeHeader();

        buffer.put(versionNumber);
        buffer.put(nextPacketNumber);
        buffer.put((byte)Rules.league.teamSize);
        buffer.put(data.playMode.getValue());
        buffer.put(data.firstHalf ? (byte)1 : 0);
        buffer.put(data.kickOffTeam == null ? 2 : data.kickOffTeam.getValue());
        buffer.put(data.period.getValue());
        // V8 sends '0' (blue) when no drop in has occurred. This is addressed in V9.
        buffer.put(data.dropInTeam == null ? 0 : data.dropInTeam.getValue());
        buffer.putShort(data.dropInTime);
        buffer.putShort(data.secsRemaining);
        buffer.putShort(data.secondaryTime);

        for (TeamInfo team : data.team) {
            writeTeamInfo(buffer, team);
        }

        return buffer.array();
    }

    @Nullable
    @Override
    public GameState fromBytes(ByteBuffer buffer)
    {
        if (!verifyHeader(buffer))
            return null;

        GameState data = new GameState();

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
            t.coach.penalty = Penalty.fromValue(buffer.get());
            t.coach.secsTillUnpenalised = buffer.get();
            for (PlayerInfo p : t.player) {
                p.penalty = Penalty.fromValue(buffer.get());
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

        for (int i=0; i< TeamInfo.MAX_NUM_PLAYERS; i++) {
            writePlayerInfo(buffer, teamInfo.player[i]);
        }
    }

    private static void writePlayerInfo(ByteBuffer buffer, PlayerInfo playerInfo)
    {
        buffer.put(playerInfo.penalty.getValue());
        buffer.put(playerInfo.secsTillUnpenalised);
    }
}
