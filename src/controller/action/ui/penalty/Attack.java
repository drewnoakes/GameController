package controller.action.ui.penalty;

import common.annotations.NotNull;
import controller.*;
import data.Penalty;

/**
 *
 * @author Michel-Zen
 */
public class Attack extends PenaltyAction
{
    @Override
    public void executeForRobot(@NotNull Game game, @NotNull WriteableGameState state, @NotNull WriteableTeamState team,
                                @NotNull WriteablePlayerState player)
    {
        player.setPenalty(Penalty.HLIllegalAttack);
        player.setWhenPenalized(state.getTime());
        game.pushState("Illegal Attack " + team.getTeamColor() + " " + player.getUniformNumber());
    }
}
