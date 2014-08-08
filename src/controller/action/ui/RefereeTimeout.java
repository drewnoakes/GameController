package controller.action.ui;

import common.Log;

import controller.action.ActionBoard;
import controller.action.ActionType;
import controller.action.GCAction;
import data.AdvancedData;
import data.PlayMode;
import data.SecondaryGameState;

public class RefereeTimeout extends GCAction
{
    public RefereeTimeout()
    {
        super(ActionType.UI);
    }

    @Override
    public void perform(AdvancedData data)
    {
        if (!data.refereeTimeout) {
            data.previousSecGameState = data.secGameState;
            data.secGameState = SecondaryGameState.Timeout;
            data.refereeTimeout = true;
            Log.setNextMessage("Referee Timeout");
            if (data.playMode == PlayMode.Playing) {
                data.addTimeInCurrentPlayMode();
            }
            if (data.previousSecGameState == SecondaryGameState.PenaltyShootout
                    && (data.playMode == PlayMode.Set || data.playMode == PlayMode.Playing)) {
                data.team[data.kickOffTeam == data.team[0].teamColor ? 0 : 1].penaltyShot--;
            }
            ActionBoard.initial.forcePerform(data);
        } else {
            data.secGameState = data.previousSecGameState;
            data.previousSecGameState = SecondaryGameState.Timeout;
            data.refereeTimeout = false;
            Log.setNextMessage("End of Referee Timeout");
            if (data.secGameState != SecondaryGameState.PenaltyShootout) {
                ActionBoard.ready.perform(data);
            }
        }
    }

    @Override
    public boolean isLegal(AdvancedData data)
    {
        return data.playMode != PlayMode.Finished
                && !data.timeOutActive[0] && !data.timeOutActive[1];
    }

}
