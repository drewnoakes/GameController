package controller.net.protocol;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.ReadOnlyGameState;
import controller.ReadOnlyPlayerState;
import controller.ReadOnlyTeamState;
import data.GameStateSnapshot;
import data.League;
import data.TeamColor;

import java.nio.ByteBuffer;

/**
 * Implements game state network protocol, version 7.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class GameStateProtocol7 extends GameStateProtocol
{
    /**
     * The number of player entries in a game state message.
     * This may be more than the number of players allowed by the rules.
     */
    private static final byte NUM_PLAYERS_IN_GAME_STATE_MESSAGE = 11;

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
                (NUM_PLAYERS_IN_GAME_STATE_MESSAGE) * playerSize;

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
    public byte[] toBytes(@NotNull ReadOnlyGameState state)
    {
        ByteBuffer buffer = writeHeader();

        buffer.putInt(getVersionNumber());
        buffer.put((byte)league.rules().getTeamSize());
        buffer.put(state.getPlayMode().getValue());
        buffer.put(state.isFirstHalf() ? (byte)1 : 0);
        buffer.put(state.getNextKickOffColor() == null ? 2 : state.getNextKickOffColor().getValue());
        buffer.put(state.getPeriod().getValue());
        // V7 sends '0' (blue) when no drop in has occurred. This is addressed in V9.
        buffer.put(state.getLastDropInColor() == null ? 0 : state.getLastDropInColor().getValue());
        buffer.putShort((short)state.getDropInTime());
        buffer.putInt(state.getSecsRemaining());

        // In version 7, team data was sorted by team color
        writeTeamInfo(buffer, state.getTeam(TeamColor.Blue));
        writeTeamInfo(buffer, state.getTeam(TeamColor.Red));

        return buffer.array();
    }

    @Nullable
    @Override
    public GameStateSnapshot fromBytes(@NotNull ByteBuffer buffer)
    {
        throw new AssertionError("Not implemented as no use for parsing version 7 messages is known of.");
    }

    private static void writeTeamInfo(ByteBuffer buffer, ReadOnlyTeamState teamState)
    {
        buffer.put((byte)teamState.getTeamNumber());
        buffer.put(teamState.getTeamColor().getValue());
        // Goal color (always yellow). This carries over from a time when goals had different colours.
        buffer.put((byte)1);
        buffer.put((byte)teamState.getScore());

        // Write player data
        for (int uniformNumber = 1; uniformNumber <= NUM_PLAYERS_IN_GAME_STATE_MESSAGE; uniformNumber++) {
            if (uniformNumber <= teamState.getPlayerCount()) {
                ReadOnlyPlayerState player = teamState.getPlayer(uniformNumber);
                buffer.putShort(player.getPenalty().getValue());
                buffer.putShort((short)player.getRemainingPenaltyTime());
            } else {
                // Write blank data for unused players
                buffer.putShort((short)0);
                buffer.putShort((short)0);
            }
        }
    }
}
