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
     * @param side on which side (0:left, 1:right)
     */
    public Out(int side)
    {
        super(ActionType.UI);
        this.side = side;
    }

    @Override
    public void perform(GameState state)
    {
        state.whenDropIn = state.getTime();
        state.dropInTeam = state.team[side].teamColor;
        Log.state(state, "Out by " + state.team[side].teamColor);
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return state.playMode == PlayMode.Playing || state.testmode;
    }
}
