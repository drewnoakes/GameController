package controller.net.protocol;

import data.GameControlData;
import data.TeamInfo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Implements network protocol version 7.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class NetworkProtocol7 extends NetworkProtocol
{
    public NetworkProtocol7()
    {
        super((byte)7);
    }

    @Override
    public int getMessageSize()
    {
        final int playerSize =
                2 + // penalty
                2;  // secsToUnpenalize

        final int teamSize =
                1 + // teamNumber
                1 + // teamColor
                1 + // goal color
                1 + // score
                (TeamInfo.MAX_NUM_PLAYERS) * playerSize;

        return  4 + // header
                4 + // version
                1 + // numPlayers
                1 + // gameState
                1 + // firstHalf
                1 + // kickOffTeam
                1 + // secGameState
                1 + // dropInTeam
                2 + // dropInTime
                4 + // secsRemaining
                2 * teamSize;
    }

    @Override
    public byte[] toBytes(GameControlData data)
    {
        ByteBuffer buffer = ByteBuffer.allocate(getMessageSize());
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        buffer.put(GameControlData.GAMECONTROLLER_STRUCT_HEADER.getBytes(), 0, 4);
        buffer.putInt(versionNumber);
        buffer.put(data.playersPerTeam);
        buffer.put(data.gameState);
        buffer.put(data.firstHalf);
        buffer.put(data.kickOffTeam);
        buffer.put(data.secGameState);
        buffer.put(data.dropInTeam);
        buffer.putShort(data.dropInTime);
        buffer.putInt(data.secsRemaining);

        // in version 7, team data was sorted by team color
        if (data.team[0].teamColor == GameControlData.TEAM_BLUE) {
            writeTeamInfo(buffer, data.team[0]);
            writeTeamInfo(buffer, data.team[1]);
        } else {
            writeTeamInfo(buffer, data.team[1]);
            writeTeamInfo(buffer, data.team[0]);
        }

        return buffer.array();
    }

    @Override
    public GameControlData fromBytes(ByteBuffer buffer)
    {
        throw new AssertionError("Not implemented as no use for parsing version 7 messages is known of.");
    }

    private static void writeTeamInfo(ByteBuffer buffer, TeamInfo teamInfo)
    {
        buffer.put(teamInfo.teamNumber);
        buffer.put(teamInfo.teamColor);
        buffer.put((byte) 1); // goal color is always yellow
        buffer.put(teamInfo.score);

        // Write player data
        for (int i=0; i < TeamInfo.MAX_NUM_PLAYERS; i++) {
            buffer.putShort(teamInfo.player[i].penalty);
            buffer.putShort(teamInfo.player[i].secsTillUnpenalised);
        }
    }
}
