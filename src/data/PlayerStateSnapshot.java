package data;

import common.annotations.NotNull;

/**
 * Immutable model of a player's state, as received from a Game Controller.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class PlayerStateSnapshot
{
    /** The penalty applied to this player. May be {@link data.Penalty#None}. */
    public final Penalty penalty;

    /** Estimate of remaining penalty time for player. */
    public final byte secondsTillUnpenalised;

    public PlayerStateSnapshot(@NotNull Penalty penalty, byte secondsTillUnpenalised)
    {
        this.penalty = penalty;
        this.secondsTillUnpenalised = secondsTillUnpenalised;
    }
}
