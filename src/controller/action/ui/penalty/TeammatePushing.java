package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.Game;
import data.GameState;
import data.PlayMode;
import data.PlayerInfo;

public class TeammatePushing extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull GameState state, @NotNull PlayerInfo player, int side, int number)
    {
        game.pushState("Teammate Pushing  " + state.team[side].teamColor + " " + (number + 1));
    }

    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return Game.settings.dropInPlayerMode
                && (state.playMode == PlayMode.Ready || state.playMode == PlayMode.Playing)
                || state.testmode;
    }
}
