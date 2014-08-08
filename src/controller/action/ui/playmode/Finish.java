package controller.action.ui.playmode;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;
import data.PlayMode;
import rules.Rules;


/**
 * Sets play mode to @{link PlayMode#Finished}.
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
    public void perform(GameState state)
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
        Log.state(state, "Finished");
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