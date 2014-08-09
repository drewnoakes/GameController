package controller.action.ui.penalty;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.EventHandler;
import controller.action.ActionType;
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
        super(ActionType.UI);
    }

    @Override
    public void perform(@NotNull GameState state, @Nullable String message)
    {
        if (EventHandler.getInstance().lastUIAction == this) {
            EventHandler.getInstance().noLastUIAction = true;
        }
    }
}
