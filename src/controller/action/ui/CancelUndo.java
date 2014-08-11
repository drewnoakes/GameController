package controller.action.ui;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import data.GameState;

/**
 * Cancels an undo action in progress.
 * This a dummy action, because the undo buttons track
 * whether they created the previous event, and this is
 * one possibility to generate a different event.
 *
 * @author Thomas Roefer
 */
public class CancelUndo extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {}
}