package controller.action.ui.penalty;

import common.Log;

import data.GameState;
import data.Penalty;
import data.PlayerInfo;
import rules.Rules;

public class CoachMotion extends PenaltyAction
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
        state.whenPenalized[side][number] = state.getTime();
        state.team[side].coach.penalty = Penalty.SplCoachMotion;
        state.ejected[side][number] = true;
        Log.state(state, "Coach Motion " + state.team[side].teamColor);
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
        return Rules.league.isCoachAvailable;
    }
}
