package controller.action.ui.playmode;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;
import data.PlayMode;
import rules.Rules;


/**
 * Sets play mode to @{link PlayMode#Finished}.
 *
 * @author Michel Bartsch
 */
public class Finish extends GCAction
{
    /**
     * Creates a new Finish action.
     * Look at the ActionBoard before using this.
     */
    public Finish()
    {
        super(ActionType.UI);
    }

    /**
     * Performs this action to manipulate the data (model).
     * 
     * @param data      The current data to work on.
     */
    @Override
    public void perform(GameState data)
    {
        if (data.playMode == PlayMode.Finished) {
            return;
        }
        if (Rules.league.returnRobotsInGameStoppages) {
            data.resetPenaltyTimes();
        }
        data.addTimeInCurrentPlayMode();
        data.whenCurrentPlayModeBegan = data.getTime();
        data.playMode = PlayMode.Finished;
        Log.state(data, "Finished");
    }
    
    /**
     * Checks if this action is legal with the given data (model).
     * Illegal actions are not performed by the EventHandler.
     * 
     * @param data      The current data to check with.
     */
    @Override
    public boolean isLegal(GameState data)
    {
        return data.playMode == PlayMode.Ready
            || data.playMode == PlayMode.Set
            || data.playMode == PlayMode.Playing
            || data.playMode == PlayMode.Finished
            || data.testmode;
    }
}