package controller.action.ui.penalty;

import common.Log;

import data.GameState;
import data.Penalty;
import data.PlayerInfo;

/**
 * This action means that the request for pickup penalty has been selected.
 *
 * @author Michel Bartsch
 */
public class PickUp extends PenaltyAction
{
    @Override
    public void performOn(GameState state, PlayerInfo player, int side, int number)
    {
        if (player.penalty == Penalty.None) {
            state.whenPenalized[side][number] = state.getTime();
        }

        player.penalty = Penalty.SplRequestForPickup;
        Log.state(state, "Request for PickUp " + state.team[side].teamColor + " " + (number+1));
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return true;
    }
}
