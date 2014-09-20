package controller;

import common.annotations.NotNull;
import data.Penalty;

/**
 * A read-only view over a player's state.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public interface ReadOnlyPlayerState
{
    /** Gets the player's uniform number. The SPL coach will return -1. */
    int getUniformNumber();

    /** The penalty applied to this player. May be {@link data.Penalty#None}. */
    @NotNull
    Penalty getPenalty();

    /**
     * Gets whether a penalty applies to this player or not.
     * <p>
     * If <code>false</code>, then {@link ReadOnlyPlayerState#getPenalty()} returns {@link Penalty#None}.
     */
    boolean isPenalized();

    /** Estimate of remaining penalty time for player. */
    int getRemainingPenaltyTime();

    /** Gets the time at which a penalty was last set on this player. If no penalty so far, the value will be zero. */
    long getWhenPenalized();

    ////////////////////////// SPL-SPECIFIC VALUES

    /** Whether this player has been ejected (SPL only). */
    boolean isEjected();

    /** Gets whether this player is the coach (SPL only). */
    boolean isCoach();
}
