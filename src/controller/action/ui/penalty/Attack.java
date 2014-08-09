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
public class Attack extends PenaltyAction
{
    @Override
    public void performOn(@NotNull GameState state, @NotNull PlayerInfo player, int side, int number)
    {
        player.penalty = Penalty.HLIllegalAttack;
        state.whenPenalized[side][number] = state.getTime();
        log(state, null, "Illegal Attack " + state.team[side].teamColor + " " + (number+1));
    }
    
    @Override
    public boolean isLegal(@NotNull GameState state)
    {
        return state.playMode == PlayMode.Playing || state.testmode;
    }
}
