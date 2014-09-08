package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.*;
import data.Penalty;
import data.UISide;

/**
 * Applies the ball holding penalty to a robot.
 *
 * @author Michel Bartsch
 */
public class Holding extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull WriteableGameState state, @NotNull WriteableTeamState team,
                                @NotNull WriteablePlayerState player, @NotNull UISide side)
    {
        player.setPenalty(Penalty.SplBallHolding);
        player.setWhenPenalized(state.getTime());
        game.pushState("Ball Holding " + team.getTeamColor() + " " + player.getUniformNumber());
    }
}
