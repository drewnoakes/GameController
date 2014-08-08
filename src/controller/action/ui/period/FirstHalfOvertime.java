package controller.action.ui.period;

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
     * @param state      The current data to work on.
     */
    @Override
    public void perform(GameState state)
    {
        if (!state.firstHalf || state.period == Period.PenaltyShootout) {
            state.firstHalf = true;
            state.period = Period.Overtime;
            if (state.colorChangeAuto) {
                state.team[0].teamColor = TeamColor.Blue;
                state.team[1].teamColor = TeamColor.Red;
            }
            FirstHalf.changeSide(state);
            state.kickOffTeam = (state.leftSideKickoff ? state.team[0].teamColor : state.team[1].teamColor);
            state.playMode = PlayMode.Initial;
            Log.state(state, "1st Half Extra Time");
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
        return (state.firstHalf && state.period == Period.Overtime)
                || (Rules.league.overtime
                    && state.playoff
                    && state.period == Period.Normal
                    && state.playMode == PlayMode.Finished
                    && !state.firstHalf
                    && state.team[0].score == state.team[1].score
                    && state.team[0].score > 0)
                || state.testmode;
    }
}
