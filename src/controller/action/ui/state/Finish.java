package controller.action.ui.state;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import data.AdvancedData;
import data.GameState;
import rules.Rules;


/**
 * @author Michel Bartsch
 * 
 * This action means that the state is to be set to finish.
 */
public class Finish extends GCAction
{
    /**
     * Creates a new Finish action.
     * Look at the ActionBoard before using this.
     */
    public Finish()
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
        if (data.gameState == GameState.Finished) {
            return;
        }
        if (Rules.league.returnRobotsInGameStoppages) {
            data.resetPenaltyTimes();
        }
        data.addTimeInCurrentState();
        data.whenCurrentGameStateBegan = data.getTime();
        data.gameState = GameState.Finished;
        Log.state(data, "Finished");
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
        return (data.gameState == GameState.Ready)
            || (data.gameState == GameState.Set)
            || (data.gameState == GameState.Playing)
            || (data.gameState == GameState.Finished)
            || data.testmode;
    }
}