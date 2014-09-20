package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.*;
import data.PlayMode;
import data.Penalty;

/**
 * This action means that the player pushing penalty has been selected.
 *
 * @author Michel Bartsch
 */
public class Pushing extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull WriteableGameState state, @NotNull WriteableTeamState team, @NotNull WriteablePlayerState player)
    {
        player.setPenalty(Penalty.SplPlayerPushing);
        player.setWhenPenalized(state.getTime());

        if (state.is(PlayMode.Playing)) {
            team.setPushCount(team.getPushCount() + 1);
            for (int pushes : game.rules().getPushesToEjection()) {
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
        return state.is(PlayMode.Ready, PlayMode.Playing)
            || state.isTestMode();
    }
}