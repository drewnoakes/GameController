package controller.action.ui;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.action.ActionBoard;
import controller.action.ActionTrigger;
import data.GameState;
import data.PlayMode;

/**
 * This action means that a global game stuck has occurred.
 *
 * @author Michel Bartsch
 */
public class GlobalStuck extends Action
{
    /** On which side (0:left, 1:right) */
    private final int side;

    /**
     * @param side on which side (0:left, 1:right)
     */
    public GlobalStuck(int side)
    {
        this.side = side;
    }

    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        state.kickOffTeam = state.team[side == 0 ? 1 : 0].teamColor;

        game.apply(ActionBoard.ready, ActionTrigger.User);

        if (state.getRemainingSeconds(state.whenCurrentPlayModeBegan, Game.settings.kickoffTime + Game.settings.minDurationBeforeStuck) > 0) {
            game.pushState("Kickoff Goal " + state.team[side].teamColor);
        } else {
            game.pushState("Global Game Stuck, Kickoff " + state.kickOffTeam);
        }
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return state.playMode == PlayMode.Playing || state.testmode;
    }
}
