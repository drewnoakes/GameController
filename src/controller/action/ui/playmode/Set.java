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
        if (state.getPlayMode() == PlayMode.Set) {
            return;
        }

        if (game.settings().returnRobotsInGameStoppages) {
            state.resetPenaltyTimes();
        }

        if (!game.isPlayOff() && state.getTimeBeforeCurrentPlayMode() != 0) {
            state.addTimeInCurrentPlayMode();
        }

        state.setWhenCurrentPlayModeBegan(state.getTime());

        if (state.getPeriod() == Period.PenaltyShootout) {
            state.setTimeBeforeCurrentPlayMode(0);
            if (state.getPlayMode() != PlayMode.Initial) {
                TeamColor nextKickOffColor = state.getNextKickOffColor();
                if (nextKickOffColor == null)
                    nextKickOffColor = TeamColor.Blue;
                state.setNextKickOffColor(nextKickOffColor.other());
                FirstHalf.changeSide(game, state);
            }

            if (state.getPlayMode() != PlayMode.Playing) {
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
        return state.getPlayMode() == PlayMode.Ready
            || state.getPlayMode() == PlayMode.Set
            || (state.getPeriod() == Period.PenaltyShootout
              && (state.getPlayMode() != PlayMode.Playing || game.settings().penaltyShotRetries)
              && !state.isTimeOutActive()
              && !state.isRefereeTimeoutActive())
            || state.isTestMode();
    }
}
