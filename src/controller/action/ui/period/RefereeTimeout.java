package controller.action.ui.period;

import common.annotations.NotNull;
import controller.*;
import controller.action.ActionBoard;
import controller.action.ActionTrigger;
import data.PlayMode;
import data.Period;

public class RefereeTimeout extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        if (!state.isRefereeTimeoutActive()) {
            state.setPreviousPeriod(state.getPeriod());
            state.setPeriod(Period.Timeout);
            state.setRefereeTimeoutActive(true);
            if (state.is(PlayMode.Playing)) {
                state.addTimeInCurrentPlayMode();
            }
            if (state.getPreviousPeriod() == Period.PenaltyShootout
                    && (state.is(PlayMode.Set) || state.is(PlayMode.Playing))) {
                // Decrease the kick-off team's penalty shot count
                // TODO why do we do this? explain with a comment
                WriteableTeamState kickOffTeam = state.getTeam(state.getNextKickOffColor());
                kickOffTeam.setPenaltyShotCount(kickOffTeam.getPenaltyShotCount() - 1);
            }
            game.apply(ActionBoard.initial, ActionTrigger.User);
            game.pushState("Referee Timeout");
        } else {
            state.setPeriod(state.getPreviousPeriod());
            state.setPreviousPeriod(Period.Timeout);
            state.setRefereeTimeoutActive(false);
            if (!state.is(Period.PenaltyShootout)) {
                game.apply(ActionBoard.ready, ActionTrigger.User);
                game.pushState("End of Referee Timeout");
            }
        }
    }

    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return !state.is(PlayMode.Finished) && !state.isTimeOutActive();
    }
}
