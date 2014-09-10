package controller;

import common.annotations.NotNull;
import common.annotations.Nullable;
import data.*;

/**
 * A writeable view over the state of a game.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public interface WriteableGameState extends ReadOnlyGameState
{
    /**
     * Gets a writeable view of the team state associated with the specified team number.
     *
     * @param teamNumber the team number.
     * @return the corresponding team, or <code>null</code> if the provided team number
     *         does not match a team playing in the current game.
     */
    @Nullable WriteableTeamState getTeam(int teamNumber);

    /**
     * Gets a writeable view of the team with the specified uniform color.
     *
     * @param teamColor the uniform color to look up the team by.
     * @return the team having the specified uniform color.
     */
    @NotNull WriteableTeamState getTeam(@NotNull TeamColor teamColor);

    /**
     * Gets a writeable view of the team on the specified side of the UI.
     * <p>
     * This does not necessarily correspond with the team's side of the field.
     *
     * @param side the side of the UI to look up the team by.
     * @return the team on the specified side of the UI.
     */
    @NotNull WriteableTeamState getTeam(@NotNull UISide side);

    ////////////////////////// CLOCK & TIMING

    /**
     * Add the time passed in the current play mode to the time that already passed before.
     * Is usually called during changes of the play mode.
     */
    void addTimeInCurrentPlayMode();

    void setTimeBeforeCurrentPlayMode(long timeBeforeCurrentPlayMode);

    void setWhenCurrentPlayModeBegan(long whenCurrentPlayModeBegan);

    void setWhenDropIn(long whenDropIn);

    void setManPause(boolean manPause);

    void setManPlay(boolean manPlay);

    void setManWhenClockChanged(long manWhenClockChanged);

    void setManTimeOffset(long manTimeOffset);

    void setManRemainingGameTimeOffset(long manRemainingGameTimeOffset);

    ////////////////////////// PENALTIES

    /**
     * Clears penalties of all non-coach and non-sub players in both teams, and clears ejected state.
     * Also calls {@link WriteableGameState#resetPenaltyTimes()}.
     */
    void resetPenalties();

    /** Resets the penalized-at-time of all players to 0. Does not unpenalize them. */
    void resetPenaltyTimes();

    ////////////////////////// PLAY MODE, PERIOD & HALF

    /** Sets the current play mode. */
    void setPlayMode(@NotNull PlayMode playMode);

    /** Sets the current period. */
    void setPeriod(@NotNull Period period);

    /**
     * Stores the previous period.
     * <p>
     * Note: Most callers of {@link WriteableGameState#setPeriod} also call this function, however
     * not all do.
     */
    void setPreviousPeriod(Period previousPeriod);

    /** Sets whether this is the first half or not. Only applies during {@link Period#Normal} and
     * {@link Period#Overtime} periods.
     *
     * @param firstHalf <code>true</code> for the first half, <code>false</code> for the second.
     */
    void setFirstHalf(boolean firstHalf);

    ////////////////////////// KICK OFF AND DROP IN

    /** Sets the color of the team having the next kick off. may be <code>null</code>. */
    void setNextKickOffColor(@Nullable TeamColor nextKickOffColor);

    /** Stores the color of the last team to have a drop in. May be <code>null</code>. */
    void setLastDropInColor(@Nullable TeamColor lastDropInColor);

    ////////////////////////// MISCELLANEOUS

    /** Sets whether test mode is currently active or not. */
    void setTestMode(boolean testmode);

    ////////////////////////// SPL-SPECIFIC VALUES

    /** Sets whether a referee timeout is active or not. */
    void setRefereeTimeoutActive(boolean refereeTimeout);
}
