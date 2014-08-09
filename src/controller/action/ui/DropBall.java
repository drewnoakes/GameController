package controller.action.ui;

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
    public void perform(GameState state, String message)
    {
        // Set to null, indicating no team has kick off
        state.kickOffTeam = null;
        ActionBoard.ready.perform(state, "Dropped Ball");
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return state.playMode == PlayMode.Playing || state.testmode;
    }
}
