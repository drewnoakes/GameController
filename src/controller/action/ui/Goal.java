package controller.action.ui;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.GameState;
import controller.action.ActionBoard;
import controller.action.ActionTrigger;
import data.*;

/**
 * This action means that a team has scored or its score is to be decreased.
 *
 * @author Michel Bartsch
 */
public class Goal extends Action
{
    /** On which side (0:left, 1:right) */
    private final int side;

    /** This value will be added to the score. Normally will be one, but may vary if in test mode for example. */
    private final int set;

    /**
     * @param side on which side (0:left, 1:right)
     * @param set the amount to increment the score by
     */
    public Goal(int side, int set)
    {
        assert(set == 1 || set == -1);

        this.side = side;
        this.set = set;
    }

    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        // Don't allow the score to be made negative
        if (set < 0 && state.teams[side].score == 0)
            return;

        state.teams[side].score += set;

        if (set == 1) {
            if (state.period != Period.PenaltyShootout) {
                state.nextKickOffColor = state.teams[side].teamColor.other();
                ActionBoard.ready.forceExecute(game, state);
                game.pushState("Goal for " + state.teams[side].teamColor);
            } else {
                state.teams[side].singleShots += 1 << state.teams[side].penaltyShot - 1;
                game.apply(ActionBoard.finish, ActionTrigger.User);
                game.pushState("Goal for " + state.teams[side].teamColor);
            }
        } else {
            game.pushState("Goal decrease for " + state.teams[side].teamColor);
        }
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return (set == 1
              && state.playMode == PlayMode.Playing
              && (state.period != Period.PenaltyShootout || state.nextKickOffColor == state.teams[side].teamColor))
            || state.testmode;
    }
}
