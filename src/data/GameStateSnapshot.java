package data;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.Game;

import java.io.Serializable;


/**
 * Models the state of the game at a given moment.
 *
 * This class's representation is independent of any particular network protocol, though in
 * practice there are many similarities.
 *
 * @author Michel Bartsch
 * @author Drew Noakes https://drewnoakes.com
 */
public class GameStateSnapshot implements Serializable
{
    /** Play mode of the game. */
    @NotNull
    public PlayMode playMode = PlayMode.Initial;
    /** Whether the game is currently in the first half. Applies to both normal time and overtime. */
    public boolean firstHalf = true;
    /** Which team has the next kick off. If <code>null</code>, then the next kick off will be a drop ball. */
    @Nullable
    public TeamColor nextKickOffColor = TeamColor.Blue;
    /** The type of active game period (normal, overtime, penalties, timeout). */
    public Period period = Period.Normal;
    /** Team that caused last drop in. If no drop in has occurred yet, will be <code>null</code>. */
    @Nullable
    public TeamColor lastDropInColor;
    /** The number of seconds that have passed since the last drop in. Will be -1 before first drop in. */
    public short dropInTime = -1;
    /** An estimate of the number of seconds remaining in the current half. */
    public short secsRemaining;
    /**
     * Play-mode-specific sub-time in seconds.
     *
     * For example, may reflect the ten second countdown during kickoff, or the number of seconds
     * remaining during 'ready' play mode, and so forth.
     */
    public short secondaryTime = 0;
    public final TeamState[] teams = new TeamState[2];

    /**
     * Creates a new, blank GameState.
     */
    public GameStateSnapshot(Game game)
    {
        teams[0] = new TeamState(game.teams().get(TeamColor.Blue).getNumber(), TeamColor.Blue);
        teams[1] = new TeamState(game.teams().get(TeamColor.Red).getNumber(), TeamColor.Red);
        secsRemaining = (short) game.settings().halfTime;
    }
    
    @Override
    public String toString()
    {
        return "           playMode: " + playMode + '\n' +
               "          firstHalf: " + (firstHalf ? "true" : "false") + '\n' +
               "   nextKickOffColor: " + nextKickOffColor + '\n' +
               "             period: " + period + '\n' +
               "    lastDropInColor: " + lastDropInColor + '\n' +
               "         dropInTime: " + dropInTime + '\n' +
               "      secsRemaining: " + secsRemaining + '\n' +
               "      secondaryTime: " + secondaryTime + '\n';
    }

    /** Gets the index of the specified team number, or -1 if the team number is unknown. */
    public int getTeamIndex(byte teamNumber)
    {
        return teamNumber == teams[0].teamNumber
                ? 0
                : teamNumber == teams[1].teamNumber
                    ? 1
                    : -1;
    }
}
