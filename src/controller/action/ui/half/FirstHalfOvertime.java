package controller.action.ui.half;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import data.AdvancedData;
import data.GameControlData;
import data.GameState;
import rules.Rules;


/**
 * @author Michel Bartsch
 * 
 * This action means that the half is to be set to the first half.
 */
public class FirstHalfOvertime extends GCAction
{
    /**
     * Creates a new FirstHalf action.
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
    public void perform(AdvancedData data)
    {
        if (data.firstHalf != GameControlData.C_TRUE || data.secGameState == GameControlData.STATE2_PENALTYSHOOT) {
            data.firstHalf = GameControlData.C_TRUE;
            data.secGameState = GameControlData.STATE2_OVERTIME;
            if (data.colorChangeAuto) {
                data.team[0].teamColor = GameControlData.TEAM_BLUE;
                data.team[1].teamColor = GameControlData.TEAM_RED;
            }
            FirstHalf.changeSide(data);
            data.kickOffTeam = (data.leftSideKickoff ? data.team[0].teamColor : data.team[1].teamColor);
            data.gameState = GameState.Initial;
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
    public boolean isLegal(AdvancedData data)
    {
        return ((data.firstHalf == GameControlData.C_TRUE)
                && (data.secGameState == GameControlData.STATE2_OVERTIME))
                || ((Rules.league.overtime)
                    && (data.playoff)
                    && (data.secGameState == GameControlData.STATE2_NORMAL)
                    && (data.gameState == GameState.Finished)
                    && (data.firstHalf  != GameControlData.C_TRUE)
                    && (data.team[0].score == data.team[1].score)
                    && (data.team[0].score > 0))
                || (data.testmode);
    }
}