package controller;

import common.annotations.NotNull;
import common.annotations.Nullable;
import data.TeamColor;

/**
 * A read-only view over a team's state.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public interface ReadOnlyTeamState
{
    /**
     * Gets the number of non-substituted robots in the team.
     * <p>
     * This number is the maximum number of robots allowed on the field at any time,
     * not the actual number of active robots (which may be fewer).
     */
    int getNumberOfRobotsInPlay();

    /** Gets the number that uniquely identifies this team within the league. */
    int getTeamNumber();

    /** Gets the name of this team. */
    @NotNull
    String getTeamName();

    /** Gets the colour of this team's uniform. */
    @NotNull
    TeamColor getTeamColor();

    /** Gets the team's current score (number of goals). */
    int getScore();

    /**
     * Gets a read-only view of the state of the specified player from this team.
     * <p>
     * To access the SPL coach's state, use {@link ReadOnlyTeamState#getCoach()}.
     *
     * @param uniformNumber the uniform number of the player, between 1 and the league-specific team size limit
     * @throws IllegalArgumentException <code>uniformNumber</code> is invalid.
     */
    @NotNull
    ReadOnlyPlayerState getPlayer(int uniformNumber);

    /**
     * Gets the number of players on the team, as specified by the league's rules.
     * <p>
     * Includes penalised players. Note that this is the maximum number of players,
     * and a team may actually have fewer than this during play.
     */
    int getPlayerCount();

    ////////////////////////// TIMEOUTS

    /** Gets whether this team is currently taking a time out. */
    boolean isTimeoutActive();

    /**
     * Gets the number of timeouts taken by the team.
     * May be reset to zero after each half, depending upon the league's rules.
     */
    boolean isTimeoutTaken();

    ////////////////////////// PENALTY SHOOTOUTS

    /** Gets the number of penalty shots taken by this team. */
    int getPenaltyShotCount();

    /**
     * Gets a bit-array indicating the outcome of penalty shots taken so far.
     * The first penalty result is stored in the LSB.
     */
    short getPenaltyShotFlags();

    /**
     * Gets the result of a specific penalty shot attempt.
     *
     * @param attemptIndex the index of the attempt, starting at zero.
     */
    boolean getPenaltyResult(int attemptIndex);

    ////////////////////////// SPL-SPECIFIC VALUES

    /** The number of pushes for this team (SPL only). */
    int getPushCount();

    /**
     * Gets the coach's state (SPL only).
     *
     * @throws AssertionError if no coach is used in this league.
     */
    @NotNull
    PlayerState getCoach();

    /** Gets the byte array payload of the last coach message (SPL only). */
    @Nullable
    byte[] getCoachMessage();
}
