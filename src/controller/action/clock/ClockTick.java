package controller.action.clock;

import controller.action.ActionBoard;
import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;
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
    public void perform(GameState state)
    {
        if (state.playMode == PlayMode.Ready
               && state.getSecondsSince(state.whenCurrentPlayModeBegan) >= Rules.league.readyTime) {
            ActionBoard.set.perform(state);
        } else if (state.playMode == PlayMode.Finished) {
            Integer remainingPauseTime = state.getRemainingPauseTime();
            if (remainingPauseTime != null) {
                if (state.firstHalf && remainingPauseTime <= Rules.league.pauseTime / 2) {
                    ActionBoard.secondHalf.perform(state);
                } else if (!state.firstHalf && remainingPauseTime <= Rules.league.pausePenaltyShootOutTime / 2) {
                    ActionBoard.penaltyShoot.perform(state);
                }
            }
        }
        state.updateCoachMessages();
    }
    
    /**
     * Checks if this action is legal with the given data (model).
     * Illegal actions are not performed by the EventHandler.
     * 
     * @param data      The current data to check with.
     */
    @Override
    public boolean isLegal(GameState state)
    {
        return true;
    }

    /**
     * Gets whether the clock should be running given the current GameState.
     *
     * @param state the game state to consider
     * @return true if the clock should be running, otherwise false
     */
    public boolean isClockRunning(GameState state)
    {
        boolean halfNotStarted = state.timeBeforeCurrentPlayMode == 0 && state.playMode != PlayMode.Playing;
        return
          !(state.playMode == PlayMode.Initial
             || state.playMode == PlayMode.Finished
             || ((state.playMode == PlayMode.Ready || state.playMode == PlayMode.Set)
                 && ((state.playoff && Rules.league.playOffTimeStop) || halfNotStarted))
             || state.manPause)
         || state.manPlay;
    }
}
