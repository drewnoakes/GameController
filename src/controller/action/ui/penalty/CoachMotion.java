package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.*;
import data.Penalty;

public class CoachMotion extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull WriteableGameState state, @NotNull WriteableTeamState team,
                                @NotNull WriteablePlayerState player)
    {
        player.setPenalty(Penalty.SplCoachMotion);
        player.setWhenPenalized(state.getTime());
        player.setEjected(true);
        game.pushState("Coach Motion " + team.getTeamColor() + " " + player.getUniformNumber());
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return game.rules().isCoachAvailable();
    }
}
