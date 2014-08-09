package controller.action.ui;

import controller.Clock;
import controller.EventHandler;
import controller.action.ActionType;
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
        super(ActionType.UI);
    }

    @Override
    public void perform(GameState state, String message)
    {
        if (EventHandler.getInstance().lastUIAction == this) {
            Clock.getInstance().stop();
        }
    }
}