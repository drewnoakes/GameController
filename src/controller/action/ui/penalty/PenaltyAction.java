package controller.action.ui.penalty;

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
    /**
     * Creates a new Defender action.
     * Look at the ActionBoard before using this.
     */
    public PenaltyAction()
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
            EventHandler.getInstance().noLastUIEvent = true;
        }
    }
}
