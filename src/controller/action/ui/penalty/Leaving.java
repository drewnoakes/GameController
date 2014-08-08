package controller.action.ui.penalty;

import common.Log;
import data.GameState;
import data.PlayMode;
import data.Penalty;
import data.PlayerInfo;

/**
 * This action means that the leaving the field penalty has been selected.
 *
 * @author Michel Bartsch
 */
public class Leaving extends PenaltyAction
{
    /**
     * Performs this action`s penalty on a selected player.
     *  @param state      The current data to work on.
     * @param player    The player to penalise.
     * @param side      The side the player is playing on (0:left, 1:right).
     * @param number    The player`s number, beginning with 0!
     */
    @Override
    public void performOn(GameState state, PlayerInfo player, int side, int number)
    {
        player.penalty = Penalty.SplLeavingTheField;
        state.whenPenalized[side][number] = state.getTime();
        Log.state(state, "Leaving the Field " + state.team[side].teamColor + " " + (number+1));
    }
    
    /**
     * Checks if this action is legal with the given data (model).
     * Illegal actions are not performed by the EventHandler.
     *
     * @param state      The current data to check with.
     */
    @Override
    public boolean isLegal(GameState state)
    {
        return state.playMode == PlayMode.Ready
            || state.playMode == PlayMode.Playing
            || state.testmode;
    }
}
