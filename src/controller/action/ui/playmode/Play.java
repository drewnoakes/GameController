package controller.action.ui.playmode;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;
import data.PlayMode;

/**
 * Sets play mode to @{link PlayMode#Playing}.
 *
 * @author Michel Bartsch
 */
public class Play extends GCAction
{
    /**
     * Creates a new Play action.
     * Look at the ActionBoard before using this.
     */
    public Play()
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
        if (state.playMode == PlayMode.Playing) {
            return;
        }
        if (!state.playoff && state.timeBeforeCurrentPlayMode != 0) {
            state.addTimeInCurrentPlayMode();
        }
        state.whenCurrentPlayModeBegan = state.getTime();
        state.playMode = PlayMode.Playing;
        Log.state(state, "Playing");
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
        return (state.playMode == PlayMode.Set)
            || (state.playMode == PlayMode.Playing)
            || state.testmode;
    }
}