package controller.action.ui;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;
import data.Period;
import data.PlayMode;
import rules.Rules;


/**
 * This action means that a team get kickoff.
 *
 * @author Michel Bartsch
 */
public class KickOff extends GCAction
{
    /** On which side (0:left, 1:right) */
    private int side;
    
    
    /**
     * Creates a new KickOff action.
     * Look at the ActionBoard before using this.
     * 
     * @param side      On which side (0:left, 1:right)
     */
    public KickOff(int side)
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
        if (state.kickOffTeam == state.team[side].teamColor) {
            return;
        }
        state.kickOffTeam = state.team[side].teamColor;
        if (Rules.league.kickoffChoice
                && state.period == Period.Normal
                && state.firstHalf
                && state.playMode == PlayMode.Initial) {
            state.leftSideKickoff = side == 0;
        }
        Log.state(state, "Kickoff " + state.team[side].teamColor);
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
        return state.kickOffTeam == state.team[side].teamColor
                || (Rules.league.kickoffChoice
                    && state.period == Period.Normal
                    && state.firstHalf
                    && state.playMode == PlayMode.Initial)
                || state.testmode;
    }
}
