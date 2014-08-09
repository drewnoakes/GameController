package controller.action.ui.penalty;

import data.GameState;
import data.PlayMode;
import data.Penalty;
import data.PlayerInfo;
import rules.Rules;

/**
 * This action means that the player pushing penalty has been selected.
 *
 * @author Michel Bartsch
 */
public class Pushing extends PenaltyAction
{
    @Override
    public void performOn(GameState state, PlayerInfo player, int side, int number)
    {
        player.penalty = Penalty.SplPlayerPushing;
        state.whenPenalized[side][number] = state.getTime();

        if (state.playMode == PlayMode.Playing) {
            state.pushes[side]++;
            for (int pushes : Rules.league.pushesToEjection) {
                if (state.pushes[side] == pushes) {
                    state.ejected[side][number] = true;
                }
            }
        }
        
        log(state, null, "Player Pushing " + state.team[side].teamColor + " " + (number+1));
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return state.playMode == PlayMode.Ready
            || state.playMode == PlayMode.Playing
            || state.testmode;
    }
}