/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.*;
import data.Penalty;

/**
 *
 * @author Michel-Zen
 */
public class BallManipulation extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull WriteableGameState state, @NotNull WriteableTeamState team,
                                @NotNull WriteablePlayerState player)
    {
        player.setPenalty(Penalty.HLBallManipulation);
        player.setWhenPenalized(state.getTime());
        game.pushState("Ball Manipulation " + team.getTeamColor() + " " + player.getUniformNumber());
    }
}
