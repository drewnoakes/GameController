package controller.action.ui.playmode;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import data.AdvancedData;
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
     * @param data      The current data to work on.
     */
    @Override
    public void perform(AdvancedData data)
    {
        if (data.playMode == PlayMode.Playing) {
            return;
        }
        if (!data.playoff && data.timeBeforeCurrentPlayMode != 0) {
            data.addTimeInCurrentPlayMode();
        }
        data.whenCurrentPlayModeBegan = data.getTime();
        data.playMode = PlayMode.Playing;
        Log.state(data, "Playing");
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
        return (data.playMode == PlayMode.Set)
            || (data.playMode == PlayMode.Playing)
            || data.testmode;
    }
}