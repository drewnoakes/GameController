package controller;

import common.annotations.NotNull;
import common.annotations.Nullable;
import data.Period;
import data.PlayMode;
import data.TeamColor;
import data.UISide;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public interface ReadOnlyGameState
{
    ////////////////////////// TEAM LOOKUP

    @Nullable
    ReadOnlyTeamState getTeam(int teamNumber);

    @NotNull
    ReadOnlyTeamState getTeam(@NotNull TeamColor teamColor);

    @NotNull
    ReadOnlyTeamState getTeam(@NotNull UISide side);

    ////////////////////////// CLOCK & TIMING

    /**
     * Returns the current time. Can be stopped in test mode.
     * @return The current time in ms. May become incompatible to
     *         the time delivered by System.currentTimeMillis().
     */
    long getTime();

    /**
     * Returns the number of seconds since a certain timestamp.
     *
     * @param millis The timestamp in ms.
     * @return The number of seconds since the timestamp.
     */
    int getSecondsSince(long millis);

    /**
     * The number of seconds until a certain duration is over. The time
     * already passed is specified as a timestamp when it began.
     *
     * @param millis The timestamp in ms.
     * @param durationInSeconds The full duration in s.
     * @return The number of seconds that still remain from the duration. Can be negative.
     */
    int getRemainingSeconds(long millis, int durationInSeconds);

    /**
     * The method returns the remaining pause time.
     * @return The remaining number of seconds of the game pause or null if there currently is no pause.
     */
    Integer getRemainingPauseTime();

    /**
     * Calculates the remaining time a player is to stay penalized.
     */
    int getRemainingPenaltyTime(@NotNull ReadOnlyPlayerState player);

    /**
     * Determines the secondary time. Although this is a GUI feature, the secondary time
     * will also be encoded in the network packet.
     *
     * @param timeKickOffBlockedOvertime In case the kickOffBlocked time is delivered, this
     *                                   parameter specified how long negative values will
     *                                   be returned before the time is switched off.
     * @return The secondary time in seconds or null if there currently is none.
     */
    Integer getSecondaryTime(int timeKickOffBlockedOvertime);

    /** How much time summed up before the current play mode? (ms)*/
    long getTimeBeforeCurrentPlayMode();

    /** When was switched to the current play mode? (ms) */
    long getWhenCurrentPlayModeBegan();

    /** When was the last drop-in? (ms, 0 = never) */
    long getWhenDropIn();

    /** If true, the clock has manually been paused in the testmode. */
    boolean isManPause();

    /** If true, the clock has manually been started in the testmode. */
    boolean isManPlay();

    /** When was the last manual intervention to the clock? */
    long getManWhenClockChanged();

    /** Time offset resulting from manually stopping the clock. */
    long getManTimeOffset();

    /** Time offset resulting from starting the clock when it should be stopped. */
    long getManRemainingGameTimeOffset();

    /** The number of seconds that have passed since the last drop in. Will be -1 before first drop in. */
    int getDropInTime();

    /**
     * Calculates the remaining game time in the current phase of the game.
     * This is what the primary clock will show.
     * Measured in seconds.
     *
     * @return The remaining number of seconds.
     */
    int getSecsRemaining();

    /**
     * Play-mode-specific sub-time in seconds.
     *
     * For example, may reflect the ten second countdown during kickoff, or the number of seconds
     * remaining during 'ready' play mode, and so forth.
     */
    int getSecondaryTime();

    ////////////////////////// MISCELLANEOUS

    /** The type of active game period (normal, overtime, penalties, timeout). */
    @NotNull
    Period getPeriod();

    /** Used to backup the secondary game state during a timeout. */
    @NotNull
    Period getPreviousPeriod();

    /** Whether the game is currently in the first half. Applies to both normal time and overtime. */
    boolean isFirstHalf();

    /** Play mode of the game. */
    @NotNull
    PlayMode getPlayMode();

    /** Gets whether the score of both teams are level (equal). */
    boolean areScoresLevel();

    /** Gets whether a timeout is currently active for either team. */
    boolean isTimeOutActive();

    /** If true, left side has the kickoff. */
    boolean isLeftSideKickoff();

    /** If true, the testmode has been activated. */
    boolean isTestMode();

    /** Which team has the next kick off. If <code>null</code>, then the next kick off will be a drop ball. */
    @Nullable
    TeamColor getNextKickOffColor();

    /** Color of the team that caused last drop in. If no drop in has occurred yet, will be <code>null</code>. */
    @Nullable
    TeamColor getLastDropInColor();

    int getGameId();

    boolean isPlayOff();

    ////////////////////////// SPL-SPECIFIC VALUES

    /** If true, the referee set a timeout (SPL only). */
    boolean isRefereeTimeout();
}
