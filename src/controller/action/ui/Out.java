package controller.action.ui;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.GameState;
import data.PlayMode;

/**
 * Updates the game state in response to the ball going out of play.
 *
 * @author Michel Bartsch
 */
public class Out extends Action
{
    /** Out on which side (0:left, 1:right) */
    private final int side;

    /**
     * @param side out on which side (0:left, 1:right)
     */
    public Out(int side)
    {
        this.side = side;
    }

    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        state.whenDropIn = state.getTime();
        state.lastDropInColor = state.team[side].teamColor;
        game.pushState("Out by " + state.team[side].teamColor);
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return state.playMode == PlayMode.Playing || state.testmode;
    }
}
