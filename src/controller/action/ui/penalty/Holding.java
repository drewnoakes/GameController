package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.Game;
import controller.GameState;
import data.Penalty;
import data.PlayerInfo;

/**
 * This action means that the ball holding penalty has been selected.
 *
 * @author Michel Bartsch
 */
public class Holding extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull GameState state, @NotNull PlayerInfo player, int side, int number)
    {
        player.penalty = Penalty.SplBallHolding;
        state.whenPenalized[side][number] = state.getTime();
        game.pushState("Ball Holding " + state.team[side].teamColor + " " + (number + 1));
    }
}
