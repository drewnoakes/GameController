package controller.action.clock;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.action.ActionBoard;
import controller.action.ActionTrigger;
import data.GameState;
import data.PlayMode;

/**
 * This action means that some time has passed.
 *
 * @author Michel Bartsch
 */
public class ClockTick extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        if (state.playMode == PlayMode.Ready
               && state.getSecondsSince(state.whenCurrentPlayModeBegan) >= Game.settings.readyTime) {
            game.apply(ActionBoard.set, ActionTrigger.Clock);
        } else if (state.playMode == PlayMode.Finished) {
            Integer remainingPauseTime = state.getRemainingPauseTime();
            if (remainingPauseTime != null) {
                if (state.firstHalf && remainingPauseTime <= Game.settings.pauseTime / 2) {
                    game.apply(ActionBoard.secondHalf, ActionTrigger.Clock);
                } else if (!state.firstHalf && remainingPauseTime <= Game.settings.pausePenaltyShootOutTime / 2) {
                    game.apply(ActionBoard.secondHalf, ActionTrigger.Clock);
                }
            }
        }

        state.updateCoachMessages();
    }

    /**
     * Gets whether the clock should be running given the current GameState.
     *
     * @param state the game state to consider
     * @return true if the clock should be running, otherwise false
     */
    public boolean isClockRunning(@NotNull GameState state)
    {
        boolean halfNotStarted = state.timeBeforeCurrentPlayMode == 0 && state.playMode != PlayMode.Playing;
        return
          !(state.playMode == PlayMode.Initial
             || state.playMode == PlayMode.Finished
             || ((state.playMode == PlayMode.Ready || state.playMode == PlayMode.Set)
                 && ((state.playoff && Game.settings.playOffTimeStop) || halfNotStarted))
             || state.manPause)
         || state.manPlay;
    }
}
