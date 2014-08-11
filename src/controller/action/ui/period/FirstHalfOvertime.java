package controller.action.ui.period;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import data.*;
import rules.Rules;

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
            if (state.colorChangeAuto) {
                state.team[0].teamColor = TeamColor.Blue;
                state.team[1].teamColor = TeamColor.Red;
            }
            FirstHalf.changeSide(state);
            state.kickOffTeam = (state.leftSideKickoff ? state.team[0].teamColor : state.team[1].teamColor);
            state.playMode = PlayMode.Initial;
            game.pushState("1st Half Extra Time");
        }
    }

    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return (state.firstHalf && state.period == Period.Overtime)
                || (Rules.league.overtime
                    && state.playoff
                    && state.period == Period.Normal
                    && state.playMode == PlayMode.Finished
                    && !state.firstHalf
                    && state.team[0].score == state.team[1].score
                    && state.team[0].score > 0)
                || state.testmode;
    }
}
