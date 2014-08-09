package controller.action.ui;

import common.annotations.NotNull;
import common.annotations.Nullable;
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
    private final int side;
    /** This value will be added to the score. Normally will be one, but may vary if in test mode for example. */
    private final int set;

    /**
     * @param side on which side (0:left, 1:right)
     * @param set the amount to increment the score by
     */
    public Goal(int side, int set)
    {
        super(ActionType.UI);

        assert(set == 1 || set == -1);

        this.side = side;
        this.set = set;
    }

    @Override
    public void perform(@NotNull GameState state, @Nullable String message)
    {
        state.team[side].score += set;

        if (set == 1) {
            if (state.period != Period.PenaltyShootout) {
                state.kickOffTeam = state.team[side].teamColor.other();
                ActionBoard.ready.perform(state, "Goal for " + state.team[side].teamColor);
            } else {
                state.team[side].singleShots += 1 << state.team[side].penaltyShot - 1;
                ActionBoard.finish.perform(state, "Goal for " + state.team[side].teamColor);
            }
        } else {
            log(state, message, "Goal decrease for " + state.team[side].teamColor);
        }
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return (set == 1
              && state.playMode == PlayMode.Playing
              && (state.period != Period.PenaltyShootout || state.kickOffTeam == state.team[side].teamColor))
            || state.testmode;
    }
}
