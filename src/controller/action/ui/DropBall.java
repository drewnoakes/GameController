package controller.action.ui;

import common.Log;
import controller.action.ActionBoard;
import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;
import data.PlayMode;

/**
 * This action means that a global game stuck has occurred.
 *
 * @author Michel Bartsch
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
     * @param state      The current data to work on.
     */
    @Override
    public void perform(GameState state)
    {
        // Set to null, indicating no team has kick off
        state.kickOffTeam = null;
        Log.setNextMessage("Dropped Ball");
        ActionBoard.ready.perform(state);
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
        return state.playMode == PlayMode.Playing || state.testmode;
    }
}
