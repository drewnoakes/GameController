package controller.action.ui.period;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;
import data.PlayMode;
import data.Period;
import rules.Rules;


/**
 * This action means that a penalty shoot is to be starting.
 *
 * @author Michel Bartsch
 */
public class PenaltyShoot extends GCAction
{
    /**
     * Creates a new PenaltyShoot action.
     * Look at the ActionBoard before using this.
     */
    public PenaltyShoot()
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
        if (state.period != Period.PenaltyShootout) {
            state.period = Period.PenaltyShootout;
            // Don't set data.whenCurrentPlayModeBegan, because it's used to count the pause
            state.playMode = PlayMode.Initial;
            state.timeBeforeCurrentPlayMode = 0;
            state.resetPenalties();
            if (Rules.league.timeOutPerHalf) {
                state.timeOutTaken = new boolean[] {false, false};
            }
            Log.state(state, "Penalty Shoot-out");
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
        return state.period == Period.PenaltyShootout
          || state.previousPeriod == Period.PenaltyShootout
          || (!state.firstHalf
            && state.playMode == PlayMode.Finished
            && !(Rules.league.overtime
                && state.playoff
                && state.period == Period.Normal
                && state.team[0].score == state.team[1].score
                && state.team[0].score > 0))
          || state.testmode;
    }
}
