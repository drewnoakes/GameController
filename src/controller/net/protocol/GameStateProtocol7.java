package controller.net.protocol;

import common.annotations.NotNull;
import common.annotations.Nullable;
import data.GameStateSnapshot;
import data.League;
import data.TeamColor;
import data.TeamState;

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
                (TeamState.NUM_PLAYERS_IN_GAME_STATE_MESSAGE) * playerSize;

        return  4 + // header
                4 + // version
                1 + // numPlayers
                1 + // playMode
                1 + // firstHalf
                1 + // nextKickOffColor
                1 + // period
                1 + // lastDropInColor
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
        buffer.put(state.nextKickOffColor == null ? 2 : state.nextKickOffColor.getValue());
        buffer.put(state.period.getValue());
        // V7 sends '0' (blue) when no drop in has occurred. This is addressed in V9.
        buffer.put(state.lastDropInColor == null ? 0 : state.lastDropInColor.getValue());
        buffer.putShort(state.dropInTime);
        buffer.putInt(state.secsRemaining);

        // In version 7, team data was sorted by team color
        if (state.teams[0].teamColor == TeamColor.Blue) {
            writeTeamInfo(buffer, state.teams[0]);
            writeTeamInfo(buffer, state.teams[1]);
        } else {
            writeTeamInfo(buffer, state.teams[1]);
            writeTeamInfo(buffer, state.teams[0]);
        }

        return buffer.array();
    }

    @Nullable
    @Override
    public GameStateSnapshot fromBytes(@NotNull ByteBuffer buffer)
    {
        throw new AssertionError("Not implemented as no use for parsing version 7 messages is known of.");
    }

    private static void writeTeamInfo(ByteBuffer buffer, TeamState teamState)
    {
        buffer.put((byte)teamState.teamNumber);
        buffer.put(teamState.teamColor.getValue());
        buffer.put((byte) 1); // goal color is always yellow
        buffer.put(teamState.score);

        // Write player data
        for (int i=0; i < TeamState.NUM_PLAYERS_IN_GAME_STATE_MESSAGE; i++) {
            buffer.putShort(teamState.player[i].penalty.getValue());
            buffer.putShort(teamState.player[i].secsTillUnpenalised);
        }
    }
}
