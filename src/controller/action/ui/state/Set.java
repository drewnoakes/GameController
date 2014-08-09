package controller.action.ui.state;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import controller.action.ui.half.FirstHalf;
import data.*;
import rules.Rules;


/**
 * @author Michel Bartsch
 * 
 * This action means that the state is to be set to set.
 */
public class Set extends GCAction
{
    /**
     * Creates a new Set action.
     * Look at the ActionBoard before using this.
     */
    public Set()
    {
        super(ActionType.UI);
    }

    /**
     * Performs this action to manipulate the data (model).
     * 
     * @param data      The current data to work on.
     */
    @Override
    public void perform(AdvancedData data)
    {
        if (data.gameState == GameState.Set) {
            return;
        }
        if (Rules.league.returnRobotsInGameStoppages) {
            data.resetPenaltyTimes();
        }
        if (!data.playoff && data.timeBeforeCurrentGameState != 0) {
            data.addTimeInCurrentState();
        }
        data.whenCurrentGameStateBegan = data.getTime();

        if (data.secGameState == SecondaryGameState.PenaltyShootout) {
            data.timeBeforeCurrentGameState = 0;
            if (data.gameState != GameState.Initial) {
                data.kickOffTeam = data.kickOffTeam == TeamColor.Blue ? TeamColor.Red : TeamColor.Blue;
                FirstHalf.changeSide(data);
            }

            if (data.gameState != GameState.Playing) {
                data.team[data.team[0].teamColor == data.kickOffTeam ? 0 : 1].penaltyShot++;
            }
        }
        data.gameState = GameState.Set;
        Log.state(data, "Set");
    }
    
    /**
     * Checks if this action is legal with the given data (model).
     * Illegal actions are not performed by the EventHandler.
     * 
     * @param data      The current data to check with.
     */
    @Override
    public boolean isLegal(AdvancedData data)
    {
        return data.gameState == GameState.Ready
            || data.gameState == GameState.Set
            || (data.secGameState == SecondaryGameState.PenaltyShootout
              && (data.gameState != GameState.Playing || Rules.league.penaltyShotRetries)
              && !data.timeOutActive[0]
              && !data.timeOutActive[1]
              && !data.refereeTimeout)
            || data.testmode;
    }
}
