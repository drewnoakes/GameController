package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.*;
import data.Penalty;

/**
 * This action means that the illegal defender penalty has been selected.
 *
 * @author Michel Bartsch
 */
public class Defender extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull WriteableGameState state, @NotNull WriteableTeamState team,
                                @NotNull WriteablePlayerState player)
    {
        player.setPenalty(Penalty.SplIllegalDefender);
        player.setWhenPenalized(state.getTime());
        game.pushState("Illegal Defender " + team.getTeamColor() + " " + player.getUniformNumber());
    }
}
