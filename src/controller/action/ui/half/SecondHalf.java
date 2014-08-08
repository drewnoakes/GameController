package controller.action.ui.half;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import data.*;

/**
 * @author Michel Bartsch
 * 
 * This action means that the half is to be set to the second half.
 */
public class SecondHalf extends GCAction
{
    /**
     * Creates a new SecondHalf action.
     * Look at the ActionBoard before using this.
     */
    public SecondHalf()
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
        if (data.firstHalf || data.secGameState == SecondaryGameState.PenaltyShootout) {
            data.firstHalf = false;
            data.secGameState = SecondaryGameState.Normal;
            if (data.colorChangeAuto) {
                data.team[0].teamColor = TeamColor.Blue;
                data.team[1].teamColor = TeamColor.Red;
            }
            FirstHalf.changeSide(data);
            data.kickOffTeam = (data.leftSideKickoff ? data.team[0].teamColor : data.team[1].teamColor);
            data.playMode = PlayMode.Initial;
            // Don't set data.whenCurrentPlayModeBegan, because it's used to count the pause
            Log.state(data, "2nd Half");
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
        return (!data.firstHalf && data.secGameState == SecondaryGameState.Normal)
            || (data.secGameState == SecondaryGameState.Normal && data.playMode == PlayMode.Finished)
            || (data.testmode);
    }
}
