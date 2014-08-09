package controller.action.net;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.action.ActionType;
import controller.action.GCAction;
import controller.net.RobotWatcher;
import data.GameState;
import data.Penalty;
import data.SPLCoachMessage;

public class SPLCoachMessageReceived extends GCAction
{
    @NotNull
    private final SPLCoachMessage message;
    
    public SPLCoachMessageReceived(SPLCoachMessage message)
    {
        super(ActionType.NET);
        this.message = message;
    }
    
    @Override
    public void perform(@NotNull GameState state, @Nullable String message)
    {
        byte team = state.team[0].teamNumber == this.message.teamNumber ? (byte)0 : (byte)1;
        RobotWatcher.updateCoach(team);
        if ((System.currentTimeMillis() - state.timestampCoachMessage[team]) >= SPLCoachMessage.SPL_COACH_MESSAGE_RECEIVE_INTERVAL
                && state.team[team].coach.penalty != Penalty.SplCoachMotion) {
            state.timestampCoachMessage[team] = System.currentTimeMillis();
            state.splCoachMessageQueue.add(this.message);
        }
    }
}
