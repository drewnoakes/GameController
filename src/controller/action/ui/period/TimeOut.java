package controller.action.ui.period;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.GameState;
import controller.action.ActionBoard;
import data.*;

/**
 * This action means that a timeOut is to be taken or ending.
 *
 * @author Michel Bartsch
 */
public class TimeOut extends Action
{
    /** On which side (0:left, 1:right) */
    private final int side;

    /**
     * @param side the side to whom the timeout belongs (0:left, 1:right)
     */
    public TimeOut(int side)
    {
        this.side = side;
    }

    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        if (!state.timeOutActive[side]) {
            // Starting a timeout
            state.previousPeriod = state.period;
            state.period = Period.Timeout;
            state.timeOutActive[side] = true;
            state.timeOutTaken[side] = true;
            if (state.previousPeriod != Period.PenaltyShootout) {
                if (game.settings().giveOpponentKickOffOnTimeOut)
                    state.nextKickOffColor = state.teams[side].teamColor.other();
            } else if (state.playMode == PlayMode.Set) {
                state.teams[state.nextKickOffColor == state.teams[0].teamColor ? 0 : 1].penaltyShot--;
            }
            ActionBoard.initial.forceExecute(game, state);
            game.pushState("Timeout " + state.teams[side].teamColor);
        } else {
            // Completing
            state.period = state.previousPeriod;
            state.previousPeriod = Period.Timeout;
            state.timeOutActive[side] = false;
            if (state.period != Period.PenaltyShootout) {
                ActionBoard.ready.forceExecute(game, state);
                game.pushState("End of Timeout " + state.teams[side].teamColor);
            }
        }
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
      return state.timeOutActive[side]
            || ((state.playMode == PlayMode.Initial ||
                  state.playMode == PlayMode.Ready ||
                  state.playMode == PlayMode.Set)
                && !state.timeOutTaken[side]
                && !state.timeOutActive[side == 0 ? 1 : 0]
                && state.period != Period.Timeout)
            || state.testmode;
    }
}
