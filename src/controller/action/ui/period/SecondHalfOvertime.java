package controller.action.ui.period;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.GameState;
import data.*;

/**
 * This action means that the half is to be set to the second half of overtime.
 *
 * @author Michel Bartsch
 */
public class SecondHalfOvertime extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        if (state.firstHalf || state.period == Period.PenaltyShootout) {
            state.firstHalf = false;
            state.period = Period.Overtime;
            if (game.options().colorChangeAuto) {
                state.team[0].teamColor = TeamColor.Blue;
                state.team[1].teamColor = TeamColor.Red;
            }
            FirstHalf.changeSide(state);
            state.kickOffTeam = (state.leftSideKickoff ? state.team[0].teamColor : state.team[1].teamColor);
            state.playMode = PlayMode.Initial;
            game.pushState("2nd Half Extra Time");
        }
    }

    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return (!state.firstHalf && state.period == Period.Overtime)
            || (state.period == Period.Overtime && state.playMode == PlayMode.Finished)
            || state.testmode;
    }
}
