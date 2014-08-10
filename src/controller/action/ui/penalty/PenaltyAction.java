package controller.action.ui.penalty;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.ActionHandler;
import controller.action.ActionTrigger;
import controller.action.GCAction;
import data.GameState;

/**
 *
 * @author Michel-Zen
 */
public abstract class PenaltyAction extends GCAction
{
    protected PenaltyAction()
    {
        super(ActionTrigger.User);
    }

    @Override
    public void perform(@NotNull GameState state, @Nullable String message)
    {
        if (ActionHandler.getInstance().lastUserAction == this) {
            ActionHandler.getInstance().noLastUserAction = true;
        }
    }
}
