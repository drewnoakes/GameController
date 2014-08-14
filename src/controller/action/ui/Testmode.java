package controller.action.ui;

import common.Log;
import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.GameState;

/**
 * Toggles test mode on/off.
 *
 * @author Michel Bartsch
 */
public class Testmode extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        state.testmode = !state.testmode;
        Log.toFile("Testmode = " + state.testmode);
    }
}