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
    /**
     * Creates a new Testmode action.
     * Look at the ActionBoard before using this.
     */
    public Testmode()
    {
        super(ActionType.UI);
    }
    
    /**
     * Performs this action to manipulate the data (model).
     *
     * @param state      The current data to work on.
     */
    @Override
    public void perform(GameState state)
    {
        state.testmode = !state.testmode;
        Log.toFile("Testmode = "+ state.testmode);
    }
    
    /**
     * Checks if this action is legal with the given data (model).
     * Illegal actions are not performed by the EventHandler.
     *
     * @param state      The current data to check with.
     */
    @Override
    public boolean isLegal(GameState state)
    {
        return true;
    }
}