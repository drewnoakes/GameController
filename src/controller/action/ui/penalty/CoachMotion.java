package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.Game;
import controller.GameState;
import data.Penalty;
import data.PlayerInfo;

public class CoachMotion extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull GameState state, @NotNull PlayerInfo player, int side, int number)
    {
        state.whenPenalized[side][number] = state.getTime();
        state.team[side].coach.penalty = Penalty.SplCoachMotion;
        state.ejected[side][number] = true;
        game.pushState("Coach Motion " + state.team[side].teamColor + " " + (number + 1));
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return game.settings().isCoachAvailable;
    }
}
