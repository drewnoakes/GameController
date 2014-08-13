package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.Game;
import data.GameState;
import data.Penalty;
import data.PlayerInfo;

/**
 * This action means that the substitution player penalty has been selected.
 *
 * @author Michel Bartsch
 */
public class Substitute extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull GameState state, @NotNull PlayerInfo player, int side, int number)
    {
        if (player.penalty != Penalty.None) {
            state.addToPenaltyQueue(side, state.whenPenalized[side][number], player.penalty);
        }

        player.penalty = Penalty.Substitute;
        state.whenPenalized[side][number] = state.getTime();
        game.pushState("Leaving Player " + state.team[side].teamColor + " " + (number + 1));
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return Game.settings.teamSize > Game.settings.robotsPlaying;
    }
}