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
    public void perform(GameState data)
    {
        data.timeBeforeCurrentPlayMode -= 1000*60;
        Log.state(data, "Increase Game Clock");
    }

    @Override
    public boolean isLegal(GameState data)
    {
        return data.playMode != PlayMode.Playing
                && data.timeBeforeCurrentPlayMode >= 1000*60
                || data.testmode;
    }
}
