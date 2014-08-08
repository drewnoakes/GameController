package controller.action.ui.period;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import data.*;

/**
 * This action means that the half is to be set to the second half.
 *
 * @author Michel Bartsch
 */
public class SecondHalf extends GCAction
{
    public SecondHalf()
    {
        super(ActionType.UI);
    }

    @Override
    public void perform(GameState state)
    {
        if (state.firstHalf || state.period == Period.PenaltyShootout) {
            state.firstHalf = false;
            state.period = Period.Normal;
            if (state.colorChangeAuto) {
                state.team[0].teamColor = TeamColor.Blue;
                state.team[1].teamColor = TeamColor.Red;
            }
            FirstHalf.changeSide(state);
            state.kickOffTeam = (state.leftSideKickoff ? state.team[0].teamColor : state.team[1].teamColor);
            state.playMode = PlayMode.Initial;
            // Don't set data.whenCurrentPlayModeBegan, because it's used to count the pause
            Log.state(state, "2nd Half");
        }
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return (!state.firstHalf && state.period == Period.Normal)
            || (state.period == Period.Normal && state.playMode == PlayMode.Finished)
            || (state.testmode);
    }
}
