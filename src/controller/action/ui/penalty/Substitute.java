package controller.action.ui.penalty;

import common.Log;
import data.AdvancedData;
import data.Penalty;
import data.PlayerInfo;
import rules.Rules;

/**
 * This action means that the substitution player penalty has been selected.
 *
 * @author Michel Bartsch
 */
public class Substitute extends PenaltyAction
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
        if (player.penalty != Penalty.None) {
            data.addToPenaltyQueue(side, data.whenPenalized[side][number], player.penalty);
        }

        player.penalty = Penalty.Substitute;
        data.whenPenalized[side][number] = data.getTime();
        Log.state(data, "Leaving Player " + data.team[side].teamColor + " " + (number+1));
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
        return Rules.league.teamSize > Rules.league.robotsPlaying;
    }
}