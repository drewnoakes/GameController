package controller.action.ui;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.action.ActionTrigger;
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
      super(ActionTrigger.User);
    }

    @Override
    public void perform(@NotNull GameState state, @Nullable String message)
    {}
}