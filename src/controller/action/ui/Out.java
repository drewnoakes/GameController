package controller.action.ui;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;
import data.PlayMode;

/**
 * This action means that the ball is out.
 *
 * @author Michel Bartsch
 */
public class Out extends GCAction
{
    /** On which side (0:left, 1:right) */
    private int side;
    
    
    /**
     * Creates a new Out action.
     * Look at the ActionBoard before using this.
     * 
     * @param side      On which side (0:left, 1:right)
     */
    public Out(int side)
    {
        super(ActionType.UI);
        this.side = side;
    }

    /**
     * Performs this action to manipulate the data (model).
     *
     * @param state      The current data to work on.
     */
    @Override
    public void perform(GameState state)
    {
        state.whenDropIn = state.getTime();
        state.dropInTeam = state.team[side].teamColor;
        Log.state(state, "Out by " + state.team[side].teamColor);
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
