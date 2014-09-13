package controller.action.ui.period;

import common.annotations.NotNull;
import controller.*;
import data.*;

/**
 * Moves the game into the second half of the normal game period.
 *
 * @author Michel Bartsch
 */
public class SecondHalf extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        if (state.isFirstHalf() || state.is(Period.PenaltyShootout)) {
            state.setFirstHalf(false);
            state.setPeriod(Period.Normal);
            state.setNextKickOffColor(game.initialKickOffColor().other());
            state.setPlayMode(PlayMode.Initial);
            FirstHalf.changeSide(game, state);
            // Don't set state.whenCurrentPlayModeBegan, because it's used to count the pause
            game.pushState("2nd Half");
        }
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return (!state.isFirstHalf() && state.is(Period.Normal))
            || (state.is(Period.Normal) && state.is(PlayMode.Finished))
            || state.isTestMode();
    }
}
