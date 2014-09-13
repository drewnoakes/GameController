package controller.action.ui;

import common.annotations.NotNull;
import controller.*;
import controller.action.ActionBoard;
import controller.action.ActionTrigger;
import data.*;

/**
 * Adjusts a team's score. May be in response to a goal, or due to test mode.
 *
 * @author Michel Bartsch
 */
public class Goal extends Action
{
    private final UISide side;
    private final int delta;

    /**
     * @param side the side of the team to change score of
     * @param delta the amount to modify the score by
     */
    public Goal(UISide side, int delta)
    {
        assert(delta == 1 || delta == -1);

        this.side = side;
        this.delta = delta;
    }

    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        WriteableTeamState team = state.getTeam(side);

        int newScore = team.getScore() + delta;

        // Don't allow the score to be made negative
        if (newScore < 0)
            return;

        // Update the score
        team.setScore(newScore);

        if (delta == 1) {
            if (!state.is(Period.PenaltyShootout)) {
                state.setNextKickOffColor(team.getTeamColor().other());
                ActionBoard.ready.forceExecute(game, state);
                game.pushState("Goal for " + team.getTeamColor());
            } else {
                team.addPenaltyGoal();
                game.apply(ActionBoard.finish, ActionTrigger.User);
                game.pushState("Goal for " + team.getTeamColor());
            }
        } else {
            game.pushState("Goal decrease for " + team.getTeamColor());
        }
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return (delta == 1
              && state.is(PlayMode.Playing)
              && (!state.is(Period.PenaltyShootout) || state.getNextKickOffColor() == state.getTeam(side).getTeamColor()))
            || state.isTestMode();
    }
}
