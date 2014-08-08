package controller.action.ui.playmode;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import data.AdvancedData;
import data.PlayMode;
import data.SecondaryGameState;
import rules.Rules;

/**
 * Sets play mode to @{link PlayMode#Ready}.
 *
 * @author Michel Bartsch
 */
public class Ready extends GCAction
{
    /**
     * Creates a new Ready action.
     * Look at the ActionBoard before using this.
     */
    public Ready()
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
        if (data.playMode == PlayMode.Ready) {
            return;
        }
        if (Rules.league.returnRobotsInGameStoppages) {
            data.resetPenaltyTimes();
        }
        if (data.playMode == PlayMode.Playing) {
            data.addTimeInCurrentPlayMode();
        }
        data.whenCurrentPlayModeBegan = data.getTime();
        data.playMode = PlayMode.Ready;
        Log.state(data, "Ready");
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
        return
            (data.playMode == PlayMode.Initial
              && !data.timeOutActive[0]
              && !data.timeOutActive[1]
              && !data.refereeTimeout
              && data.secGameState != SecondaryGameState.PenaltyShootout)
            || data.playMode == PlayMode.Ready
            || data.testmode;
    }
}
