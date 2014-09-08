package controller.action.ui;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.WriteableGameState;

/**
 * Causes the current game to end.
 * <p>
 * For safety, this action must be run twice in a row by the user.
 *
 * @author Michel Bartsch
 */
public class Quit extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        if (game.getLastUserAction() == this) {
            // User executed this action twice in a row -- so actually perform it
            game.requestShutdown();
        }
    }
}