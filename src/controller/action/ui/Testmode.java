package controller.action.ui;

import common.Log;
import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;

/**
 * Toggles test mode on/off.
 *
 * @author Michel Bartsch
 */
public class Testmode extends GCAction
{
    public Testmode()
    {
        super(ActionType.UI);
    }
    
    @Override
    public void perform(@NotNull GameState state, @Nullable String message)
    {
        state.testmode = !state.testmode;
        Log.toFile("Testmode = " + state.testmode);
    }
}