package controller.action.ui;

import java.util.ArrayList;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.action.RobotAction;
import controller.action.ui.penalty.*;
import controller.action.ui.penalty.PenaltyAction;
import controller.GameState;
import controller.GameState.PenaltyQueueData;
import data.League;
import data.Penalty;
import data.PlayerState;

/**
 * This action means that a robot button has been pressed.
 *
 * @author Michel Bartsch
 */
public class RobotButton extends Action
{
    private final League league;
    /** On which side (0:left, 1:right) */
    private final int side;
    /** The players`s number, beginning with 0! */
    private final int number;
    
    /**
     * @param side on which side (0:left, 1:right)
     * @param number the players`s number, beginning with 0!
     */
    public RobotButton(League league, int side, int number)
    {
        this.league = league;
        this.side = side;
        this.number = number;
    }

    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        PlayerState player = state.teams[side].player[number];
        if (player.penalty == Penalty.Substitute && !isCoach()) {
            ArrayList<PenaltyQueueData> playerInfoList = state.penaltyQueueForSubPlayers.get(side);
            if (playerInfoList.isEmpty()) {
                if (game.league().isHLFamily()) {
                    player.penalty = Penalty.None;
                } else {
                    player.penalty = Penalty.SplRequestForPickup;
                }
                state.whenPenalized[side][number] = state.getTime();
            } else {
                PenaltyQueueData playerInfo = playerInfoList.get(0);
                player.penalty = playerInfo.penalty;
                state.whenPenalized[side][number] = playerInfo.whenPenalized;
                playerInfoList.remove(0);
            }
            game.pushState("Entering Player " + state.teams[side].teamColor + " " + (number + 1));
        } else if (game.getLastUserAction() instanceof RobotAction) {
            RobotAction robotAction = (RobotAction)game.getLastUserAction();
            robotAction.executeForRobot(game, state, player, side, number);
        } else if (player.penalty != Penalty.None) {
            // Clear the robot's existing penalty
            player.penalty = Penalty.None;
            game.pushState("Unpenalised " + state.teams[side].teamColor + " " + (number + 1));
        }
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        if (state.testmode)
            return true;

        if (state.ejected[side][number])
            return false;

        Action lastUIAction = game.getLastUserAction();
        Penalty penalty = state.teams[side].player[number].penalty;

        return (!(lastUIAction instanceof PenaltyAction)
                   && penalty != Penalty.None
                   && (state.getRemainingPenaltyTime(side, number) == 0 || game.league().isHLFamily())
                   && (penalty != Penalty.Substitute || state.getNumberOfRobotsInPlay(side) < game.settings().robotsPlaying)
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
                   && (!isCoach() && (!game.league().isSPLFamily() || number != 0)))
               || (lastUIAction instanceof CoachMotion
                   && isCoach()
                   && state.teams[side].coach.penalty != Penalty.SplCoachMotion)
               || (penalty == Penalty.None
                   && (lastUIAction instanceof PenaltyAction)
                   && !(lastUIAction instanceof CoachMotion)
                   && !(lastUIAction instanceof Substitute)
                   && !isCoach())
               || (lastUIAction instanceof TeammatePushing && penalty == Penalty.None);
    }
    
    public boolean isCoach()
    {
        return league.settings().isCoachAvailable && number == league.settings().teamSize;
    }
}