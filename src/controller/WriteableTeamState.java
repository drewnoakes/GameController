package controller;

import common.annotations.NotNull;
import common.annotations.Nullable;
import data.Penalty;
import data.SPLCoachMessage;
import data.TeamColor;

/**
 * A writeable view over the state of a team.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public interface WriteableTeamState extends ReadOnlyTeamState
{
    /**
     * Sets the uniform color in use by this team.
     * <p>
     * Note that in some game configurations, the team colors may be swapped at half time.
     */
    void setTeamColor(TeamColor color);

    /**
     * Gets a writeable view of the state of the specified player from this team.
     * <p>
     * To access the SPL coach's state, use {@link ReadOnlyTeamState#getCoach()}.
     *
     * @param uniformNumber the uniform number of the player, between 1 and the league-specific team size limit
     * @throws IllegalArgumentException <code>uniformNumber</code> is invalid.
     */
    @NotNull
    WriteablePlayerState getPlayer(int uniformNumber);

    /**
     * Sets the current score (number of goals) for this team.
     *
     * @param score the team's score.
     * @throws IllegalArgumentException score is negative.
     */
    void setScore(int score);

    ////////////////////////// TIMEOUTS

    /** Sets whether a timeout taken by this team is currently active. */
    void setTimeoutActive(boolean isTimeoutActive);

    /**
     * Sets whether this team has taken a timeout.
     * <p>
     * This value is used when a limit to the number of timeouts is required in a given
     * portion of the game. Once the team may take another timeout, this flag is cleared.
     */
    void setTimeoutTaken(boolean taken);

    ////////////////////////// PENALTY SHOOTOUTS

    /**
     * Sets the number of penalty shots this team has taken.
     * Only used during {@link data.Period#PenaltyShootout}.
     */
    void setPenaltyShotCount(int penaltyShotCount);

    /** Indicates that the team successfully scored a goal during their penalty strike. */
    void addPenaltyGoal();

    ////////////////////////// PLAYER PENALTIES

    // TODO does this actually have to be a queue? Isn't it only possible to ever have one item in the queue?

    /**
     * Enqueues details about a penalty such as the type, and time given.
     * <p>
     * Penalties may be de-queued via {@link WriteableTeamState#popQueuedPenalty()}.
     * This mechanism supports the transferal of penalties between players within
     * a team when substitutions are made.
     */
    void enqueuePenalty(long whenPenalized, Penalty penalty);

    /** Pops queued penalty details. Returns <code>null</code> if no penalty exists in the queue. */
    @Nullable
    TeamState.QueuedPenalty popQueuedPenalty();

    ////////////////////////// SPL-SPECIFIC VALUES

    /**
     * Handles an incoming coach message (SPL only).
     * <p>
     * If this message comes too quickly (within {@link SPLCoachMessage#SPL_COACH_MESSAGE_RECEIVE_INTERVAL}) then
     * it will be ignored. Otherwise it will be stored for a randomly chosen period of time (between
     * {@link data.SPLCoachMessage#SPL_COACH_MESSAGE_MIN_SEND_INTERVAL} and
     * {@link data.SPLCoachMessage#SPL_COACH_MESSAGE_MAX_SEND_INTERVAL}) before becoming available via
     * {@link ReadOnlyTeamState#getCoachMessage()}.
     *
     * @param message the received message.
     */
    void receiveSplCoachMessage(@NotNull SPLCoachMessage message);

    /** Sets the number of pushes called on this team (SPL only). */
    void setPushCount(int pushCount);
}
