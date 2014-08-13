package controller.action.ui;

import java.util.ArrayList;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.action.RobotAction;
import controller.action.ui.penalty.*;
import controller.action.ui.penalty.PenaltyAction;
import data.GameState;
import data.GameState.PenaltyQueueData;
import data.Penalty;
import data.PlayerInfo;
import leagues.HL;
import leagues.SPL;

/**
 * This action means that a robot button has been pressed.
 *
 * @author Michel Bartsch
 */
public class RobotButton extends Action
{
    /** On which side (0:left, 1:right) */
    private final int side;
    /** The players`s number, beginning with 0! */
    private final int number;
    
    /**
     * @param side on which side (0:left, 1:right)
     * @param number the players`s number, beginning with 0!
     */
    public RobotButton(int side, int number)
    {
        this.side = side;
        this.number = number;
    }

    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        PlayerInfo player = state.team[side].player[number];
        if (player.penalty == Penalty.Substitute && !isCoach()) {
            ArrayList<PenaltyQueueData> playerInfoList = state.penaltyQueueForSubPlayers.get(side);
            if (playerInfoList.isEmpty()) {
                if (Game.settings instanceof HL) {
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
            game.pushState("Entering Player " + state.team[side].teamColor + " " + (number + 1));
        } else if (game.getLastUserAction() instanceof RobotAction) {
            RobotAction robotAction = (RobotAction)game.getLastUserAction();
            robotAction.executeForRobot(game, state, player, side, number);
        } else if (player.penalty != Penalty.None) {
            // Clear the robot's existing penalty
            player.penalty = Penalty.None;
            game.pushState("Unpenalised " + state.team[side].teamColor + " " + (number + 1));
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
        Penalty penalty = state.team[side].player[number].penalty;

        return (!(lastUIAction instanceof PenaltyAction)
                   && penalty != Penalty.None
                   && (state.getRemainingPenaltyTime(side, number) == 0 || Game.settings instanceof HL)
                   && (penalty != Penalty.Substitute || state.getNumberOfRobotsInPlay(side) < Game.settings.robotsPlaying)
                   && !isCoach())
               || (lastUIAction instanceof PickUpHL
                   && penalty != Penalty.Service
                   && penalty != Penalty.Substitute)
               || (lastUIAction instanceof Service
                   && penalty != Penalty.Service
                   && penalty != Penalty.Substitute)
               || (lastUIAction instanceof PickUpSPL
                   && Game.settings instanceof SPL
                   && penalty != Penalty.SplRequestForPickup
                   && penalty != Penalty.Substitute)
               || (lastUIAction instanceof Substitute
                   && penalty != Penalty.Substitute
                   && (!isCoach() && (!(Game.settings instanceof SPL) || number != 0)))
               || (lastUIAction instanceof CoachMotion
                   && isCoach()
                   && state.team[side].coach.penalty != Penalty.SplCoachMotion)
               || (penalty == Penalty.None
                   && (lastUIAction instanceof PenaltyAction)
                   && !(lastUIAction instanceof CoachMotion)
                   && !(lastUIAction instanceof Substitute)
                   && !isCoach())
               || (lastUIAction instanceof TeammatePushing && penalty == Penalty.None);
    }
    
    public boolean isCoach()
    {
        return Game.settings.isCoachAvailable && number == Game.settings.teamSize;
    }
}