package controller;

import common.annotations.NotNull;
import data.Penalty;

/**
 * Models the state of a player at a given moment.
 *
 * <ul>
 *     <li>Implements both read-only and writeable interfaces.</li>
 *     <li>{@link PlayerState#clone()} produces an exact copy. This mechanism is used to enable 'undo' functionality.</li>
 * </ul>
 *
 * @author Michel Bartsch
 * @author Drew Noakes https://drewnoakes.com
 */
public class PlayerState implements WriteablePlayerState, ReadOnlyPlayerState
{
    // NOTE if a new field is added here, be sure to include it in the copy constructor

    @NotNull private final Game game;
    private final int uniformNumber;
    private final boolean isCoach;
    @NotNull private Penalty penalty;
    private long whenPenalized;
    private boolean isEjected;

    public PlayerState(@NotNull Game game, int uniformNumber, boolean isCoach)
    {
        this.game = game;
        this.uniformNumber = uniformNumber;
        this.isCoach = isCoach;

        this.penalty = Penalty.None;
    }

    /** Private copy constructor. */
    private PlayerState(@NotNull PlayerState source)
    {
        game = source.game;
        uniformNumber = source.uniformNumber;
        isCoach = source.isCoach;
        penalty = source.penalty;
        whenPenalized = source.whenPenalized;
        isEjected = source.isEjected;
    }

    @NotNull
    public PlayerState clone()
    {
        return new PlayerState(this);
    }
    
    @Override
    public int getUniformNumber()
    {
        return uniformNumber;
    }

    @Override
    @NotNull
    public Penalty getPenalty()
    {
        return penalty;
    }

    @Override
    public void setPenalty(@NotNull Penalty penalty)
    {
        this.penalty = penalty;
    }

    @Override
    public int getRemainingPenaltyTime()
    {
        return game.getGameState().getRemainingPenaltyTime(this);
    }

    @Override
    public boolean isEjected()
    {
        return isEjected;
    }

    @Override
    public void setEjected(boolean isEjected)
    {
        this.isEjected = isEjected;
    }

    @Override
    public long getWhenPenalized()
    {
        return whenPenalized;
    }

    @Override
    public void setWhenPenalized(long whenPenalized)
    {
        this.whenPenalized = whenPenalized;
    }

    @Override
    public boolean isCoach()
    {
        return isCoach;
    }
}
