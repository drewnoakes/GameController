package controller.action.ui;

import common.annotations.NotNull;
import controller.*;
import data.Period;
import data.PlayMode;
import data.UISide;

/**
 * Assigns kickoff to a team, indicated by UI side.
 *
 * @author Michel Bartsch
 */
public class KickOff extends Action
{
    private final UISide side;
    
    /**
     * @param side on which side
     */
    public KickOff(@NotNull UISide side)
    {
        this.side = side;
    }

    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        if (state.getNextKickOffColor() == state.getTeam(side).getTeamColor()) {
            return;
        }
        state.setNextKickOffColor(state.getTeam(side).getTeamColor());
        if (game.settings().kickoffChoice
                && state.getPeriod() == Period.Normal
                && state.isFirstHalf()
                && state.getPlayMode() == PlayMode.Initial) {
            state.setLeftSideKickoff(side == UISide.Left);
        }
        game.pushState("Kickoff " + state.getTeam(side).getTeamColor());
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return state.getNextKickOffColor() == state.getTeam(side).getTeamColor()
                || (game.settings().kickoffChoice
                    && state.getPeriod() == Period.Normal
                    && state.isFirstHalf()
                    && state.getPlayMode() == PlayMode.Initial)
                || state.isTestMode();
    }
}
