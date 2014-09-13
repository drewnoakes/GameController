package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.*;
import data.PlayMode;
import data.Penalty;

/**
 * Applies the SPL leaving the field penalty to a robot.
 *
 * @author Michel Bartsch
 */
public class Leaving extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull WriteableGameState state, @NotNull WriteableTeamState team,
                                @NotNull WriteablePlayerState player)
    {
        player.setPenalty(Penalty.SplLeavingTheField);
        player.setWhenPenalized(state.getTime());
        game.pushState("Leaving the Field " + team.getTeamColor() + " " + player.getUniformNumber());
    }

    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return state.is(PlayMode.Ready)
            || state.is(PlayMode.Playing)
            || state.isTestMode();
    }
}
