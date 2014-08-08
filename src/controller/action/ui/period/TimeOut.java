package controller.action.ui.period;

import common.Log;
import controller.action.ActionBoard;
import controller.action.ActionType;
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
    private int side;

    /**
     * Creates a new TimeOut action.
     * Look at the ActionBoard before using this.
     * 
     * @param side      On which side (0:left, 1:right)
     */
    public TimeOut(int side)
    {
        super(ActionType.UI);
        this.side = side;
    }

    /**
     * Performs this action to manipulate the data (model).
     *
     * @param state      The current data to work on.
     */
    @Override
    public void perform(GameState state)
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
            Log.setNextMessage("Timeout "+ state.team[side].teamColor);
            ActionBoard.initial.forcePerform(state);
        } else {
            // Completing
            state.period = state.previousPeriod;
            state.previousPeriod = Period.Timeout;
            state.timeOutActive[side] = false;
            Log.setNextMessage("End of Timeout "+ state.team[side].teamColor);
            if (state.period != Period.PenaltyShootout) {
                ActionBoard.ready.perform(state);
            }
        }
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
