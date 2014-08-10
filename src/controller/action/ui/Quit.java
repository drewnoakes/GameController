package controller.action.ui;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.ActionHandler;
import controller.Clock;
import controller.action.ActionTrigger;
import controller.action.GCAction;
import data.GameState;

/**
 * This action means that the operator tries to close the GameController.
 *
 * @author Michel Bartsch
 */
public class Quit extends GCAction
{
    public Quit()
    {
        super(ActionTrigger.User);
    }

    @Override
    public void perform(@NotNull GameState state, @Nullable String message)
    {
        if (ActionHandler.getInstance().lastUserAction == this) {
            Clock.getInstance().stop();
        }
    }
}