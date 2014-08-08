package controller.action.ui;

import java.util.ArrayList;

import common.Log;
import controller.EventHandler;
import controller.action.ActionType;
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
    private int side;
    /** The players`s number, beginning with 0! */
    private int number;
    
    /**
     * @param side on which side (0:left, 1:right)
     * @param number the players`s number, beginning with 0!
     */
    public Robot(int side, int number)
    {
        super(ActionType.UI);
        this.side = side;
        this.number = number;
    }

    @Override
    public void perform(GameState state)
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
            Log.state(state, "Entering Player " + state.team[side].teamColor + " " + (number+1));
        }
        else if (EventHandler.getInstance().lastUIAction instanceof PenaltyAction || EventHandler.getInstance().lastUIAction instanceof TeammatePushing) {
            EventHandler.getInstance().lastUIAction.performOn(state, player, side, number);
        }
        else if (player.penalty != Penalty.None) {
            Log.state(state, "Unpenalised " + state.team[side].teamColor + " " + (number+1));
            player.penalty = Penalty.None;
        }
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return !state.ejected[side][number]
                && (!(EventHandler.getInstance().lastUIAction instanceof PenaltyAction)
                && state.team[side].player[number].penalty != Penalty.None
                && (state.getRemainingPenaltyTime(side, number) == 0 || Rules.league instanceof HL)
                && (state.team[side].player[number].penalty != Penalty.Substitute || state.getNumberOfRobotsInPlay(side) < Rules.league.robotsPlaying)
                && !isCoach()
                || EventHandler.getInstance().lastUIAction instanceof PickUpHL
                && state.team[side].player[number].penalty != Penalty.HLService
                && state.team[side].player[number].penalty != Penalty.Substitute
                || EventHandler.getInstance().lastUIAction instanceof ServiceHL
                && state.team[side].player[number].penalty != Penalty.HLService
                && state.team[side].player[number].penalty != Penalty.Substitute
                || (EventHandler.getInstance().lastUIAction instanceof PickUp && Rules.league instanceof SPL)
                && state.team[side].player[number].penalty != Penalty.SplRequestForPickup
                && state.team[side].player[number].penalty != Penalty.Substitute
                || EventHandler.getInstance().lastUIAction instanceof Substitute
                && state.team[side].player[number].penalty != Penalty.Substitute
                && (!isCoach() && (!(Rules.league instanceof SPL) || number != 0))
                || (EventHandler.getInstance().lastUIAction instanceof CoachMotion)
                    && (isCoach() && (state.team[side].coach.penalty != Penalty.SplCoachMotion))
                || state.team[side].player[number].penalty == Penalty.None
                    && (EventHandler.getInstance().lastUIAction instanceof PenaltyAction)
                    && !(EventHandler.getInstance().lastUIAction instanceof CoachMotion)
                    && !(EventHandler.getInstance().lastUIAction instanceof Substitute)
                    && (!isCoach())
                || (state.team[side].player[number].penalty == Penalty.None)
                    && (EventHandler.getInstance().lastUIAction instanceof TeammatePushing))
                || state.testmode;
    }
    
    public boolean isCoach()
    {
        return Rules.league.isCoachAvailable && number == Rules.league.teamSize;
    }
}