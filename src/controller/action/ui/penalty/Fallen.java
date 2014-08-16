package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.Game;
import controller.GameState;
import data.PlayMode;
import data.Penalty;
import data.PlayerState;

/**
 * This action means that the fallen robot penalty has been selected.
 *
 * @author Michel Bartsch
 */
public class Fallen extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull GameState state, @NotNull PlayerState player, int side, int number)
    {
        player.penalty = Penalty.SplObstruction;
        state.whenPenalized[side][number] = state.getTime();
        game.pushState("Fallen Robot " + state.team[side].teamColor + " " + (number + 1));
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return state.playMode == PlayMode.Ready
            || state.playMode == PlayMode.Playing
            || state.playMode == PlayMode.Set
            || state.testmode;
    }
}
