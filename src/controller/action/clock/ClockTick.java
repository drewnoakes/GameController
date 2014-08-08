package controller.action.clock;

import controller.action.ActionBoard;
import controller.action.ActionType;
import controller.action.GCAction;
import data.AdvancedData;
import data.PlayMode;
import rules.Rules;


/**
 * This action means that some time has been passed.
 *
 * @author Michel Bartsch
 */
public class ClockTick extends GCAction
{
    /**
     * Creates a new ClockTick action.
     * Look at the ActionBoard before using this.
     */
    public ClockTick()
    {
        super(ActionType.CLOCK);
    }

    /**
     * Performs this action to manipulate the data (model).
     * 
     * @param data      The current data to work on.
     */
    @Override
    public void perform(AdvancedData data)
    {
        if (data.playMode == PlayMode.Ready
               && data.getSecondsSince(data.whenCurrentPlayModeBegan) >= Rules.league.readyTime) {
            ActionBoard.set.perform(data);
        } else if (data.playMode == PlayMode.Finished) {
            Integer remainingPauseTime = data.getRemainingPauseTime();
            if (remainingPauseTime != null) {
                if (data.firstHalf && remainingPauseTime <= Rules.league.pauseTime / 2) {
                    ActionBoard.secondHalf.perform(data);
                } else if (!data.firstHalf && remainingPauseTime <= Rules.league.pausePenaltyShootOutTime / 2) {
                    ActionBoard.penaltyShoot.perform(data);
                }
            }
        }
        data.updateCoachMessages();
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
        return true;
    }
    
    public boolean isClockRunning(AdvancedData data)
    {
        boolean halfNotStarted = data.timeBeforeCurrentPlayMode == 0 && data.playMode != PlayMode.Playing;
        return !((data.playMode == PlayMode.Initial)
         || (data.playMode == PlayMode.Finished)
         || (
                ((data.playMode == PlayMode.Ready)
               || (data.playMode == PlayMode.Set))
                && ((data.playoff && Rules.league.playOffTimeStop) || halfNotStarted)
                )
         || data.manPause)
         || data.manPlay;
    }
}
