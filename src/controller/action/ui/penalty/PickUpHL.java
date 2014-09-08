package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.*;
import data.Penalty;
import data.UISide;

/**
 *
 * @author Michel-Zen
 */
public class PickUpHL extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull WriteableGameState state, @NotNull WriteableTeamState team,
                                @NotNull WriteablePlayerState player, @NotNull UISide side)
    {
        if (player.getPenalty() == Penalty.None) {
            player.setWhenPenalized(state.getTime());
        }

        player.setPenalty(Penalty.HLPickupOrIncapable);
        game.pushState("Request for PickUp / Incapable Player " + team.getTeamColor() + " " + player.getUniformNumber());
    }
}
