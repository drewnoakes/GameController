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
    public Play()
    {
        super(ActionType.UI);
    }

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
    
    @Override
    public boolean isLegal(GameState state)
    {
        return (state.playMode == PlayMode.Set)
            || (state.playMode == PlayMode.Playing)
            || state.testmode;
    }
}