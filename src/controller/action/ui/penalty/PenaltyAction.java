package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.action.RobotAction;
import data.GameState;
import data.PlayMode;

/**
 * Abstract base class for penalty actions which apply to single robots.
 *
 * @author Michel-Zen
 * @author Drew Noakes https://drewnoakes.com
 */
public abstract class PenaltyAction extends Action implements RobotAction
{
    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        // If the last user action was to select this penalty, we want to clear that selection
        if (game.getLastUserAction() == this) {
            game.clearLastUserAction();
        }
    }

    /**
     * Unless overridden, penalty actions are legal when {@link PlayMode#Playing}
     * or when test mode is active.
     */
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return state.playMode == PlayMode.Playing || state.testmode;
    }
}
