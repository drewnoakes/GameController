package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.*;
import data.PlayMode;
import data.Penalty;

/**
 * Applies the SPL inactive player penalty to a robot.
 *
 * @author Michel Bartsch
 */
public class Inactive extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull WriteableGameState state, @NotNull WriteableTeamState team,
                                @NotNull WriteablePlayerState player)
    {
        player.setPenalty(Penalty.SplInactivePlayer);
        player.setWhenPenalized(state.getTime());
        game.pushState("Inactive Player " + team.getTeamColor() + " " + player.getUniformNumber());
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return state.is(PlayMode.Ready, PlayMode.Playing)
            || state.isTestMode();
    }
}
