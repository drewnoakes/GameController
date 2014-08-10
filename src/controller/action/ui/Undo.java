package controller.action.ui;

import common.Log;
import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.ActionHandler;
import controller.action.ActionTrigger;
import controller.action.GCAction;
import data.GameState;

/**
 * This action means that the operator wants to go back in the timeline.
 *
 * @author Michel Bartsch
 */
public class Undo extends GCAction
{
    /** How far to go back in the timeline by this action. */
    private final int states;

    /** This is true, if this action has just been executed */
    public boolean executed = false;

    /**
     * @param states the number of actions to go back in the timeline
     */
    public Undo(int states)
    {
        super(ActionTrigger.User);
        assert(states > 0);
        this.states = states;
    }

    @Override
    public void perform(@NotNull GameState state, @Nullable String message)
    {
        if (ActionHandler.getInstance().lastUserAction == this && !executed) {
            executed = true;
            Log.toFile("Undo " + states + " States to " + Log.goBack(states));
        } else {
            executed = false;
        }
    }
}