package data;

import common.annotations.NotNull;
import common.annotations.Nullable;
import rules.Rules;

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
    /** Which team has the next kick off. If null, then the next kick off will be a drop ball. */
    @Nullable
    public TeamColor kickOffTeam = TeamColor.Blue;
    /** The type of active game period (normal, overtime, penalties, timeout). */
    public Period period = Period.Normal;
    /** Team that caused last drop in. If no drop in has occurred yet, will be null. */
    @Nullable
    public TeamColor dropInTeam;
    /** The number of seconds that have passed since the last drop in. Will be -1 before first drop in. */
    public short dropInTime = -1;
    /** An estimate of the number of seconds remaining in the current half. */
    public short secsRemaining = (short) Rules.league.halfTime;
    /**
     * Play-mode-specific sub-time in seconds.
     *
     * For example, may reflect the ten second countdown during kickoff, or the number of seconds
     * remaining during 'ready' play mode, and so forth.
     */
    public short secondaryTime = 0;
    public TeamInfo[] team = new TeamInfo[2];

    /**
     * Creates a new, blank GameState.
     */
    public GameStateSnapshot()
    {
        for (int i=0; i<team.length; i++) {
            team[i] = new TeamInfo();
        }
        team[0].teamColor = TeamColor.Blue;
        team[1].teamColor = TeamColor.Red;
    }
    
    @Override
    public String toString()
    {
        return "           playMode: " + playMode + '\n' +
               "          firstHalf: " + (firstHalf ? "true" : "false") + '\n' +
               "        kickOffTeam: " + kickOffTeam + '\n' +
               "             period: " + period + '\n' +
               "         dropInTeam: " + dropInTeam + '\n' +
               "         dropInTime: " + dropInTime + '\n' +
               "      secsRemaining: " + secsRemaining + '\n' +
               "      secondaryTime: " + secondaryTime + '\n';
    }
}
