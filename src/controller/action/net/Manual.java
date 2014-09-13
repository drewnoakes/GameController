package controller.action.net;

import common.annotations.NotNull;
import controller.*;
import data.PlayMode;
import data.Penalty;
import data.UISide;

/**
 * This action means that a player has been penalised or unpenalised manually via a {@link data.RobotMessage}.
 *
 * @author Michel Bartsch
 */
public class Manual extends Action
{
    private final UISide side;
    private final int uniformNumber;
    private final boolean isUnpenalisation;
    
    /**
     * @param side on which side of the UI.
     * @param uniformNumber the player's uniform number.
     * @param isUnpenalisation if <code>true</code>, this action means manual unpenalising, otherwise manual penalising.
     */
    public Manual(@NotNull UISide side, int uniformNumber, boolean isUnpenalisation)
    {
        if (uniformNumber < 1)
            throw new IllegalArgumentException("Uniform number must be greater than zero.");

        this.side = side;
        this.uniformNumber = uniformNumber;
        this.isUnpenalisation = isUnpenalisation;
    }

    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        WriteableTeamState team = state.getTeam(side);
        WriteablePlayerState player = team.getPlayer(uniformNumber);

        if (!isUnpenalisation) {
            player.setPenalty(Penalty.Manual);
            player.setWhenPenalized(state.getTime());

            if (!state.is(PlayMode.Initial) && !state.is(PlayMode.Finished)) {
                game.pushState("Manually Penalised " + team.getTeamColor() + " " + uniformNumber);
            }
        } else {
            player.setPenalty(Penalty.None);

            if (!state.is(PlayMode.Initial) && !state.is(PlayMode.Finished)) {
                game.pushState("Manually Unpenalised " + team.getTeamColor() + " " + uniformNumber);
            }
        }
    }
}
