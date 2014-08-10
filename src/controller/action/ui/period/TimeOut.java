package controller.action.ui.period;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.action.ActionBoard;
import controller.action.ActionTrigger;
import controller.action.GCAction;
import data.*;
import rules.Rules;

/**
 * This action means that a timeOut is to be taken or ending.
 *
 * @author Michel Bartsch
 */
public class TimeOut extends GCAction
{
    /** On which side (0:left, 1:right) */
    private final int side;

    /**
     * @param side the side to whom the timeout belongs (0:left, 1:right)
     */
    public TimeOut(int side)
    {
        super(ActionTrigger.User);
        this.side = side;
    }

    @Override
    public void perform(@NotNull GameState state, @Nullable String message)
    {
        if (!state.timeOutActive[side]) {
            // Starting a timeout
            state.previousPeriod = state.period;
            state.period = Period.Timeout;
            state.timeOutActive[side] = true;
            state.timeOutTaken[side] = true;
            if (state.previousPeriod != Period.PenaltyShootout) {
                if (Rules.league.giveOpponentKickOffOnTimeOut)
                    state.kickOffTeam = state.team[side].teamColor.other();
            } else if (state.playMode == PlayMode.Set) {
                state.team[state.kickOffTeam == state.team[0].teamColor ? 0 : 1].penaltyShot--;
            }
            ActionBoard.initial.forcePerform(state, "Timeout " + state.team[side].teamColor);
        } else {
            // Completing
            state.period = state.previousPeriod;
            state.previousPeriod = Period.Timeout;
            state.timeOutActive[side] = false;
            if (state.period != Period.PenaltyShootout) {
                ActionBoard.ready.perform(state, "End of Timeout " + state.team[side].teamColor);
            }
        }
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
      return state.timeOutActive[side]
            || ((state.playMode == PlayMode.Initial ||
                  state.playMode == PlayMode.Ready ||
                  state.playMode == PlayMode.Set)
                && !state.timeOutTaken[side]
                && !state.timeOutActive[side == 0 ? 1 : 0]
                && state.period != Period.Timeout)
            || state.testmode;
    }
}
