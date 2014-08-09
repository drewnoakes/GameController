package controller.action.ui.playmode;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.action.ActionType;
import controller.action.GCAction;
import controller.action.ui.period.FirstHalf;
import data.*;
import rules.Rules;

/**
 * Sets play mode to {@link PlayMode#Set}.
 *
 * @author Michel Bartsch
 */
public class Set extends GCAction
{
    public Set()
    {
        super(ActionType.UI);
    }

    @Override
    public void perform(@NotNull GameState state, @Nullable String message)
    {
        if (state.playMode == PlayMode.Set) {
            return;
        }
        if (Rules.league.returnRobotsInGameStoppages) {
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
        log(state, message, "Set");
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return state.playMode == PlayMode.Ready
            || state.playMode == PlayMode.Set
            || (state.period == Period.PenaltyShootout
              && (state.playMode != PlayMode.Playing || Rules.league.penaltyShotRetries)
              && !state.timeOutActive[0]
              && !state.timeOutActive[1]
              && !state.refereeTimeout)
            || state.testmode;
    }
}
