package controller.action.ui.half;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import data.AdvancedData;
import data.PlayMode;
import data.SecondaryGameState;
import rules.Rules;


/**
 * @author Michel Bartsch
 * 
 * This action means that a penalty shoot is to be starting.
 */
public class PenaltyShoot extends GCAction
{
    /**
     * Creates a new PenaltyShoot action.
     * Look at the ActionBoard before using this.
     */
    public PenaltyShoot()
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
        if (data.secGameState != SecondaryGameState.PenaltyShootout) {
            data.secGameState = SecondaryGameState.PenaltyShootout;
            // Don't set data.whenCurrentPlayModeBegan, because it's used to count the pause
            data.playMode = PlayMode.Initial;
            data.timeBeforeCurrentPlayMode = 0;
            data.resetPenalties();
            if (Rules.league.timeOutPerHalf) {
                data.timeOutTaken = new boolean[] {false, false};
            }
            Log.state(data, "Penalty Shoot-out");
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
        return data.secGameState == SecondaryGameState.PenaltyShootout
          || data.previousSecGameState == SecondaryGameState.PenaltyShootout
          || (!data.firstHalf
            && data.playMode == PlayMode.Finished
            && !(Rules.league.overtime
                && data.playoff
                && data.secGameState == SecondaryGameState.Normal
                && data.team[0].score == data.team[1].score
                && data.team[0].score > 0))
          || data.testmode;
    }
}
