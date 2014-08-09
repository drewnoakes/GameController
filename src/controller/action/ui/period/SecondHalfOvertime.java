package controller.action.ui.period;

import controller.action.ActionType;
import controller.action.GCAction;
import data.*;

/**
 * This action means that the half is to be set to the second half of overtime.
 *
 * @author Michel Bartsch
 */
public class SecondHalfOvertime extends GCAction
{
    public SecondHalfOvertime()
    {
        super(ActionType.UI);
    }

    @Override
    public void perform(GameState state, String message)
    {
        if (state.firstHalf || state.period == Period.PenaltyShootout) {
            state.firstHalf = false;
            state.period = Period.Overtime;
            if (state.colorChangeAuto) {
                state.team[0].teamColor = TeamColor.Blue;
                state.team[1].teamColor = TeamColor.Red;
            }
            FirstHalf.changeSide(state);
            state.kickOffTeam = (state.leftSideKickoff ? state.team[0].teamColor : state.team[1].teamColor);
            state.playMode = PlayMode.Initial;
            log(state, message, "2nd Half Extra Time");
        }
    }

    @Override
    public boolean isLegal(GameState state)
    {
        return (!state.firstHalf && state.period == Period.Overtime)
            || (state.period == Period.Overtime && state.playMode == PlayMode.Finished)
            || state.testmode;
    }
}
