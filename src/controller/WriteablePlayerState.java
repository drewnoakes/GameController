package controller;

import data.Penalty;

/**
 * A writeable view over the state of a player.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public interface WriteablePlayerState extends ReadOnlyPlayerState
{
    /** Sets the penalty to apply to this player. Pass {@link Penalty#None} to specify no penalty. */
    void setPenalty(Penalty penalty);

    /** Sets the time when the player penalized last, in milliseconds. */
    void setWhenPenalized(long whenPenalized);

    /** Sets whether this player has been ejected or not (SPL only). */
    void setEjected(boolean isEjected);
}