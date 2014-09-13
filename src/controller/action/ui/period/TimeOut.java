package controller.action.ui.period;

import common.annotations.NotNull;
import controller.*;
import controller.action.ActionBoard;
import data.*;

/**
 * Starts or ends a timeout for a particular team.
 *
 * @author Michel Bartsch
 */
public class TimeOut extends Action
{
    /** The side calling the timeout. */
    @NotNull private final UISide side;

    /**
     * @param side the side calling the timeout.
     */
    public TimeOut(@NotNull UISide side)
    {
        this.side = side;
    }

    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        WriteableTeamState team = state.getTeam(side);

        if (!team.isTimeOutActive()) {
            // Starting a timeout
            state.setPreviousPeriod(state.getPeriod());
            state.setPeriod(Period.Timeout);
            team.setTimeOutActive(true);
            team.setTimeOutTaken(true);

            if (state.getPreviousPeriod() != Period.PenaltyShootout) {
                if (game.settings().giveOpponentKickOffOnTimeOut)
                    state.setNextKickOffColor(team.getTeamColor().other());
            } else if (state.is(PlayMode.Set)) {
                // Decrease the kick-off team's penalty shot count
                // TODO why do we do this? explain with a comment
                WriteableTeamState kickOffTeam = state.getTeam(state.getNextKickOffColor());
                kickOffTeam.setPenaltyShotCount(kickOffTeam.getPenaltyShotCount() - 1);
            }

            ActionBoard.initial.forceExecute(game, state);
            game.pushState("Timeout " + team.getTeamColor());
        } else {
            // Completing a timeout
            state.setPeriod(state.getPreviousPeriod());
            state.setPreviousPeriod(Period.Timeout);
            team.setTimeOutActive(false);
            if (state.getPeriod() != Period.PenaltyShootout) {
                ActionBoard.ready.forceExecute(game, state);
                game.pushState("End of Timeout " + team.getTeamColor());
            }
        }
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        ReadOnlyTeamState team = state.getTeam(side);
        ReadOnlyTeamState otherTeam = state.getTeam(side.other());

        return team.isTimeOutActive()
            || ((state.is(PlayMode.Initial) ||
                  state.is(PlayMode.Ready) ||
                  state.is(PlayMode.Set))
                && !team.isTimeOutTaken()
                && !otherTeam.isTimeOutActive()
                && state.getPeriod() != Period.Timeout)
            || state.isTestMode();
    }
}
