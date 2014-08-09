package controller.action.ui;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.action.ActionBoard;
import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;
import data.PlayMode;
import rules.Rules;

/**
 * This action means that a global game stuck has occurred.
 *
 * @author Michel Bartsch
 */
public class GlobalStuck extends GCAction
{
    /** On which side (0:left, 1:right) */
    private final int side;

    /**
     * @param side on which side (0:left, 1:right)
     */
    public GlobalStuck(int side)
    {
        super(ActionType.UI);
        this.side = side;
    }

    @Override
    public void perform(@NotNull GameState state, @Nullable String message)
    {
        state.kickOffTeam = state.team[side == 0 ? 1 : 0].teamColor;

        if (message == null) {
            if (state.getRemainingSeconds(state.whenCurrentPlayModeBegan, Rules.league.kickoffTime + Rules.league.minDurationBeforeStuck) > 0) {
                message = "Kickoff Goal " + state.team[side].teamColor;
            } else {
                message = "Global Game Stuck, Kickoff " + state.kickOffTeam;
            }
        }

        ActionBoard.ready.perform(state, message);
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return state.playMode == PlayMode.Playing || state.testmode;
    }
}
