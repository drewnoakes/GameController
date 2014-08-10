package controller.action.ui.playmode;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.action.ActionTrigger;
import controller.action.GCAction;
import data.GameState;
import data.PlayMode;
import data.Period;
import rules.Rules;

/**
 * Sets play mode to {@link PlayMode#Ready}.
 *
 * @author Michel Bartsch
 */
public class Ready extends GCAction
{
    public Ready()
    {
        super(ActionTrigger.User);
    }

    @Override
    public void perform(@NotNull GameState state, @Nullable String message)
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
        log(state, message, "Ready");
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
