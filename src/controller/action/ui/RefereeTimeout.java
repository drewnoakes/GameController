package controller.action.ui;

import common.Log;

import controller.action.ActionBoard;
import controller.action.ActionType;
import controller.action.GCAction;
import data.AdvancedData;
import data.GameState;
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
            if (data.gameState == GameState.Playing) {
                data.addTimeInCurrentState();
            }
            if (data.previousSecGameState == SecondaryGameState.PenaltyShootout
                    && (data.gameState == GameState.Set || data.gameState == GameState.Playing)) {
                data.team[data.kickOffTeam == data.team[0].teamColor ? 0 : 1].penaltyShot--;
            }
            
            data.gameState = null; // to force execution of next call
            ActionBoard.initial.perform(data);
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
        return data.gameState != GameState.Finished
                && !data.timeOutActive[0] && !data.timeOutActive[1];
    }

}
