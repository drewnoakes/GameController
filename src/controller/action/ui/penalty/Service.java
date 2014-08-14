package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.Game;
import controller.GameState;
import data.Penalty;
import data.PlayerInfo;

/**
 *
 * @author Daniel Seifert
 */
public class Service extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull GameState state, @NotNull PlayerInfo player, int side, int number)
    {
        if (player.penalty == Penalty.None) {
            state.whenPenalized[side][number] = state.getTime();
            player.penalty = Penalty.Service;
            game.pushState("Request for Service " + state.team[side].teamColor + " " + (number + 1));
        } else {
            player.penalty = Penalty.Service;
            game.pushState("Additional Request for Service " + state.team[side].teamColor + " " + (number + 1));
        }
    }
}
