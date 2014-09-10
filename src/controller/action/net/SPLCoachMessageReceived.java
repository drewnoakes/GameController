package controller.action.net;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.WriteableGameState;
import controller.WriteableTeamState;
import data.Penalty;
import data.SPLCoachMessage;

/**
 * Executed when a new SPL coach message is received.
 *
 * @author Sebastian Koralewski
 * @author Drew Noakes https://drewnoakes.com
 */
public class SPLCoachMessageReceived extends Action
{
    @NotNull private final SPLCoachMessage message;

    public SPLCoachMessageReceived(@NotNull SPLCoachMessage message)
    {
        this.message = message;
    }
    
    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        WriteableTeamState team = state.getTeam(this.message.teamNumber);

        if (team == null) {
            // The validity of the team number should have been checked earlier
            // but it's good to check again here anyway. We should be especially
            // careful to to prevent against exceptions/crashes due to invalid
            // messages.
            return;
        }

        if (team.getCoach().getPenalty() == Penalty.SplCoachMotion) {
            // Ignore messages from a penalised coach
            return;
        }

        long age = System.currentTimeMillis() - team.getTimestampCoachMessage();

        if (age >= SPLCoachMessage.SPL_COACH_MESSAGE_RECEIVE_INTERVAL) {
            // Enough time has passed
            team.setTimestampCoachMessage(System.currentTimeMillis());
            state.enqueueSplCoachMessage(this.message);
        }
    }
}
