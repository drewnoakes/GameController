package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.*;
import data.Penalty;

/**
 *
 * @author Michel-Zen
 */
public class PickUpHL extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull WriteableGameState state, @NotNull WriteableTeamState team,
                                @NotNull WriteablePlayerState player)
    {
        if (!player.isPenalized()) {
            player.setWhenPenalized(state.getTime());
        }

        player.setPenalty(Penalty.HLPickupOrIncapable);
        game.pushState("Request for PickUp / Incapable Player " + team.getTeamColor() + " " + player.getUniformNumber());
    }

    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        // Pick up is always available
        return true;
    }
}
