package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.Game;
import controller.GameState;
import data.Penalty;
import data.PlayerState;

/**
 * This action means that the playing with hands penalty has been selected.
 *
 * @author Michel Bartsch
 */
public class Hands extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull GameState state, @NotNull PlayerState player, int side, int number)
    {
        player.penalty = Penalty.SplPlayingWithHands;
        state.whenPenalized[side][number] = state.getTime();
        game.pushState("Playing with Hands " + state.teams[side].teamColor + " " + (number + 1));
    }
}
