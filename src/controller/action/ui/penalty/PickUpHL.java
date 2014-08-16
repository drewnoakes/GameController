package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.Game;
import controller.GameState;
import data.Penalty;
import data.PlayerState;

/**
 *
 * @author Michel-Zen
 */
public class PickUpHL extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull GameState state, @NotNull PlayerState player, int side, int number)
    {
        if (player.penalty == Penalty.None) {
            state.whenPenalized[side][number] = state.getTime();
        }

        player.penalty = Penalty.HLPickupOrIncapable;
        game.pushState("Request for PickUp / Incapable Player " + state.team[side].teamColor + " " + (number + 1));
    }
}
