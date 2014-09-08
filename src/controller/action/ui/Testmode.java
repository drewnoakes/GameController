package controller.action.ui;

import common.Log;
import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.WriteableGameState;

/**
 * Toggles test mode on/off.
 *
 * @author Michel Bartsch
 */
public class Testmode extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        state.setTestMode(!state.isTestMode());
        Log.toFile("Testmode = " + state.isTestMode());
    }
}