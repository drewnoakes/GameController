package controller.action.clock;

import common.annotations.NotNull;
import controller.*;
import controller.action.ActionBoard;
import controller.action.ActionTrigger;
import data.PlayMode;

/**
 * This action means that some time has passed.
 *
 * @author Michel Bartsch
 */
public class ClockTick extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        if (state.getPlayMode() == PlayMode.Ready
               && state.getSecondsSince(state.getWhenCurrentPlayModeBegan()) >= game.settings().readyTime) {
            game.apply(ActionBoard.set, ActionTrigger.Clock);
        } else if (state.getPlayMode() == PlayMode.Finished) {
            Integer remainingPauseTime = state.getRemainingPauseTime();
            if (remainingPauseTime != null) {
                if (state.isFirstHalf() && remainingPauseTime <= game.settings().pauseTime / 2) {
                    game.apply(ActionBoard.secondHalf, ActionTrigger.Clock);
                } else if (!state.isFirstHalf() && remainingPauseTime <= game.settings().pausePenaltyShootOutTime / 2) {
                    game.apply(ActionBoard.secondHalf, ActionTrigger.Clock);
                }
            }
        }
    }

    /**
     * Gets whether the clock should be running given the current GameState.
     *
     * @param game
     * @param state the game state to consider
     * @return true if the clock should be running, otherwise false
     */
    public boolean isClockRunning(Game game, @NotNull ReadOnlyGameState state)
    {
        boolean halfNotStarted = state.getTimeBeforeCurrentPlayMode() == 0 && state.getPlayMode() != PlayMode.Playing;
        return
          !(state.getPlayMode() == PlayMode.Initial
             || state.getPlayMode() == PlayMode.Finished
             || ((state.getPlayMode() == PlayMode.Ready || state.getPlayMode() == PlayMode.Set)
                 && ((game.isPlayOff() && game.settings().playOffTimeStop) || halfNotStarted))
             || state.isManPause())
         || state.isManPlay();
    }
}
