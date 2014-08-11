package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.Game;
import data.GameState;
import data.PlayMode;
import data.Penalty;
import data.PlayerInfo;

/**
 * This action means that the leaving the field penalty has been selected.
 *
 * @author Michel Bartsch
 */
public class Leaving extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull GameState state, @NotNull PlayerInfo player, int side, int number)
    {
        player.penalty = Penalty.SplLeavingTheField;
        state.whenPenalized[side][number] = state.getTime();
        game.pushState("Leaving the Field " + state.team[side].teamColor + " " + (number + 1));
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return state.playMode == PlayMode.Ready
            || state.playMode == PlayMode.Playing
            || state.testmode;
    }
}
