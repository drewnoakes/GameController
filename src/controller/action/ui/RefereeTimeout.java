package controller.action.ui;

import common.Log;

import controller.action.ActionBoard;
import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;
import data.PlayMode;
import data.Period;

public class RefereeTimeout extends GCAction
{
    public RefereeTimeout()
    {
        super(ActionType.UI);
    }

    @Override
    public void perform(GameState data)
    {
        if (!data.refereeTimeout) {
            data.previousPeriod = data.period;
            data.period = Period.Timeout;
            data.refereeTimeout = true;
            Log.setNextMessage("Referee Timeout");
            if (data.playMode == PlayMode.Playing) {
                data.addTimeInCurrentPlayMode();
            }
            if (data.previousPeriod == Period.PenaltyShootout
                    && (data.playMode == PlayMode.Set || data.playMode == PlayMode.Playing)) {
                data.team[data.kickOffTeam == data.team[0].teamColor ? 0 : 1].penaltyShot--;
            }
            ActionBoard.initial.forcePerform(data);
        } else {
            data.period = data.previousPeriod;
            data.previousPeriod = Period.Timeout;
            data.refereeTimeout = false;
            Log.setNextMessage("End of Referee Timeout");
            if (data.period != Period.PenaltyShootout) {
                ActionBoard.ready.perform(data);
            }
        }
    }

    @Override
    public boolean isLegal(GameState data)
    {
        return data.playMode != PlayMode.Finished
                && !data.timeOutActive[0] && !data.timeOutActive[1];
    }

}
