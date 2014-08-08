package controller.action.ui;

import common.Log;
import controller.action.ActionBoard;
import controller.action.ActionType;
import controller.action.GCAction;
import data.AdvancedData;
import data.GameState;

/**
 * @author Michel Bartsch
 * 
 * This action means that a global game stuck has occured.
 */
public class DropBall extends GCAction
{    
    /**
     * Creates a new DropBall action.
     * Look at the ActionBoard before using this.
     */
    public DropBall()
    {
        super(ActionType.UI);
    }

    /**
     * Performs this action to manipulate the data (model).
     * 
     * @param data      The current data to work on.
     */
    @Override
    public void perform(AdvancedData data)
    {
        // Set to null, indicating no team has kick off
        data.kickOffTeam = null;
        Log.setNextMessage("Dropped Ball");
        ActionBoard.ready.perform(data);
    }
    
    /**
     * Checks if this action is legal with the given data (model).
     * Illegal actions are not performed by the EventHandler.
     * 
     * @param data      The current data to check with.
     */
    @Override
    public boolean isLegal(AdvancedData data)
    {
        return (data.gameState == GameState.Playing) || data.testmode;
    }
}
