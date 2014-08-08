package controller.action.ui;

import common.Log;
import controller.action.ActionBoard;
import controller.action.ActionType;
import controller.action.GCAction;
import data.*;

/**
 * This action means that a team has scored or its score is to be decreased.
 *
 * @author Michel Bartsch
 */
public class Goal extends GCAction
{
    /** On which side (0:left, 1:right) */
    private int side;
    /** This value will be added to the score. */
    private int set;
    
    
    /**
     * Creates a new Goal action.
     * Look at the ActionBoard before using this.
     * 
     * @param side      On which side (0:left, 1:right)
     * @param set       This value will be added to the score.
     */
    public Goal(int side, int set)
    {
        super(ActionType.UI);
        this.side = side;
        this.set = set;
    }

    /**
     * Performs this action to manipulate the data (model).
     *
     * @param state      The current data to work on.
     */
    @Override
    public void perform(GameState state)
    {
        state.team[side].score += set;
        if (set == 1) {
            if (state.period != Period.PenaltyShootout) {
                state.kickOffTeam = state.team[side].teamColor.other();
                Log.setNextMessage("Goal for "+ state.team[side].teamColor);
                ActionBoard.ready.perform(state);
            } else {
                state.team[side].singleShots += (1<<(state.team[side].penaltyShot-1));
                Log.setNextMessage("Goal for "+ state.team[side].teamColor);
                ActionBoard.finish.perform(state);
            }
        } else {
            Log.state(state, "Goal decrease for " + state.team[side].teamColor);
        }
    }
    
    /**
     * Checks if this action is legal with the given data (model).
     * Illegal actions are not performed by the EventHandler.
     *
     * @param state      The current data to check with.
     */
    @Override
    public boolean isLegal(GameState state)
    {
        return (set == 1
              && state.playMode == PlayMode.Playing
              && (state.period != Period.PenaltyShootout || state.kickOffTeam == state.team[side].teamColor))
            || state.testmode;
    }
}
