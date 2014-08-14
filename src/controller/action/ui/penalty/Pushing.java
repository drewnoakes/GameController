package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.Game;
import controller.GameState;
import data.PlayMode;
import data.Penalty;
import data.PlayerInfo;

/**
 * This action means that the player pushing penalty has been selected.
 *
 * @author Michel Bartsch
 */
public class Pushing extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull GameState state, @NotNull PlayerInfo player, int side, int number)
    {
        player.penalty = Penalty.SplPlayerPushing;
        state.whenPenalized[side][number] = state.getTime();

        if (state.playMode == PlayMode.Playing) {
            state.pushes[side]++;
            for (int pushes : game.settings().pushesToEjection) {
                if (state.pushes[side] == pushes) {
                    state.ejected[side][number] = true;
                }
            }
        }

        game.pushState("Player Pushing " + state.team[side].teamColor + " " + (number + 1));
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return state.playMode == PlayMode.Ready
            || state.playMode == PlayMode.Playing
            || state.testmode;
    }
}