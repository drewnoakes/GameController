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
    public DropBall()
    {
        super(ActionType.UI);
    }

    @Override
    public void perform(GameState state)
    {
        // Set to null, indicating no team has kick off
        state.kickOffTeam = null;
        Log.setNextMessage("Dropped Ball");
        ActionBoard.ready.perform(state);
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return state.playMode == PlayMode.Playing || state.testmode;
    }
}
