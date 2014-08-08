package controller.action.ui.half;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import data.*;
import rules.Rules;


/**
 * This action means that the half is to be set to the first half of overtime.
 *
 * @author Michel Bartsch
 */
public class FirstHalfOvertime extends GCAction
{
    /**
     * Creates a new FirstHalfOvertime action.
     * Look at the ActionBoard before using this.
     */
    public FirstHalfOvertime()
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
        if (!data.firstHalf || data.period == Period.PenaltyShootout) {
            data.firstHalf = true;
            data.period = Period.Overtime;
            if (data.colorChangeAuto) {
                data.team[0].teamColor = TeamColor.Blue;
                data.team[1].teamColor = TeamColor.Red;
            }
            FirstHalf.changeSide(data);
            data.kickOffTeam = (data.leftSideKickoff ? data.team[0].teamColor : data.team[1].teamColor);
            data.playMode = PlayMode.Initial;
            Log.state(data, "1st Half Extra Time");
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
        return (data.firstHalf && data.period == Period.Overtime)
                || (Rules.league.overtime
                    && data.playoff
                    && data.period == Period.Normal
                    && data.playMode == PlayMode.Finished
                    && !data.firstHalf
                    && data.team[0].score == data.team[1].score
                    && data.team[0].score > 0)
                || data.testmode;
    }
}
