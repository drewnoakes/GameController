package controller;

import data.Penalty;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public interface WriteablePlayerState extends ReadOnlyPlayerState
{
    void setPenalty(Penalty penalty);

    /** Sets the time when the player penalized last, in milliseconds. */
    void setWhenPenalized(long whenPenalized);

    void setEjected(boolean isEjected);
}