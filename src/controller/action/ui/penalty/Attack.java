package controller.action.ui.penalty;

import data.GameState;
import data.PlayMode;
import data.Penalty;
import data.PlayerInfo;

/**
 *
 * @author Michel-Zen
 */
public class Attack extends PenaltyAction
{
    @Override
    public void performOn(GameState state, PlayerInfo player, int side, int number)
    {
        player.penalty = Penalty.HLIllegalAttack;
        state.whenPenalized[side][number] = state.getTime();
        log(state, null, "Illegal Attack " + state.team[side].teamColor + " " + (number+1));
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return state.playMode == PlayMode.Playing || state.testmode;
    }
}
