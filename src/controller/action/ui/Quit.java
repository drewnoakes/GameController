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
    /**
     * Creates a new Quit action.
     * Look at the ActionBoard before using this.
     */
    public Quit()
    {
        super(ActionType.UI);
    }

    /**
     * Performs this action to manipulate the data (model).
     * 
     * @param data      The current data to work on.
     */
    @Override
    public void perform(GameState data)
    {
        if (EventHandler.getInstance().lastUIEvent == this) {
            Clock.getInstance().stop();
        }
    }
    
    /**
     * Checks if this action is legal with the given data (model).
     * Illegal actions are not performed by the EventHandler.
     * 
     * @param data      The current data to check with.
     */
    @Override
    public boolean isLegal(GameState data)
    {
        return true;
    }
}