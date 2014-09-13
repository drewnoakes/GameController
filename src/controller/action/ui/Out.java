package controller.action.ui;

import common.annotations.NotNull;
import controller.*;
import data.PlayMode;
import data.UISide;

/**
 * Updates the game state in response to the ball going out of play.
 *
 * @author Michel Bartsch
 */
public class Out extends Action
{
    /** Out on which side. */
    private final UISide side;

    /**
     * @param side out on which side
     */
    public Out(UISide side)
    {
        this.side = side;
    }

    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        state.setWhenDropIn(state.getTime());
        state.setLastDropInColor(state.getTeam(side).getTeamColor());
        game.pushState("Out by " + state.getTeam(side).getTeamColor());
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return state.is(PlayMode.Playing) || state.isTestMode();
    }
}
