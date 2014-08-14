package controller.net.protocol;

import common.annotations.NotNull;
import common.annotations.Nullable;
import data.GameStateSnapshot;
import data.League;
import data.TeamColor;
import data.TeamInfo;

import java.nio.ByteBuffer;

/**
 * Implements game state network protocol, version 7.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class GameStateProtocol7 extends GameStateProtocol
{
    private final League league;

    public GameStateProtocol7(@NotNull League league)
    {
        super((byte)7);

        this.league = league;
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
                (TeamInfo.NUM_PLAYERS_IN_GAME_STATE_MESSAGE) * playerSize;

        return  4 + // header
                4 + // version
                1 + // numPlayers
                1 + // playMode
                1 + // firstHalf
                1 + // kickOffTeam
                1 + // period
                1 + // dropInTeam
                2 + // dropInTime
                4 + // secsRemaining
                2 * teamSize;
    }

    @NotNull
    @Override
    public byte[] toBytes(@NotNull GameStateSnapshot state)
    {
        ByteBuffer buffer = writeHeader();

        buffer.putInt(getVersionNumber());
        buffer.put((byte)league.settings().teamSize);
        buffer.put(state.playMode.getValue());
        buffer.put(state.firstHalf ? (byte)1 : 0);
        buffer.put(state.kickOffTeam == null ? 2 : state.kickOffTeam.getValue());
        buffer.put(state.period.getValue());
        // V7 sends '0' (blue) when no drop in has occurred. This is addressed in V9.
        buffer.put(state.dropInTeam == null ? 0 : state.dropInTeam.getValue());
        buffer.putShort(state.dropInTime);
        buffer.putInt(state.secsRemaining);

        // In version 7, team data was sorted by team color
        if (state.team[0].teamColor == TeamColor.Blue) {
            writeTeamInfo(buffer, state.team[0]);
            writeTeamInfo(buffer, state.team[1]);
        } else {
            writeTeamInfo(buffer, state.team[1]);
            writeTeamInfo(buffer, state.team[0]);
        }

        return buffer.array();
    }

    @Nullable
    @Override
    public GameStateSnapshot fromBytes(@NotNull ByteBuffer buffer)
    {
        throw new AssertionError("Not implemented as no use for parsing version 7 messages is known of.");
    }

    private static void writeTeamInfo(ByteBuffer buffer, TeamInfo teamInfo)
    {
        buffer.put(teamInfo.teamNumber);
        buffer.put(teamInfo.teamColor.getValue());
        buffer.put((byte) 1); // goal color is always yellow
        buffer.put(teamInfo.score);

        // Write player data
        for (int i=0; i < TeamInfo.NUM_PLAYERS_IN_GAME_STATE_MESSAGE; i++) {
            buffer.putShort(teamInfo.player[i].penalty.getValue());
            buffer.putShort(teamInfo.player[i].secsTillUnpenalised);
        }
    }
}
