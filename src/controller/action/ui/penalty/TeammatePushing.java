package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.Game;
import controller.GameState;
import data.PlayMode;
import data.PlayerState;

public class TeammatePushing extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull GameState state, @NotNull PlayerState player, int side, int number)
    {
        game.pushState("Teammate Pushing  " + state.team[side].teamColor + " " + (number + 1));
    }

    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return game.settings().dropInPlayerMode
                && (state.playMode == PlayMode.Ready || state.playMode == PlayMode.Playing)
                || state.testmode;
    }
}
