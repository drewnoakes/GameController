package controller.action.ui.period;

import common.annotations.NotNull;
import controller.*;
import data.*;

/**
 * Moves the game into the second half of the overtime game period.
 *
 * @author Michel Bartsch
 */
public class SecondHalfOvertime extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        if (state.isFirstHalf() || state.getPeriod() == Period.PenaltyShootout) {
            state.setFirstHalf(false);
            state.setPeriod(Period.Overtime);
            state.setNextKickOffColor(game.initialKickOffColor().other());
            state.setPlayMode(PlayMode.Initial);
            FirstHalf.changeSide(game, state);
            game.pushState("2nd Half Extra Time");
        }
    }

    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        // TODO what is this first clause for?
        return (!state.isFirstHalf() && state.getPeriod() == Period.Overtime)
            || (state.getPeriod() == Period.Overtime && state.getPlayMode() == PlayMode.Finished)
            || state.isTestMode();
    }
}
