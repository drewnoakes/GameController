package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.*;
import data.Penalty;
import data.UISide;

/**
 * This action means that the request for pickup penalty has been selected.
 *
 * @author Michel Bartsch
 */
public class PickUpSPL extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull WriteableGameState state, @NotNull WriteableTeamState team,
                                @NotNull WriteablePlayerState player, @NotNull UISide side)
    {
        if (player.getPenalty() == Penalty.None) {
            player.setWhenPenalized(state.getTime());
        }

        player.setPenalty(Penalty.SplRequestForPickup);
        game.pushState("Request for PickUp " + team.getTeamColor() + " " + player.getUniformNumber());
    }
}
