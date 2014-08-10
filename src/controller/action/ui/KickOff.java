package controller.action.ui;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.action.ActionTrigger;
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
    private final int side;
    
    /**
     * @param side on which side (0:left, 1:right)
     */
    public KickOff(int side)
    {
        super(ActionTrigger.User);
        this.side = side;
    }

    @Override
    public void perform(@NotNull GameState state, @Nullable String message)
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
        log(state, message, "Kickoff " + state.team[side].teamColor);
    }
    
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
