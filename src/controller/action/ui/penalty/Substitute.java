package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.*;
import data.Penalty;

/**
 * This action means that the substitution player penalty has been selected.
 *
 * @author Michel Bartsch
 */
public class Substitute extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull WriteableGameState state, @NotNull WriteableTeamState team,
                                @NotNull WriteablePlayerState player)
    {
        // Queue any penalty the leaving player has to be picked up by the entering player
        if (player.isPenalized()) {
            team.enqueuePenalty(player.getWhenPenalized(), player.getPenalty());
        }

        player.setPenalty(Penalty.Substitute);
        player.setWhenPenalized(state.getTime());
        game.pushState("Leaving Player " + team.getTeamColor() + " " + player.getUniformNumber());
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return game.settings().teamSize > game.settings().robotsPlaying;
    }
}