package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.Game;
import data.GameState;
import data.Penalty;
import data.PlayerInfo;

/**
 * This action means that the playing with hands penalty has been selected.
 *
 * @author Michel Bartsch
 */
public class Hands extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull GameState state, @NotNull PlayerInfo player, int side, int number)
    {
        player.penalty = Penalty.SplPlayingWithHands;
        state.whenPenalized[side][number] = state.getTime();
        game.pushState("Playing with Hands " + state.team[side].teamColor + " " + (number + 1));
    }
}
