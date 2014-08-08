package controller.action.ui.penalty;

import common.Log;
import data.GameState;
import data.PlayMode;
import data.Penalty;
import data.PlayerInfo;

/**
 * This action means that the inactive player penalty has been selected.
 *
 * @author Michel Bartsch
 */
public class Inactive extends PenaltyAction
{
    @Override
    public void performOn(GameState state, PlayerInfo player, int side, int number)
    {
        player.penalty = Penalty.SplInactivePlayer;
        state.whenPenalized[side][number] = state.getTime();
        Log.state(state, "Inactive Player " + state.team[side].teamColor + " " + (number+1));
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return state.playMode == PlayMode.Ready
            || state.playMode == PlayMode.Playing
            || state.testmode;
    }
}
