package controller.action.ui.penalty;

import common.Log;
import data.GameState;
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
    @Override
    public void performOn(GameState state, PlayerInfo player, int side, int number)
    {
        if (player.penalty != Penalty.None) {
            state.addToPenaltyQueue(side, state.whenPenalized[side][number], player.penalty);
        }

        player.penalty = Penalty.Substitute;
        state.whenPenalized[side][number] = state.getTime();
        Log.state(state, "Leaving Player " + state.team[side].teamColor + " " + (number+1));
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return Rules.league.teamSize > Rules.league.robotsPlaying;
    }
}