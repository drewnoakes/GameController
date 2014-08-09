package controller.action.ui.penalty;

import common.annotations.NotNull;
import data.GameState;
import data.Penalty;
import data.PlayerInfo;

/**
 *
 * @author Daniel Seifert
 */
public class ServiceHL extends PickUp
{
    @Override
    public void performOn(@NotNull GameState state, @NotNull PlayerInfo player, int side, int number)
    {
        if (player.penalty == Penalty.None) {
            state.whenPenalized[side][number] = state.getTime();
            player.penalty = Penalty.HLService;
            log(state, null, "Request for Service " + state.team[side].teamColor + " " + (number+1));
        } else {
            player.penalty = Penalty.HLService;
            log(state, null, "Additional Request for Service " + state.team[side].teamColor + " " + (number+1));
        }
    }
}
