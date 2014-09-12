package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.*;
import data.PlayMode;
import data.Penalty;

/**
 * Applies the SPL obstruction (fallen robot) penalty to a robot.
 *
 * @author Michel Bartsch
 */
public class Fallen extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull WriteableGameState state, @NotNull WriteableTeamState team,
                                @NotNull WriteablePlayerState player)
    {
        player.setPenalty(Penalty.SplObstruction);
        player.setWhenPenalized(state.getTime());
        game.pushState("Fallen Robot " + team.getTeamColor() + " " + player.getUniformNumber());
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return state.getPlayMode() == PlayMode.Ready
            || state.getPlayMode() == PlayMode.Playing
            || state.getPlayMode() == PlayMode.Set
            || state.isTestMode();
    }
}
