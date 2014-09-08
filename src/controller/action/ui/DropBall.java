package controller.action.ui;

import common.annotations.NotNull;
import controller.*;
import controller.action.ActionBoard;
import data.PlayMode;

/**
 * This action means that a global game stuck has occurred.
 *
 * @author Michel Bartsch
 */
public class DropBall extends Action
{    
    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        // Set to null, indicating no team has kick off
        state.setNextKickOffColor(null);
        ActionBoard.ready.forceExecute(game, state);
        game.pushState("Dropped Ball");
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return state.getPlayMode() == PlayMode.Playing || state.isTestMode();
    }
}
