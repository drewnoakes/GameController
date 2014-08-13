package controller.action.ui.playmode;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.action.ui.period.FirstHalf;
import data.GameState;
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
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        if (state.playMode == PlayMode.Set) {
            return;
        }
        if (Game.settings.returnRobotsInGameStoppages) {
            state.resetPenaltyTimes();
        }
        if (!state.playoff && state.timeBeforeCurrentPlayMode != 0) {
            state.addTimeInCurrentPlayMode();
        }
        state.whenCurrentPlayModeBegan = state.getTime();

        if (state.period == Period.PenaltyShootout) {
            state.timeBeforeCurrentPlayMode = 0;
            if (state.playMode != PlayMode.Initial) {
                state.kickOffTeam = state.kickOffTeam == TeamColor.Blue ? TeamColor.Red : TeamColor.Blue;
                FirstHalf.changeSide(state);
            }

            if (state.playMode != PlayMode.Playing) {
                state.team[state.team[0].teamColor == state.kickOffTeam ? 0 : 1].penaltyShot++;
            }
        }
        state.playMode = PlayMode.Set;
        game.pushState("Set");
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return state.playMode == PlayMode.Ready
            || state.playMode == PlayMode.Set
            || (state.period == Period.PenaltyShootout
              && (state.playMode != PlayMode.Playing || Game.settings.penaltyShotRetries)
              && !state.timeOutActive[0]
              && !state.timeOutActive[1]
              && !state.refereeTimeout)
            || state.testmode;
    }
}
