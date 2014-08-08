package controller.action.ui;

import common.Log;

import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;
import data.PlayMode;

public class IncGameClock extends GCAction
{
    public IncGameClock()
    {
        super(ActionType.UI);
    }

    @Override
    public void perform(GameState state)
    {
        state.timeBeforeCurrentPlayMode -= 1000*60;
        Log.state(state, "Increase Game Clock");
    }

    @Override
    public boolean isLegal(GameState state)
    {
        return state.playMode != PlayMode.Playing
                && state.timeBeforeCurrentPlayMode >= 1000*60
                || state.testmode;
    }
}
