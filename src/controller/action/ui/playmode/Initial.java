package controller.action.ui.playmode;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;
import data.PlayMode;
import rules.Rules;

/**
 * Sets play mode to {@link PlayMode#Initial}.
 *
 * @author Michel Bartsch
 */
public class Initial extends GCAction
{
    public Initial()
    {
        super(ActionType.UI);
    }

    @Override
    public void perform(@NotNull GameState state, @Nullable String message)
    {
        if (state.playMode != PlayMode.Initial) {
            forcePerform(state, "Initial");
        }
    }

    /**
     * Performs this action, even if the current play mode is {@link PlayMode#Initial}.
     *
     * @param data The current data to work on.
     * @param message the message to associate with this action
     */
    public void forcePerform(GameState data, String message)
    {
        if (Rules.league.returnRobotsInGameStoppages) {
            data.resetPenaltyTimes();
        }
        data.whenCurrentPlayModeBegan = data.getTime();
        data.playMode = PlayMode.Initial;
        log(data, message, message);
    }

    @Override
    public boolean isLegal(GameState state)
    {
        return state.playMode == PlayMode.Initial || state.testmode;
    }
}