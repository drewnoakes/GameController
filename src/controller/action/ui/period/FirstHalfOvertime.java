package controller.action.ui.period;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.GameState;
import data.*;

/**
 * This action means that the half is to be set to the first half of overtime.
 *
 * @author Michel Bartsch
 */
public class FirstHalfOvertime extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        if (!state.firstHalf || state.period == Period.PenaltyShootout) {
            state.firstHalf = true;
            state.period = Period.Overtime;
            state.nextKickOffColor = game.initialKickOffColor();
            state.playMode = PlayMode.Initial;
            FirstHalf.changeSide(game, state);
            game.pushState("1st Half Extra Time");
        }
    }

    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return (state.firstHalf && state.period == Period.Overtime)
                || (game.settings().overtime
                    && game.isPlayOff()
                    && state.period == Period.Normal
                    && state.playMode == PlayMode.Finished
                    && !state.firstHalf
                    && state.teams[0].score == state.teams[1].score
                    && state.teams[0].score > 0)
                || state.testmode;
    }
}
