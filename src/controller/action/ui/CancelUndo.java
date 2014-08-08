package controller.action.ui;

import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;

/**
 * Cancels an undo action in progress.
 * This a dummy action, because the undo buttons track
 * whether they created the previous event, and this is
 * one possibility to generate a different event.
 *
 * @author Thomas Roefer
 */
public class CancelUndo extends GCAction
{
    public CancelUndo()
    {
      super(ActionType.UI);
    }

    @Override
    public void perform(GameState state)
    {}
    
    @Override
    public boolean isLegal(GameState state)
    {
        return true;
    }
}