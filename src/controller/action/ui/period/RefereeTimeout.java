package controller.action.ui.period;

import common.Log;

import controller.action.ActionBoard;
import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;
import data.PlayMode;
import data.Period;

public class RefereeTimeout extends GCAction
{
    public RefereeTimeout()
    {
        super(ActionType.UI);
    }

    @Override
    public void perform(GameState state)
    {
        if (!state.refereeTimeout) {
            state.previousPeriod = state.period;
            state.period = Period.Timeout;
            state.refereeTimeout = true;
            Log.setNextMessage("Referee Timeout");
            if (state.playMode == PlayMode.Playing) {
                state.addTimeInCurrentPlayMode();
            }
            if (state.previousPeriod == Period.PenaltyShootout
                    && (state.playMode == PlayMode.Set || state.playMode == PlayMode.Playing)) {
                state.team[state.kickOffTeam == state.team[0].teamColor ? 0 : 1].penaltyShot--;
            }
            ActionBoard.initial.forcePerform(state);
        } else {
            state.period = state.previousPeriod;
            state.previousPeriod = Period.Timeout;
            state.refereeTimeout = false;
            Log.setNextMessage("End of Referee Timeout");
            if (state.period != Period.PenaltyShootout) {
                ActionBoard.ready.perform(state);
            }
        }
    }

    @Override
    public boolean isLegal(GameState state)
    {
        return state.playMode != PlayMode.Finished
                && !state.timeOutActive[0]
                && !state.timeOutActive[1];
    }
}
