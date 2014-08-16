/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.Game;
import controller.GameState;
import data.Penalty;
import data.PlayerState;

/**
 *
 * @author Michel-Zen
 */
public class BallManipulation extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull GameState state, @NotNull PlayerState player, int side, int number)
    {
        player.penalty = Penalty.HLBallManipulation;
        state.whenPenalized[side][number] = state.getTime();
        game.pushState("Ball Manipulation " + state.team[side].teamColor + " " + (number + 1));
    }
}
