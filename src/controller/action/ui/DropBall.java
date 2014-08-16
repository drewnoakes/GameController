package controller.action.ui;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.action.ActionBoard;
import controller.GameState;
import data.PlayMode;

/**
 * This action means that a global game stuck has occurred.
 *
 * @author Michel Bartsch
 */
public class DropBall extends Action
{    
    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        // Set to null, indicating no team has kick off
        state.nextKickOffColor = null;
        ActionBoard.ready.forceExecute(game, state);
        game.pushState("Dropped Ball");
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return state.playMode == PlayMode.Playing || state.testmode;
    }
}
