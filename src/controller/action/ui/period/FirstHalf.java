package controller.action.ui.period;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import data.*;

/**
 * This action means that the half is to be set to the first half.
 *
 * @author Michel Bartsch
 */
public class FirstHalf extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        if (!state.firstHalf || state.period == Period.PenaltyShootout) {
            state.firstHalf = true;
            state.period = Period.Normal;
            changeSide(state);
            state.kickOffTeam = (state.leftSideKickoff ? state.team[0].teamColor : state.team[1].teamColor);
            state.playMode = PlayMode.Initial;
            // Don't set data.whenCurrentPlayModeBegan, because it's used to count the pause
            game.pushState("1st Half");
        }
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return (state.firstHalf && state.period == Period.Normal) || state.testmode;
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

        if (Game.settings.timeOutPerHalf && (state.period != Period.PenaltyShootout)) {
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
