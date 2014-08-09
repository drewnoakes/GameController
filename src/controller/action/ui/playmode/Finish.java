package controller.action.ui.playmode;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;
import data.PlayMode;
import rules.Rules;

/**
 * Sets play mode to {@link PlayMode#Finished}.
 *
 * @author Michel Bartsch
 */
public class Finish extends GCAction
{
    public Finish()
    {
        super(ActionType.UI);
    }

    @Override
    public void perform(@NotNull GameState state, @Nullable String message)
    {
        if (state.playMode == PlayMode.Finished) {
            return;
        }
        if (Rules.league.returnRobotsInGameStoppages) {
            state.resetPenaltyTimes();
        }
        state.addTimeInCurrentPlayMode();
        state.whenCurrentPlayModeBegan = state.getTime();
        state.playMode = PlayMode.Finished;
        log(state, message, "Finished");
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return state.playMode == PlayMode.Ready
            || state.playMode == PlayMode.Set
            || state.playMode == PlayMode.Playing
            || state.playMode == PlayMode.Finished
            || state.testmode;
    }
}