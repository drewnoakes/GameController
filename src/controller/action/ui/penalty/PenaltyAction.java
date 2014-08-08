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
    public PenaltyAction()
    {
        super(ActionType.UI);
    }

    @Override
    public void perform(GameState state)
    {
        if (EventHandler.getInstance().lastUIAction == this) {
            EventHandler.getInstance().noLastUIAction = true;
        }
    }
}
