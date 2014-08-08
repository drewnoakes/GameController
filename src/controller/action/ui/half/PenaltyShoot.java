package controller.action.ui.half;

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
     * @param data      The current data to work on.
     */
    @Override
    public void perform(GameState data)
    {
        if (data.period != Period.PenaltyShootout) {
            data.period = Period.PenaltyShootout;
            // Don't set data.whenCurrentPlayModeBegan, because it's used to count the pause
            data.playMode = PlayMode.Initial;
            data.timeBeforeCurrentPlayMode = 0;
            data.resetPenalties();
            if (Rules.league.timeOutPerHalf) {
                data.timeOutTaken = new boolean[] {false, false};
            }
            Log.state(data, "Penalty Shoot-out");
        }
    }
    
    /**
     * Checks if this action is legal with the given data (model).
     * Illegal actions are not performed by the EventHandler.
     * 
     * @param data      The current data to check with.
     */
    @Override
    public boolean isLegal(GameState data)
    {
        return data.period == Period.PenaltyShootout
          || data.previousPeriod == Period.PenaltyShootout
          || (!data.firstHalf
            && data.playMode == PlayMode.Finished
            && !(Rules.league.overtime
                && data.playoff
                && data.period == Period.Normal
                && data.team[0].score == data.team[1].score
                && data.team[0].score > 0))
          || data.testmode;
    }
}
