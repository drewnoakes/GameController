package controller.action.ui;

import common.Log;
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
    public void perform(GameState state, String message)
    {
        state.testmode = !state.testmode;
        Log.toFile("Testmode = " + state.testmode);
    }
}