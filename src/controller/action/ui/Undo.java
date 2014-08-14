package controller.action.ui;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.GameState;

/**
 * Reverts the game state by one or more states from the timeline.
 *
 * @author Michel Bartsch
 */
public class Undo extends Action
{
    /** How far to go back in the timeline by this action. */
    private final int count;

    private boolean isPreview;

    /**
     * @param count the number of actions to revert in the timeline
     */
    public Undo(int count)
    {
        assert(count > 0);
        this.count = count;
    }

    /**
     * Gets whether this action was most recently ran to preview an undo operation.
     * <p>
     * Undo operations require two presses. The first on any button causes this
     * value to be <code>true</code>.
     */
    public boolean isPreview()
    {
        return isPreview;
    }

    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        if (game.getLastUserAction() == this) {
            game.undo(count);
            game.clearLastUserAction();
            isPreview = false;
        } else {
            isPreview = true;
        }
    }
}