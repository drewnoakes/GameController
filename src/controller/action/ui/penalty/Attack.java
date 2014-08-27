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
public class Attack extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull GameState state, @NotNull PlayerState player, int side, int number)
    {
        player.penalty = Penalty.HLIllegalAttack;
        state.whenPenalized[side][number] = state.getTime();
        game.pushState("Illegal Attack " + state.teams[side].teamColor + " " + (number + 1));
    }
}
