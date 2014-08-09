package controller.action.ui.penalty;

import data.GameState;
import data.Penalty;
import data.PlayerInfo;
import rules.Rules;

public class CoachMotion extends PenaltyAction
{
    @Override
    public void performOn(GameState state, PlayerInfo player, int side, int number)
    {
        state.whenPenalized[side][number] = state.getTime();
        state.team[side].coach.penalty = Penalty.SplCoachMotion;
        state.ejected[side][number] = true;
        log(state, null, "Coach Motion " + state.team[side].teamColor);
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return Rules.league.isCoachAvailable;
    }
}
