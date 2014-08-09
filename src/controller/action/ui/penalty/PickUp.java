package controller.action.ui.penalty;

import common.annotations.NotNull;
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
    public void performOn(@NotNull GameState state, @NotNull PlayerInfo player, int side, int number)
    {
        if (player.penalty == Penalty.None) {
            state.whenPenalized[side][number] = state.getTime();
        }

        player.penalty = Penalty.SplRequestForPickup;
        log(state, null, "Request for PickUp " + state.team[side].teamColor + " " + (number+1));
    }
}
