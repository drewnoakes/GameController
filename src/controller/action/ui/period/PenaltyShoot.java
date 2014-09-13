package controller.action.ui.period;

import common.annotations.NotNull;
import controller.*;
import data.PlayMode;
import data.Period;
import data.TeamColor;

/**
 * Moves the game into penalty shootouts.
 *
 * @author Michel Bartsch
 */
public class PenaltyShoot extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        if (!state.is(Period.PenaltyShootout)) {
            state.setPeriod(Period.PenaltyShootout);
            // Don't set whenCurrentPlayModeBegan, because it's used to count the pause
            state.setPlayMode(PlayMode.Initial);
            state.setTimeBeforeCurrentPlayMode(0);
            state.resetPenalties();

            if (game.settings().timeOutPerHalf) {
                state.getTeam(TeamColor.Blue).setTimeOutTaken(false);
                state.getTeam(TeamColor.Red).setTimeOutTaken(false);
            }
            game.pushState("Penalty Shoot-out");
        }
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return state.is(Period.PenaltyShootout)
          || state.getPreviousPeriod() == Period.PenaltyShootout
          || (!state.isFirstHalf()
            && state.is(PlayMode.Finished)
            && !(game.settings().overtime
                && game.isPlayOff()
                && state.is(Period.Normal)
                && state.getTeam(TeamColor.Blue).getScore() == state.getTeam(TeamColor.Red).getScore()
                && state.getTeam(TeamColor.Blue).getScore() > 0))
          || state.isTestMode();
    }
}
