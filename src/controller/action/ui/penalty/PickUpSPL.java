package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.Game;
import data.GameState;
import data.Penalty;
import data.PlayerInfo;

/**
 * This action means that the request for pickup penalty has been selected.
 *
 * @author Michel Bartsch
 */
public class PickUpSPL extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull GameState state, @NotNull PlayerInfo player, int side, int number)
    {
        if (player.penalty == Penalty.None) {
            state.whenPenalized[side][number] = state.getTime();
        }

        player.penalty = Penalty.SplRequestForPickup;
        game.pushState("Request for PickUp " + state.team[side].teamColor + " " + (number + 1));
    }
}
