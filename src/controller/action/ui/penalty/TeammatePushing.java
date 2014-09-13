package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.*;
import data.PlayMode;

public class TeammatePushing extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull WriteableGameState state, @NotNull WriteableTeamState team,
                                @NotNull WriteablePlayerState player)
    {
        game.pushState("Teammate Pushing " + team.getTeamColor() + " " + player.getUniformNumber());
    }

    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return game.settings().dropInPlayerMode && state.is(PlayMode.Ready, PlayMode.Playing)
                || state.isTestMode();
    }
}
