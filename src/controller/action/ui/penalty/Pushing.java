package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.*;
import data.PlayMode;
import data.Penalty;
import data.UISide;

/**
 * This action means that the player pushing penalty has been selected.
 *
 * @author Michel Bartsch
 */
public class Pushing extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull WriteableGameState state, @NotNull WriteableTeamState team, @NotNull WriteablePlayerState player, @NotNull UISide side)
    {
        player.setPenalty(Penalty.SplPlayerPushing);
        player.setWhenPenalized(state.getTime());

        if (state.getPlayMode() == PlayMode.Playing) {
            team.setPushCount(team.getPushCount() + 1);
            for (int pushes : game.settings().pushesToEjection) {
                if (team.getPushCount() == pushes) {
                    player.setEjected(true);
                }
            }
        }

        game.pushState("Player Pushing " + team.getTeamColor() + " " + player.getUniformNumber());
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return state.getPlayMode() == PlayMode.Ready
            || state.getPlayMode() == PlayMode.Playing
            || state.isTestMode();
    }
}