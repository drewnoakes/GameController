package controller.action.ui;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.action.ActionTrigger;
import controller.action.GCAction;
import data.GameState;
import data.PlayMode;

/**
 * This action means that the ball is out.
 *
 * @author Michel Bartsch
 */
public class Out extends GCAction
{
    /** On which side (0:left, 1:right) */
    private final int side;

    /**
     * @param side on which side (0:left, 1:right)
     */
    public Out(int side)
    {
        super(ActionTrigger.User);
        this.side = side;
    }

    @Override
    public void perform(@NotNull GameState state, @Nullable String message)
    {
        state.whenDropIn = state.getTime();
        state.dropInTeam = state.team[side].teamColor;
        log(state, message, "Out by " + state.team[side].teamColor);
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return state.playMode == PlayMode.Playing || state.testmode;
    }
}
