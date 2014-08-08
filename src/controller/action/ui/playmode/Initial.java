package controller.action.ui.playmode;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import data.AdvancedData;
import data.PlayMode;
import rules.Rules;

/**
 * Sets play mode to @{link PlayMode#Initial}.
 *
 * @author Michel Bartsch
 */
public class Initial extends GCAction
{
    /**
     * Creates a new Initial action.
     * Look at the ActionBoard before using this.
     */
    public Initial()
    {
        super(ActionType.UI);
    }

    /**
     * Performs this action to manipulate the data (model).
     * 
     * @param data      The current data to work on.
     */
    @Override
    public void perform(AdvancedData data)
    {
        if (data.playMode != PlayMode.Initial) {
            forcePerform(data);
        }
    }

    /**
     * Performs this action, even if the current play mode is @{link PlayMode#Initial}.
     *
     * @param data The current data to work on.
     */
    public void forcePerform(AdvancedData data)
    {
        if (Rules.league.returnRobotsInGameStoppages) {
            data.resetPenaltyTimes();
        }
        data.whenCurrentPlayModeBegan = data.getTime();
        data.playMode = PlayMode.Initial;
        Log.state(data, "Initial");
    }

    /**
     * Checks if this action is legal with the given data (model).
     * Illegal actions are not performed by the EventHandler.
     * 
     * @param data      The current data to check with.
     */
    @Override
    public boolean isLegal(AdvancedData data)
    {
        return data.playMode == PlayMode.Initial || data.testmode;
    }
}