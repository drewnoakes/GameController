package controller.action.ui;

import common.Log;
import controller.EventHandler;
import controller.action.ActionType;
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
        super(ActionType.UI);
        this.states = states;
    }

    @Override
    public void perform(GameState state)
    {
        if (EventHandler.getInstance().lastUIAction == this && !executed) {
            executed = true;
            Log.toFile("Undo " + states + " States to " + Log.goBack(states));
        } else {
            executed = false;
        }
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return true;
    }
}