package controller.action.ui.playmode;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;
import data.PlayMode;
import data.Period;
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
     * @param state      The current data to work on.
     */
    @Override
    public void perform(GameState state)
    {
        if (state.playMode == PlayMode.Ready) {
            return;
        }
        if (Rules.league.returnRobotsInGameStoppages) {
            state.resetPenaltyTimes();
        }
        if (state.playMode == PlayMode.Playing) {
            state.addTimeInCurrentPlayMode();
        }
        state.whenCurrentPlayModeBegan = state.getTime();
        state.playMode = PlayMode.Ready;
        Log.state(state, "Ready");
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
        return
            (state.playMode == PlayMode.Initial
              && !state.timeOutActive[0]
              && !state.timeOutActive[1]
              && !state.refereeTimeout
              && state.period != Period.PenaltyShootout)
            || state.playMode == PlayMode.Ready
            || state.testmode;
    }
}
