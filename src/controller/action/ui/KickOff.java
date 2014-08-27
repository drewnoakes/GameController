package controller.action.ui;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.GameState;
import data.Period;
import data.PlayMode;

/**
 * This action means that a team get kickoff.
 *
 * @author Michel Bartsch
 */
public class KickOff extends Action
{
    /** On which side (0:left, 1:right) */
    private final int side;
    
    /**
     * @param side on which side (0:left, 1:right)
     */
    public KickOff(int side)
    {
        this.side = side;
    }

    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        if (state.nextKickOffColor == state.teams[side].teamColor) {
            return;
        }
        state.nextKickOffColor = state.teams[side].teamColor;
        if (game.settings().kickoffChoice
                && state.period == Period.Normal
                && state.firstHalf
                && state.playMode == PlayMode.Initial) {
            state.leftSideKickoff = side == 0;
        }
        game.pushState("Kickoff " + state.teams[side].teamColor);
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return state.nextKickOffColor == state.teams[side].teamColor
                || (game.settings().kickoffChoice
                    && state.period == Period.Normal
                    && state.firstHalf
                    && state.playMode == PlayMode.Initial)
                || state.testmode;
    }
}
