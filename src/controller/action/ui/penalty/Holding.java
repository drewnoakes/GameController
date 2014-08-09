package controller.action.ui.penalty;

import data.GameState;
import data.PlayMode;
import data.Penalty;
import data.PlayerInfo;

/**
 * This action means that the ball holding penalty has been selected.
 *
 * @author Michel Bartsch
 */
public class Holding extends PenaltyAction
{
    @Override
    public void performOn(GameState state, PlayerInfo player, int side, int number)
    {
        player.penalty = Penalty.SplBallHolding;
        state.whenPenalized[side][number] = state.getTime();
        log(state, null, "Ball Holding " + state.team[side].teamColor + " " + (number+1));
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return state.playMode == PlayMode.Playing || state.testmode;
    }
}
