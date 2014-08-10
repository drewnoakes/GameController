package controller.action.ui;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.action.ActionTrigger;
import controller.action.GCAction;
import data.GameState;
import data.PlayMode;

public class IncGameClock extends GCAction
{
    public IncGameClock()
    {
        super(ActionTrigger.User);
    }

    @Override
    public void perform(@NotNull GameState state, @Nullable String message)
    {
        state.timeBeforeCurrentPlayMode -= 1000*60;
        log(state, message, "Increase Game Clock");
    }

    @Override
    public boolean isLegal(GameState state)
    {
        return state.playMode != PlayMode.Playing
                && state.timeBeforeCurrentPlayMode >= 1000*60
                || state.testmode;
    }
}
