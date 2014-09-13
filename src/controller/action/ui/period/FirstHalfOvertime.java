package controller.action.ui.period;

import common.annotations.NotNull;
import controller.*;
import data.*;

/**
 * Sets the game to the first half of an overtime period.
 *
 * @author Michel Bartsch
 */
public class FirstHalfOvertime extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        if (!state.isFirstHalf() || state.is(Period.PenaltyShootout)) {
            state.setFirstHalf(true);
            state.setPeriod(Period.Overtime);
            state.setNextKickOffColor(game.initialKickOffColor());
            state.setPlayMode(PlayMode.Initial);
            FirstHalf.changeSide(game, state);
            game.pushState("1st Half Extra Time");
        }
    }

    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return (state.isFirstHalf() && state.is(Period.Overtime))
                || (game.settings().overtime
                    && game.isPlayOff()
                    && state.is(Period.Normal)
                    && state.is(PlayMode.Finished)
                    && !state.isFirstHalf()
                    && state.getTeam(TeamColor.Blue).getScore() == state.getTeam(TeamColor.Red).getScore()
                    && state.getTeam(TeamColor.Blue).getScore() > 0)
                || state.isTestMode();
    }
}
