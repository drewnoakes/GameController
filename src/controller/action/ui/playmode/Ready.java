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
    public Ready()
    {
        super(ActionType.UI);
    }

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
