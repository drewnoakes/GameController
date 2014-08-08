package controller.action.ui.penalty;

import common.Log;
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
    /**
     * Performs this action`s penalty on a selected player.
     * 
     * @param data      The current data to work on.
     * @param player    The player to penalise.
     * @param side      The side the player is playing on (0:left, 1:right).
     * @param number    The player`s number, beginning with 0!
     */
    @Override
    public void performOn(GameState data, PlayerInfo player, int side, int number)
    {
        player.penalty = Penalty.SplPlayerPushing;
        data.whenPenalized[side][number] = data.getTime();

        if (data.playMode == PlayMode.Playing) {
            data.pushes[side]++;
            for (int pushes : Rules.league.pushesToEjection) {
                if (data.pushes[side] == pushes) {
                    data.ejected[side][number] = true;
                }
            }
        }
        
        Log.state(data, "Player Pushing " + data.team[side].teamColor + " " + (number+1));
    }
    
    /**
     * Checks if this action is legal with the given data (model).
     * Illegal actions are not performed by the EventHandler.
     * 
     * @param data      The current data to check with.
     */
    @Override
    public boolean isLegal(GameState data)
    {
        return data.playMode == PlayMode.Ready
            || data.playMode == PlayMode.Playing
            || data.testmode;
    }
}