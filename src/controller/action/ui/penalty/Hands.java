package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.*;
import data.Penalty;

/**
 * Applies the playing with hands penalty to a robot.
 *
 * @author Michel Bartsch
 */
public class Hands extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull WriteableGameState state, @NotNull WriteableTeamState team,
                                @NotNull WriteablePlayerState player)
    {
        player.setPenalty(Penalty.SplPlayingWithHands);
        player.setWhenPenalized(state.getTime());
        game.pushState("Playing with Hands " + team.getTeamColor() + " " + player.getUniformNumber());
    }
}
