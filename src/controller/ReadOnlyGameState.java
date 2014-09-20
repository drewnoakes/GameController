package controller;

import common.annotations.NotNull;
import common.annotations.Nullable;
import data.Period;
import data.PlayMode;
import data.TeamColor;
import data.UISide;

/**
 * A read-only view over a game's state.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public interface ReadOnlyGameState
{
    ////////////////////////// TEAM LOOKUP

    /**
     * Gets a read-only view of the team state associated with the specified team number.
     *
     * @param teamNumber the team number.
     * @return the corresponding team, or <code>null</code> if the provided team number
     *         does not match a team playing in the current game.
     */
    @Nullable
    ReadOnlyTeamState getTeam(int teamNumber);

    /**
     * Gets a read-only view of the team with the specified uniform color.
     *
     * @param teamColor the uniform color to look up the team by.
     * @return the team having the specified uniform color.
     */
    @NotNull
    ReadOnlyTeamState getTeam(@NotNull TeamColor teamColor);

    /**
     * Gets a read-only view of the team on the specified side of the UI.
     * <p>
     * This does not necessarily correspond with the team's side of the field.
     *
     * @param side the side of the UI to look up the team by.
     * @return the team on the specified side of the UI.
     */
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

    /** Gets whether the current period is one from the specified list. */
    boolean is(@NotNull Period... periods);

    /** Whether the game is currently in the first half. Applies to both normal time and overtime. */
    boolean isFirstHalf();

    /** Play mode of the game. */
    @NotNull
    PlayMode getPlayMode();

    /** Gets whether the current play mode is one from the specified list. */
    boolean is(@NotNull PlayMode... playModes);

    /** Gets whether the score of both teams are level (equal). */
    boolean areScoresLevel();

    /** Gets whether a timeout is currently active for either team. Does not include SPL referee timeouts. */
    boolean isTimeoutActive();

    /** Gets whether test mode is currently active. */
    boolean isTestMode();

    /**
     * Gets the color of the team having the next kick off, or <code>null</code> when
     * no team gets the kick off (ie. a drop ball).
     */
    @Nullable
    TeamColor getNextKickOffColor();

    /**
     * Gets the color of the team that caused last drop in.
     * <p>
     * If no drop in has occurred yet, will be <code>null</code>.
     */
    @Nullable
    TeamColor getLastDropInColor();

    /**
     * Gets a random number that uniquely identifies the current game.
     * <p>
     * May be used to detect and protect against problems related to multiple game controllers
     * running on the same network.
     */
    int getGameId();

    /** Gets whether this game is a play-off (SPL) or knock-out (HL) game. */
    boolean isPlayOff();

    ////////////////////////// SPL-SPECIFIC VALUES

    /** Gets whether a referee timeout is currently active or not (SPL only). */
    boolean isRefereeTimeoutActive();
}
