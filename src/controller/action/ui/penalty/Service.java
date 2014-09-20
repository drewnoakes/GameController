package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.*;
import data.Penalty;

/**
 *
 * @author Daniel Seifert
 */
public class Service extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull WriteableGameState state, @NotNull WriteableTeamState team,
                                @NotNull WriteablePlayerState player)
    {
        if (!player.isPenalized()) {
            player.setWhenPenalized(state.getTime());
            player.setPenalty(Penalty.Service);
            game.pushState("Request for Service " + team.getTeamColor() + " " + player.getUniformNumber());
        } else {
            player.setPenalty(Penalty.Service);
            game.pushState("Additional Request for Service " + team.getTeamColor() + " " + player.getUniformNumber());
        }
    }
}
