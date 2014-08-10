package controller.action.ui.playmode;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.action.ActionTrigger;
import controller.action.GCAction;
import data.GameState;
import data.PlayMode;

/**
 * Sets play mode to {@link PlayMode#Playing}.
 *
 * @author Michel Bartsch
 */
public class Play extends GCAction
{
    public Play()
    {
        super(ActionTrigger.User);
    }

    @Override
    public void perform(@NotNull GameState state, @Nullable String message)
    {
        if (state.playMode == PlayMode.Playing) {
            return;
        }
        if (!state.playoff && state.timeBeforeCurrentPlayMode != 0) {
            state.addTimeInCurrentPlayMode();
        }
        state.whenCurrentPlayModeBegan = state.getTime();
        state.playMode = PlayMode.Playing;
        log(state, message, "Playing");
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return (state.playMode == PlayMode.Set)
            || (state.playMode == PlayMode.Playing)
            || state.testmode;
    }
}