package controller.action.ui.period;

import common.annotations.NotNull;
import common.annotations.Nullable;
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
    public void perform(@NotNull GameState state, @Nullable String message)
    {
        if (!state.refereeTimeout) {
            state.previousPeriod = state.period;
            state.period = Period.Timeout;
            state.refereeTimeout = true;
            if (state.playMode == PlayMode.Playing) {
                state.addTimeInCurrentPlayMode();
            }
            if (state.previousPeriod == Period.PenaltyShootout
                    && (state.playMode == PlayMode.Set || state.playMode == PlayMode.Playing)) {
                state.team[state.kickOffTeam == state.team[0].teamColor ? 0 : 1].penaltyShot--;
            }
            ActionBoard.initial.forcePerform(state, "Referee Timeout");
        } else {
            state.period = state.previousPeriod;
            state.previousPeriod = Period.Timeout;
            state.refereeTimeout = false;
            if (state.period != Period.PenaltyShootout) {
                ActionBoard.ready.perform(state, "End of Referee Timeout");
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
