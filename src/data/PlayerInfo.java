package data;

import java.io.Serializable;

/**
 * Models the state of a player at a given moment.
 * <p/>
 * This class's representation is independent of any particular network protocol, though in
 * practice there are many similarities.
 *
 * @author Michel Bartsch
 * @author Drew Noakes https://drewnoakes.com
 */
public class PlayerInfo implements Serializable
{
    /** The penalty state of the player */
    public Penalty penalty = Penalty.None;
    /** Estimate of remaining penalty time for player. */
    public byte secsTillUnpenalised;

    @Override
    public String toString()
    {
        return "----------------------------------------\n"
            + "            penalty: " + penalty + '\n'
            + "secsTillUnpenalised: " + secsTillUnpenalised + '\n';
    }
}
