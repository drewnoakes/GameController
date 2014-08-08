package controller.action.net;

import controller.action.ActionType;
import controller.action.GCAction;
import controller.net.RobotWatcher;
import data.GameState;
import data.Penalty;
import data.SPLCoachMessage;

public class SPLCoachMessageReceived extends GCAction
{
    private final SPLCoachMessage message;
    
    public SPLCoachMessageReceived(SPLCoachMessage message)
    {
        super(ActionType.NET);
        this.message = message;
    }
    
    @Override
    public void perform(GameState state)
    {
        byte team = state.team[0].teamNumber == message.teamNumber ? (byte)0 : (byte)1;
        RobotWatcher.updateCoach(team);
        if ((System.currentTimeMillis() - state.timestampCoachPackage[team]) >= SPLCoachMessage.SPL_COACH_MESSAGE_RECEIVE_INTERVAL
                && state.team[team].coach.penalty != Penalty.SplCoachMotion) {
            state.timestampCoachPackage[team] = System.currentTimeMillis();
            state.splCoachMessageQueue.add(message);
        }
    }
}
