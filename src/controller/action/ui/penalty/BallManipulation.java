/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.action.ui.penalty;

import common.annotations.NotNull;
import data.GameState;
import data.PlayMode;
import data.Penalty;
import data.PlayerInfo;

/**
 *
 * @author Michel-Zen
 */
public class BallManipulation extends PenaltyAction
{
    @Override
    public void performOn(@NotNull GameState state, @NotNull PlayerInfo player, int side, int number)
    {
        player.penalty = Penalty.HLBallManipulation;
        state.whenPenalized[side][number] = state.getTime();
        log(state, null, "Ball Manipulation " + state.team[side].teamColor + " " + (number+1));
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return state.playMode == PlayMode.Playing || state.testmode;
    }
}
