package controller.action.ui.period;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import data.*;
import rules.Rules;

/**
 * This action means that the half is to be set to the first half.
 *
 * @author Michel Bartsch
 */
public class FirstHalf extends GCAction
{
    /**
     * Creates a new FirstHalf action.
     * Look at the ActionBoard before using this.
     */
    public FirstHalf()
    {
        super(ActionType.UI);
    }

    /**
     * Performs this action to manipulate the data (model).
     *
     * @param state      The current data to work on.
     */
    @Override
    public void perform(GameState state)
    {
        if (!state.firstHalf || state.period == Period.PenaltyShootout) {
            state.firstHalf = true;
            state.period = Period.Normal;
            changeSide(state);
            state.kickOffTeam = (state.leftSideKickoff ? state.team[0].teamColor : state.team[1].teamColor);
            state.playMode = PlayMode.Initial;
            // Don't set data.whenCurrentPlayModeBegan, because it's used to count the pause
            Log.state(state, "1st Half");
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
        return (state.firstHalf && state.period == Period.Normal)
                || state.testmode;
    }
    
    /**
     * Switches sides for the teams, both for first to second and also second to first half if needed.
     * 
     * @param state the current game state to work on.
     */
    public static void changeSide(GameState state)
    {
        TeamInfo team = state.team[0];
        state.team[0] = state.team[1];
        state.team[1] = team;
        boolean[] ejected = state.ejected[0];
        state.ejected[0] = state.ejected[1];
        state.ejected[1] = ejected;

        // if necessary, swap team colors
        if (state.period != Period.PenaltyShootout && state.colorChangeAuto) {
            TeamColor color = state.team[0].teamColor;
            state.team[0].teamColor = state.team[1].teamColor;
            state.team[1].teamColor = color;
        }

        if (Rules.league.timeOutPerHalf && (state.period != Period.PenaltyShootout)) {
            state.timeOutTaken = new boolean[] {false, false};
        } else {
            boolean timeOutTaken = state.timeOutTaken[0];
            state.timeOutTaken[0] = state.timeOutTaken[1];
            state.timeOutTaken[1] = timeOutTaken;
        }
        
        state.timeBeforeCurrentPlayMode = 0;
        state.whenDropIn = 0;
        state.resetPenalties();
    }
}
