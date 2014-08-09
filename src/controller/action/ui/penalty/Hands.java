package controller.action.ui.penalty;

import data.GameState;
import data.PlayMode;
import data.Penalty;
import data.PlayerInfo;

/**
 * This action means that the playing with hands penalty has been selected.
 *
 * @author Michel Bartsch
 */
public class Hands extends PenaltyAction
{
    @Override
    public void performOn(GameState state, PlayerInfo player, int side, int number)
    {
        player.penalty = Penalty.SplPlayingWithHands;
        state.whenPenalized[side][number] = state.getTime();
        log(state, null, "Playing with Hands " + state.team[side].teamColor + " " + (number+1));
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return state.playMode == PlayMode.Playing || state.testmode;
    }
}
