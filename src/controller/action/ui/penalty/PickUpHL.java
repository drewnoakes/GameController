package controller.action.ui.penalty;

import common.Log;
import data.GameState;
import data.Penalty;
import data.PlayerInfo;

/**
 *
 * @author Michel-Zen
 */
public class PickUpHL extends PenaltyAction
{
    /**
     * Performs this action`s penalty on a selected player.
     *
     * @param data      The current data to work on.
     * @param player    The player to penalise.
     * @param side      The side the player is playing on (0:left, 1:right).
     * @param number    The player`s number, beginning with 0!
     */
    @Override
    public void performOn(GameState data, PlayerInfo player, int side, int number)
    {
        if (player.penalty == Penalty.None) {
            data.whenPenalized[side][number] = data.getTime();
        }

        player.penalty = Penalty.HLPickupOrIncapable;
        Log.state(data, "Request for PickUp / Incapable Player " + data.team[side].teamColor+ " " + (number+1));
    }

    /**
     * Checks if this action is legal with the given data (model).
     * Illegal actions are not performed by the EventHandler.
     *
     * @param data      The current data to check with.
     */
    @Override
    public boolean isLegal(GameState data)
    {
        return true;
    }
}
