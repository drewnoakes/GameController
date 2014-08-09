package controller.action.ui.period;

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
    public PenaltyShoot()
    {
        super(ActionType.UI);
    }

    @Override
    public void perform(GameState state, String message)
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
            log(state, message, "Penalty Shoot-out");
        }
    }
    
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
