package controller.net.protocol;

import data.GameControlData;
import data.PlayerInfo;
import data.SPLCoachMessage;
import data.TeamInfo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

/**
 * Implements network protocol version 9.
 *
 * Adds the 'game controller ID' to the message, which can be used to prevent against problems seen
 * when multiple game controllers are running.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class NetworkProtocol9 extends NetworkProtocol
{
    /**
     * A unique number for this game controller instance.
     *
     * Can be used by robots to defend against problems seen when multiple game controllers
     * are running.
     */
    private int gameControllerId;

    public NetworkProtocol9()
    {
        super((byte) 9);

        gameControllerId = new Random().nextInt();
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
                1 + // gameState
                1 + // firstHalf
                1 + // kickOffTeam
                1 + // secGameState
                1 + // dropInTeam
                2 + // dropInTime
                2 + // secsRemaining
                2 + // secondaryTime
                2 * teamSize +
                4;  // game controller ID
    }

    @Override
    public byte[] toBytes(GameControlData data)
    {
        ByteBuffer buffer = ByteBuffer.allocate(getMessageSize());
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        buffer.put(GameControlData.GAMECONTROLLER_STRUCT_HEADER.getBytes(), 0, 4);
        buffer.put(versionNumber);
        buffer.put(packetNumber);
        buffer.put(data.playersPerTeam);
        buffer.put(data.gameState);
        buffer.put(data.firstHalf);
        buffer.put(data.kickOffTeam);
        buffer.put(data.secGameState);
        buffer.put(data.dropInTeam);
        buffer.putShort(data.dropInTime);
        buffer.putShort(data.secsRemaining);
        buffer.putShort(data.secondaryTime);

        for (TeamInfo team : data.team) {
            writeTeamInfo(buffer, team);
        }

        buffer.putInt(gameControllerId);

        return buffer.array();
    }

    @Override
    public GameControlData fromBytes(ByteBuffer buffer)
    {
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        GameControlData data = new GameControlData();

        byte[] header = new byte[4];
        buffer.get(header, 0, 4);

        // TODO validate header contains correct data

        if (buffer.get() != versionNumber) {
            return null;
        }

        buffer.get(); // packet number (ignored when decoding)

        data.playersPerTeam = buffer.get();
        data.gameState = buffer.get();
        data.firstHalf = buffer.get();
        data.kickOffTeam = buffer.get();
        data.secGameState = buffer.get();
        data.dropInTeam = buffer.get();
        data.dropInTime = buffer.getShort();
        data.secsRemaining = buffer.getShort();
        data.secondaryTime = buffer.getShort();

        for (TeamInfo t : data.team) {
            t.teamNumber = buffer.get();
            t.teamColor = buffer.get();
            t.score = buffer.get();
            t.penaltyShot = buffer.get();
            t.singleShots = buffer.getShort();
            buffer.get(t.coachMessage);
            t.coach.penalty = buffer.get();
            t.coach.secsTillUnpenalised = buffer.get();
            for (PlayerInfo p : t.player) {
                p.penalty = buffer.get();
                p.secsTillUnpenalised = buffer.get();
            }
        }

        buffer.getInt(); // game controller ID (ignored when decoding)

        return data;
    }

    private static void writeTeamInfo(ByteBuffer buffer, TeamInfo teamInfo)
    {
        buffer.put(teamInfo.teamNumber);
        buffer.put(teamInfo.teamColor);
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
        buffer.put(playerInfo.penalty);
        buffer.put(playerInfo.secsTillUnpenalised);
    }
}
