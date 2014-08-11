package controller.action.net;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import data.GameState;
import data.Penalty;
import data.SPLCoachMessage;

public class SPLCoachMessageReceived extends Action
{
    @NotNull private final SPLCoachMessage message;

    public SPLCoachMessageReceived(@NotNull SPLCoachMessage message)
    {
        this.message = message;
    }
    
    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        int team = state.getTeamIndex(this.message.teamNumber);
        assert(team != -1);
        if ((System.currentTimeMillis() - state.timestampCoachMessage[team]) >= SPLCoachMessage.SPL_COACH_MESSAGE_RECEIVE_INTERVAL
                && state.team[team].coach.penalty != Penalty.SplCoachMotion) {
            state.timestampCoachMessage[team] = System.currentTimeMillis();
            state.splCoachMessageQueue.add(this.message);
        }
    }
}
