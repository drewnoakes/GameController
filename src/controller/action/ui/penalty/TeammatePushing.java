package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.*;
import data.PlayMode;
import data.UISide;

public class TeammatePushing extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull WriteableGameState state, @NotNull WriteableTeamState team,
                                @NotNull WriteablePlayerState player, @NotNull UISide side)
    {
        game.pushState("Teammate Pushing " + team.getTeamColor() + " " + player.getUniformNumber());
    }

    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return game.settings().dropInPlayerMode
                && (state.getPlayMode() == PlayMode.Ready || state.getPlayMode() == PlayMode.Playing)
                || state.isTestMode();
    }
}
