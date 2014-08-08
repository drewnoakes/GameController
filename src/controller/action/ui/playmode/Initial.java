package controller.action.ui.playmode;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;
import data.PlayMode;
import rules.Rules;

/**
 * Sets play mode to @{link PlayMode#Initial}.
 *
 * @author Michel Bartsch
 */
public class Initial extends GCAction
{
    public Initial()
    {
        super(ActionType.UI);
    }

    @Override
    public void perform(GameState state)
    {
        if (state.playMode != PlayMode.Initial) {
            forcePerform(state);
        }
    }

    /**
     * Performs this action, even if the current play mode is @{link PlayMode#Initial}.
     *
     * @param data The current data to work on.
     */
    public void forcePerform(GameState data)
    {
        if (Rules.league.returnRobotsInGameStoppages) {
            data.resetPenaltyTimes();
        }
        data.whenCurrentPlayModeBegan = data.getTime();
        data.playMode = PlayMode.Initial;
        Log.state(data, "Initial");
    }

    @Override
    public boolean isLegal(GameState state)
    {
        return state.playMode == PlayMode.Initial || state.testmode;
    }
}