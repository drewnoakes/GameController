package controller.action.net;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.action.ActionTrigger;
import controller.action.GCAction;
import data.GameState;
import data.Penalty;
import data.SPLCoachMessage;

public class SPLCoachMessageReceived extends GCAction
{
    @NotNull private final SPLCoachMessage message;

    public SPLCoachMessageReceived(@NotNull SPLCoachMessage message)
    {
        super(ActionTrigger.Network);

        this.message = message;
    }
    
    @Override
    public void perform(@NotNull GameState state, @Nullable String message)
    {
        byte team = state.team[0].teamNumber == this.message.teamNumber ? (byte)0 : (byte)1;
        if ((System.currentTimeMillis() - state.timestampCoachMessage[team]) >= SPLCoachMessage.SPL_COACH_MESSAGE_RECEIVE_INTERVAL
                && state.team[team].coach.penalty != Penalty.SplCoachMotion) {
            state.timestampCoachMessage[team] = System.currentTimeMillis();
            state.splCoachMessageQueue.add(this.message);
        }
    }
}
