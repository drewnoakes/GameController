package controller.action.net;

import controller.action.ActionType;
import controller.action.GCAction;
import controller.net.RobotWatcher;
import data.GameState;
import data.Penalty;
import data.SPLCoachMessage;

public class SPLCoachMessageReceived extends GCAction
{
    private SPLCoachMessage message;
    
    public SPLCoachMessageReceived(SPLCoachMessage message)
    {
        super(ActionType.NET);
        this.message = message;
    }
    
    @Override
    public void perform(GameState data)
    {
        byte team = data.team[0].teamNumber == message.teamNumber ? (byte)0 : (byte)1;
        RobotWatcher.updateCoach(team);
        if ((System.currentTimeMillis() - data.timestampCoachPackage[team]) >= SPLCoachMessage.SPL_COACH_MESSAGE_RECEIVE_INTERVAL
                && data.team[team].coach.penalty != Penalty.SplCoachMotion) {
            data.timestampCoachPackage[team] = System.currentTimeMillis();
            data.splCoachMessageQueue.add(message);
        }
    }

    @Override
    public boolean isLegal(GameState data)
    {
        return true;
    }
}
