package controller.action.ui.playmode;

import common.annotations.NotNull;
import controller.*;
import controller.action.ui.period.FirstHalf;
import data.Period;
import data.PlayMode;
import data.TeamColor;

/**
 * Sets play mode to {@link PlayMode#Set}.
 *
 * @author Michel Bartsch
 */
public class Set extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        if (state.is(PlayMode.Set)) {
            return;
        }

        if (game.rules().arePenaltiesClearedDuringStoppages()) {
            state.setRemainingPenaltyTimesToZero();
        }

        if (!game.isPlayOff() && state.getTimeBeforeCurrentPlayMode() != 0) {
            state.addTimeInCurrentPlayMode();
        }

        state.setWhenCurrentPlayModeBegan(state.getTime());

        if (state.is(Period.PenaltyShootout)) {
            state.setTimeBeforeCurrentPlayMode(0);
            if (!state.is(PlayMode.Initial)) {
                TeamColor nextKickOffColor = state.getNextKickOffColor();
                if (nextKickOffColor == null)
                    nextKickOffColor = TeamColor.Blue;
                state.setNextKickOffColor(nextKickOffColor.other());
                FirstHalf.changeSide(game, state);
            }

            if (!state.is(PlayMode.Playing)) {
                // Increment the kick-off team's penalty shot count
                WriteableTeamState team = state.getTeam(state.getNextKickOffColor());
                team.setPenaltyShotCount(team.getPenaltyShotCount() + 1);
            }
        }

        state.setPlayMode(PlayMode.Set);
        game.pushState("Set");
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        if (state.is(PlayMode.Ready, PlayMode.Set))
            return true;

        if (state.isTestMode())
            return true;

        if (state.is(Period.PenaltyShootout))
        {
            return (!state.is(PlayMode.Playing) || game.rules().arePenaltyShotRetriesAllowed()) &&
                   !state.isTimeoutActive() &&
                   !state.isRefereeTimeoutActive();
        }

        return false;
    }
}
