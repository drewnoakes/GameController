package controller.action.ui;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import data.GameState;

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
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        if (game.getLastUserAction() == this) {
            // User executed this action twice in a row -- so actually perform it
            game.requestShutdown();
        }
    }
}