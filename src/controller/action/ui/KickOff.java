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
        ReadOnlyTeamState team = state.getTeam(side);

        if (state.getNextKickOffColor() != team.getTeamColor()) {
            state.setNextKickOffColor(team.getTeamColor());
            game.pushState("Kickoff " + team.getTeamColor());
        }
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return state.getNextKickOffColor() == state.getTeam(side).getTeamColor()
                || (game.rules().isKickOffTeamChoosableAtStartOfGame()
                    && state.is(Period.Normal)
                    && state.isFirstHalf()
                    && state.is(PlayMode.Initial))
                || state.isTestMode();
    }
}
