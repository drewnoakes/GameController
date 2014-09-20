package controller.action.ui;

import common.annotations.NotNull;
import controller.*;
import controller.action.RobotAction;
import controller.action.ui.penalty.*;
import controller.action.ui.penalty.PenaltyAction;
import data.League;
import data.Penalty;
import data.UISide;

/**
 * This action means that a robot button has been pressed.
 *
 * @author Michel Bartsch
 */
public class RobotButton extends Action
{
    private final League league;
    private final UISide side;
    private final int uniformNumber;
    
    /**
     * @param side on which side
     * @param uniformNumber the player's uniform number
     */
    public RobotButton(@NotNull League league, @NotNull UISide side, int uniformNumber)
    {
        if (uniformNumber < 1)
            throw new IllegalArgumentException("Uniform number must be greater than zero.");

        this.league = league;
        this.side = side;
        this.uniformNumber = uniformNumber;
    }

    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        WriteableTeamState team = state.getTeam(side);
        WriteablePlayerState player = team.getPlayer(uniformNumber);

        if (player.getPenalty() == Penalty.Substitute && !isCoach()) {
            TeamState.QueuedPenalty queuedPenalty = team.popQueuedPenalty();
            if (queuedPenalty == null) {
                player.setPenalty(game.league().isHLFamily() ? Penalty.None : Penalty.SplRequestForPickup);
                player.setWhenPenalized(state.getTime());
            } else {
                player.setPenalty(queuedPenalty.getPenalty());
                player.setWhenPenalized(queuedPenalty.getWhenPenalized());
            }
            game.pushState("Entering Player " + team.getTeamColor() + " " + uniformNumber);
        } else if (game.getLastUserAction() instanceof RobotAction) {
            RobotAction robotAction = (RobotAction)game.getLastUserAction();
            robotAction.executeForRobot(game, state, team, player);
        } else if (player.isPenalized()) {
            // Clear the robot's existing penalty
            player.setPenalty(Penalty.None);
            game.pushState("Unpenalised " + team.getTeamColor() + " " + uniformNumber);
        }
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        if (state.isTestMode())
            return true;

        ReadOnlyTeamState team = state.getTeam(side);

        boolean isCoach = uniformNumber == game.rules().getTeamSize() + 1;
        ReadOnlyPlayerState player = isCoach ? team.getCoach() : team.getPlayer(uniformNumber);

        if (player.isEjected())
            return false;

        Action lastUIAction = game.getLastUserAction();
        Penalty penalty = player.getPenalty();

        return (!(lastUIAction instanceof PenaltyAction)
                   && penalty != Penalty.None
                   && (state.getRemainingPenaltyTime(player) == 0 || game.league().isHLFamily())
                   && (penalty != Penalty.Substitute || team.getNumberOfRobotsInPlay() < game.rules().getRobotsPlaying())
                   && !isCoach())
               || (lastUIAction instanceof PickUpHL
                   && penalty != Penalty.Service
                   && penalty != Penalty.Substitute)
               || (lastUIAction instanceof Service
                   && penalty != Penalty.Service
                   && penalty != Penalty.Substitute)
               || (lastUIAction instanceof PickUpSPL
                   && game.league().isSPLFamily()
                   && penalty != Penalty.SplRequestForPickup
                   && penalty != Penalty.Substitute)
               || (lastUIAction instanceof Substitute
                   && penalty != Penalty.Substitute
                   && (!isCoach() && (!game.league().isSPLFamily() || uniformNumber != 0)))
               || (lastUIAction instanceof CoachMotion
                   && isCoach()
                   && team.getCoach().getPenalty() != Penalty.SplCoachMotion)
               || (penalty == Penalty.None
                   && (lastUIAction instanceof PenaltyAction)
                   && !(lastUIAction instanceof CoachMotion)
                   && !(lastUIAction instanceof Substitute)
                   && !isCoach())
               || (lastUIAction instanceof TeammatePushing && penalty == Penalty.None);
    }
    
    public boolean isCoach()
    {
        return league.rules().isCoachAvailable() && uniformNumber == league.rules().getTeamSize();
    }
}