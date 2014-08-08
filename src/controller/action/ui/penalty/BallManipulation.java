/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.action.ui.penalty;

import common.Log;
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
    public void performOn(GameState state, PlayerInfo player, int side, int number)
    {
        player.penalty = Penalty.HLBallManipulation;
        state.whenPenalized[side][number] = state.getTime();
        Log.state(state, "Ball Manipulation " + state.team[side].teamColor + " " + (number+1));
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return state.playMode == PlayMode.Playing || state.testmode;
    }
}
