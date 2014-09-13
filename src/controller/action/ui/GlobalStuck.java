package controller.action.ui;

import common.annotations.NotNull;
import controller.*;
import controller.action.ActionBoard;
import controller.action.ActionTrigger;
import data.PlayMode;
import data.UISide;

/**
 * This action means that a global game stuck has occurred.
 *
 * @author Michel Bartsch
 */
public class GlobalStuck extends Action
{
    /** On which side. */
    @NotNull private final UISide side;

    /**
     * @param side on which side
     */
    public GlobalStuck(@NotNull UISide side)
    {
        this.side = side;
    }

    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        WriteableTeamState team = state.getTeam(side);

        state.setNextKickOffColor(team.getTeamColor().other());

        game.apply(ActionBoard.ready, ActionTrigger.User);

        if (state.getRemainingSeconds(state.getWhenCurrentPlayModeBegan(), game.settings().kickoffTime + game.settings().minDurationBeforeStuck) > 0) {
            game.pushState("Kickoff Goal " + team.getTeamColor());
        } else {
            game.pushState("Global Game Stuck, Kickoff " + state.getNextKickOffColor());
        }
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return state.is(PlayMode.Playing) || state.isTestMode();
    }
}
