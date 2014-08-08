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
     * @param state      The current data to work on.
     */
    @Override
    public void perform(GameState state)
    {
        if (state.playMode == PlayMode.Finished) {
            return;
        }
        if (Rules.league.returnRobotsInGameStoppages) {
            state.resetPenaltyTimes();
        }
        state.addTimeInCurrentPlayMode();
        state.whenCurrentPlayModeBegan = state.getTime();
        state.playMode = PlayMode.Finished;
        Log.state(state, "Finished");
    }
    
    /**
     * Checks if this action is legal with the given data (model).
     * Illegal actions are not performed by the EventHandler.
     *
     * @param state      The current data to check with.
     */
    @Override
    public boolean isLegal(GameState state)
    {
        return state.playMode == PlayMode.Ready
            || state.playMode == PlayMode.Set
            || state.playMode == PlayMode.Playing
            || state.playMode == PlayMode.Finished
            || state.testmode;
    }
}