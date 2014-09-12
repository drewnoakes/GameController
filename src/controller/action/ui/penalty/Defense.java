package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.*;
import data.Penalty;

/**
 *
 * @author Michel-Zen
 */
public class Defense extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull WriteableGameState state, @NotNull WriteableTeamState team,
                                @NotNull WriteablePlayerState player)
    {
        player.setPenalty(Penalty.HLIllegalDefense);
        player.setWhenPenalized(state.getTime());
        game.pushState("Illegal Defense " + team.getTeamColor() + " " + player.getUniformNumber());
    }
}
