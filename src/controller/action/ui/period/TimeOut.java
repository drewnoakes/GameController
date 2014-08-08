package controller.action.ui.period;

import common.Log;
import controller.action.ActionBoard;
import controller.action.ActionType;
import controller.action.GCAction;
import data.*;

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
     * @param data      The current data to work on.
     */
    @Override
    public void perform(GameState data)
    {
        if (!data.timeOutActive[side]) {
            data.previousPeriod = data.period;
            data.period = Period.Timeout;
            data.timeOutActive[side] = true;
            data.timeOutTaken[side] = true;
            if (data.previousPeriod != Period.PenaltyShootout) {
                data.kickOffTeam = data.team[side].teamColor.other();
            } else if (data.playMode == PlayMode.Set) {
                data.team[data.kickOffTeam == data.team[0].teamColor ? 0 : 1].penaltyShot--;
            }
            Log.setNextMessage("Timeout "+data.team[side].teamColor);
            ActionBoard.initial.forcePerform(data);
        } else {
            data.period = data.previousPeriod;
            data.previousPeriod = Period.Timeout;
            data.timeOutActive[side] = false;
            Log.setNextMessage("End of Timeout "+data.team[side].teamColor);
            if (data.period != Period.PenaltyShootout) {
                ActionBoard.ready.perform(data);
            }
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
      return data.timeOutActive[side]
            || ((data.playMode == PlayMode.Initial ||
                  data.playMode == PlayMode.Ready ||
                  data.playMode == PlayMode.Set)
                && !data.timeOutTaken[side]
                && !data.timeOutActive[side == 0 ? 1 : 0]
                && data.period != Period.Timeout)
            || data.testmode;
    }
}
