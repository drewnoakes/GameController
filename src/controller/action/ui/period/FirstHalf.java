package controller.action.ui.period;

import common.annotations.NotNull;
import controller.*;
import data.*;

/**
 * This action means that the half is to be set to the first half.
 *
 * @author Michel Bartsch
 */
public class FirstHalf extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        if (!state.isFirstHalf() || state.is(Period.PenaltyShootout)) {
            state.setFirstHalf(true);
            state.setPeriod(Period.Normal);
            state.setNextKickOffColor(game.initialKickOffColor());
            state.setPlayMode(PlayMode.Initial);
            changeSide(game, state);
            // Don't set data.whenCurrentPlayModeBegan, because it's used to count the pause
            game.pushState("1st Half");
        }
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return (state.isFirstHalf() && state.is(Period.Normal)) || state.isTestMode();
    }
    
    /**
     * Switches sides for the teams, both for first to second and also second to first half if needed.
     *
     * @param game the game being played.
     * @param state the current game state to work on.
     */
    public static void changeSide(Game game, WriteableGameState state)
    {
        // TODO add option to control this, as GC v1.2 seemed to always (?) negate the flip somewhere else
        game.uiOrientation().flip();

        // If necessary, swap team colors
        if (!state.is(Period.PenaltyShootout) && game.changeColoursEachPeriod()) {

            WriteableTeamState left = state.getTeam(UISide.Left);
            WriteableTeamState right = state.getTeam(UISide.Right);

            left.setTeamColor(left.getTeamColor().other());
            right.setTeamColor(right.getTeamColor().other());
        }

        // If necessary, clear the timeout flags of both teams
        if (game.settings().timeOutPerHalf && !state.is(Period.PenaltyShootout)) {
            state.getTeam(UISide.Left).setTimeOutTaken(false);
            state.getTeam(UISide.Right).setTimeOutTaken(false);
        }
        
        state.setTimeBeforeCurrentPlayMode(0);
        state.setWhenDropIn(0);
        state.resetPenalties();
    }
}
