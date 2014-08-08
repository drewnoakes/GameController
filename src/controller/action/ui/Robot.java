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
     * Creates a new Robot action.
     * Look at the ActionBoard before using this.
     * 
     * @param side      On which side (0:left, 1:right)
     * @param number    The players`s number, beginning with 0!
     */
    public Robot(int side, int number)
    {
        super(ActionType.UI);
        this.side = side;
        this.number = number;
    }

    /**
     * Performs this action to manipulate the data (model).
     * 
     * @param data      The current data to work on.
     */
    @Override
    public void perform(GameState data)
    {
        PlayerInfo player = data.team[side].player[number];
        if (player.penalty == Penalty.Substitute && !isCoach()) {
            ArrayList<PenaltyQueueData> playerInfoList = data.penaltyQueueForSubPlayers.get(side);
            if (playerInfoList.isEmpty()) {
                if (Rules.league instanceof HL) {
                    player.penalty = Penalty.None;
                } else {
                    player.penalty = Penalty.SplRequestForPickup;
                }
                data.whenPenalized[side][number] = data.getTime();
            } else {
                PenaltyQueueData playerInfo = playerInfoList.get(0);
                player.penalty = playerInfo.penalty;
                data.whenPenalized[side][number] = playerInfo.whenPenalized;
                playerInfoList.remove(0);
            }
            Log.state(data, "Entering Player " + data.team[side].teamColor + " " + (number+1));
        }
        else if (EventHandler.getInstance().lastUIEvent instanceof PenaltyAction || EventHandler.getInstance().lastUIEvent instanceof TeammatePushing) {
            EventHandler.getInstance().lastUIEvent.performOn(data, player, side, number);
        }
        else if (player.penalty != Penalty.None) {
            Log.state(data, "Unpenalised " + data.team[side].teamColor + " " + (number+1));
            player.penalty = Penalty.None;
        }
    }
    
    /**
     * Checks if this action is legal with the given data (model).
     * Illegal actions are not performed by the EventHandler.
     * 
     * @param data      The current data to check with.
     */
    @Override
    public boolean isLegal(GameState data)
    {
        return !data.ejected[side][number]
                && (!(EventHandler.getInstance().lastUIEvent instanceof PenaltyAction)
                && data.team[side].player[number].penalty != Penalty.None
                && (data.getRemainingPenaltyTime(side, number) == 0 || Rules.league instanceof HL)
                && (data.team[side].player[number].penalty != Penalty.Substitute || data.getNumberOfRobotsInPlay(side) < Rules.league.robotsPlaying)
                && !isCoach()
                || EventHandler.getInstance().lastUIEvent instanceof PickUpHL
                && data.team[side].player[number].penalty != Penalty.HLService
                && data.team[side].player[number].penalty != Penalty.Substitute
                || EventHandler.getInstance().lastUIEvent instanceof ServiceHL
                && data.team[side].player[number].penalty != Penalty.HLService
                && data.team[side].player[number].penalty != Penalty.Substitute
                || (EventHandler.getInstance().lastUIEvent instanceof PickUp && Rules.league instanceof SPL)
                && data.team[side].player[number].penalty != Penalty.SplRequestForPickup
                && data.team[side].player[number].penalty != Penalty.Substitute
                || EventHandler.getInstance().lastUIEvent instanceof Substitute
                && data.team[side].player[number].penalty != Penalty.Substitute
                && (!isCoach() && (!(Rules.league instanceof SPL) || number != 0))
                || (EventHandler.getInstance().lastUIEvent instanceof CoachMotion)
                    && (isCoach() && (data.team[side].coach.penalty != Penalty.SplCoachMotion))
                || data.team[side].player[number].penalty == Penalty.None
                    && (EventHandler.getInstance().lastUIEvent instanceof PenaltyAction)
                    && !(EventHandler.getInstance().lastUIEvent instanceof CoachMotion)
                    && !(EventHandler.getInstance().lastUIEvent instanceof Substitute)
                    && (!isCoach())
                || (data.team[side].player[number].penalty == Penalty.None)
                    && (EventHandler.getInstance().lastUIEvent instanceof TeammatePushing))
                || data.testmode;
    }
    
    public boolean isCoach()
    {
        return Rules.league.isCoachAvailable && number == Rules.league.teamSize;
    }
}