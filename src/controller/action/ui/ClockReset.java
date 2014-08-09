package controller.action.ui;

import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;

/**
 * This action means that the clock is to be resetted.
 *
 * @author Michel Bartsch
 */
public class ClockReset extends GCAction
{
    public ClockReset()
    {
        super(ActionType.UI);
    }

    @Override
    public void perform(GameState state, String message)
    {
        state.timeBeforeCurrentPlayMode = 0;
        state.whenCurrentPlayModeBegan = state.getTime();
        state.manWhenClockChanged = state.whenCurrentPlayModeBegan;
        state.manRemainingGameTimeOffset = 0;
        log(state, message, "Time reset");
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return state.testmode;
    }
}