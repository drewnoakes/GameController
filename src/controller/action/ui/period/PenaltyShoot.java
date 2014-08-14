package controller.action.ui.period;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.GameState;
import data.PlayMode;
import data.Period;

/**
 * This action means that a penalty shoot is to be starting.
 *
 * @author Michel Bartsch
 */
public class PenaltyShoot extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        if (state.period != Period.PenaltyShootout) {
            state.period = Period.PenaltyShootout;
            // Don't set data.whenCurrentPlayModeBegan, because it's used to count the pause
            state.playMode = PlayMode.Initial;
            state.timeBeforeCurrentPlayMode = 0;
            state.resetPenalties();
            if (game.settings().timeOutPerHalf) {
                state.timeOutTaken = new boolean[] {false, false};
            }
            game.pushState("Penalty Shoot-out");
        }
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return state.period == Period.PenaltyShootout
          || state.previousPeriod == Period.PenaltyShootout
          || (!state.firstHalf
            && state.playMode == PlayMode.Finished
            && !(game.settings().overtime
                && game.options().playOff
                && state.period == Period.Normal
                && state.team[0].score == state.team[1].score
                && state.team[0].score > 0))
          || state.testmode;
    }
}
