package controller.action.ui;

import common.Log;
import controller.action.ActionBoard;
import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;
import data.PlayMode;
import rules.Rules;


/**
 * This action means that a global game stuck has occurred.
 *
 * @author Michel Bartsch
 */
public class GlobalStuck extends GCAction
{
    /** On which side (0:left, 1:right) */
    private int side;

    /**
     * Creates a new GlobalStuck action.
     * Look at the ActionBoard before using this.
     * 
     * @param side      On which side (0:left, 1:right)
     */
    public GlobalStuck(int side)
    {
        super(ActionType.UI);
        this.side = side;
    }

    /**
     * Performs this action to manipulate the data (model).
     *
     * @param state      The current data to work on.
     */
    @Override
    public void perform(GameState state)
    {
        state.kickOffTeam = state.team[side == 0 ? 1 : 0].teamColor;
        if (state.getRemainingSeconds(state.whenCurrentPlayModeBegan, Rules.league.kickoffTime + Rules.league.minDurationBeforeStuck) > 0) {
            Log.setNextMessage("Kickoff Goal "+ state.team[side].teamColor);
        } else {
            Log.setNextMessage("Global Game Stuck, Kickoff "+ state.kickOffTeam);
        }
        ActionBoard.ready.perform(state);
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
        return state.playMode == PlayMode.Playing || state.testmode;
    }
}
