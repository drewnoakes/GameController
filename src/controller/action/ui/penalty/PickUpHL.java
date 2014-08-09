package controller.action.ui.penalty;

import common.annotations.NotNull;
import data.GameState;
import data.Penalty;
import data.PlayerInfo;

/**
 *
 * @author Michel-Zen
 */
public class PickUpHL extends PenaltyAction
{
    @Override
    public void performOn(@NotNull GameState state, @NotNull PlayerInfo player, int side, int number)
    {
        if (player.penalty == Penalty.None) {
            state.whenPenalized[side][number] = state.getTime();
        }

        player.penalty = Penalty.HLPickupOrIncapable;
        log(state, null, "Request for PickUp / Incapable Player " + state.team[side].teamColor + " " + (number+1));
    }
}
