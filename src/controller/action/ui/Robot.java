package controller.action.ui;

import java.util.ArrayList;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.ActionHandler;
import controller.action.ActionTrigger;
import controller.action.GCAction;
import controller.action.ui.penalty.*;
import controller.action.ui.penalty.PenaltyAction;
import data.GameState;
import data.GameState.PenaltyQueueData;
import data.Penalty;
import data.PlayerInfo;
import rules.HL;
import rules.Rules;
import rules.SPL;

/**
 * This action means that a player has been selected.
 *
 * @author Michel Bartsch
 */
public class Robot extends GCAction
{
    /** On which side (0:left, 1:right) */
    private final int side;
    /** The players`s number, beginning with 0! */
    private final int number;
    
    /**
     * @param side on which side (0:left, 1:right)
     * @param number the players`s number, beginning with 0!
     */
    public Robot(int side, int number)
    {
        super(ActionTrigger.User);
        this.side = side;
        this.number = number;
    }

    @Override
    public void perform(@NotNull GameState state, @Nullable String message)
    {
        PlayerInfo player = state.team[side].player[number];
        if (player.penalty == Penalty.Substitute && !isCoach()) {
            ArrayList<PenaltyQueueData> playerInfoList = state.penaltyQueueForSubPlayers.get(side);
            if (playerInfoList.isEmpty()) {
                if (Rules.league instanceof HL) {
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
            log(state, message, "Entering Player " + state.team[side].teamColor + " " + (number + 1));
        }
        else if (ActionHandler.getInstance().lastUserAction instanceof PenaltyAction || ActionHandler.getInstance().lastUserAction instanceof TeammatePushing) {
            ActionHandler.getInstance().lastUserAction.performOn(state, player, side, number);
        }
        else if (player.penalty != Penalty.None) {
            log(state, message, "Unpenalised " + state.team[side].teamColor + " " + (number + 1));
            player.penalty = Penalty.None;
        }
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        GCAction lastUIAction = ActionHandler.getInstance().lastUserAction;
        Penalty penalty = state.team[side].player[number].penalty;

        return !state.ejected[side][number]
                && (!(lastUIAction instanceof PenaltyAction)
                && penalty != Penalty.None
                && (state.getRemainingPenaltyTime(side, number) == 0 || Rules.league instanceof HL)
                && (penalty != Penalty.Substitute || state.getNumberOfRobotsInPlay(side) < Rules.league.robotsPlaying)
                && !isCoach()
                || lastUIAction instanceof PickUpHL
                && penalty != Penalty.HLService
                && penalty != Penalty.Substitute
                || lastUIAction instanceof ServiceHL
                && penalty != Penalty.HLService
                && penalty != Penalty.Substitute
                || (lastUIAction instanceof PickUp && Rules.league instanceof SPL)
                && penalty != Penalty.SplRequestForPickup
                && penalty != Penalty.Substitute
                || lastUIAction instanceof Substitute
                && penalty != Penalty.Substitute
                && (!isCoach() && (!(Rules.league instanceof SPL) || number != 0))
                || (lastUIAction instanceof CoachMotion)
                    && (isCoach() && (state.team[side].coach.penalty != Penalty.SplCoachMotion))
                || penalty == Penalty.None
                    && (lastUIAction instanceof PenaltyAction)
                    && !(lastUIAction instanceof CoachMotion)
                    && !(lastUIAction instanceof Substitute)
                    && (!isCoach())
                || (penalty == Penalty.None)
                    && (lastUIAction instanceof TeammatePushing))
                || state.testmode;
    }
    
    public boolean isCoach()
    {
        return Rules.league.isCoachAvailable && number == Rules.league.teamSize;
    }
}