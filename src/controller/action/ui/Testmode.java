package controller.action.ui;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;

/**
 * This action means that the testmode should be toggled on or off.
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
    public void perform(GameState state)
    {
        state.testmode = !state.testmode;
        Log.toFile("Testmode = "+ state.testmode);
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return true;
    }
}