package controller.action.ui.penalty;

import common.Log;
import data.AdvancedData;
import data.PlayMode;
import data.Penalty;
import data.PlayerInfo;

/**
 * @author Michel Bartsch
 * 
 * This action means that the playing with hands penalty has been selected.
 */
public class Hands extends PenaltyAction
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
    public void performOn(AdvancedData data, PlayerInfo player, int side, int number)
    {
        player.penalty = Penalty.SplPlayingWithHands;
        data.whenPenalized[side][number] = data.getTime();
        Log.state(data, "Playing with Hands " + data.team[side].teamColor + " " + (number+1));
    }
    
    /**
     * Checks if this action is legal with the given data (model).
     * Illegal actions are not performed by the EventHandler.
     * 
     * @param data      The current data to check with.
     */
    @Override
    public boolean isLegal(AdvancedData data)
    {
        return data.playMode == PlayMode.Playing || data.testmode;
    }
}
