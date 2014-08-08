package controller.action.net;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;
import data.PlayMode;
import data.Penalty;

/**
 * This action means that a player has been penalised or unpenalised manually via a @{link data.RobotMessage}.
 *
 * @author Michel Bartsch
 */
public class Manual extends GCAction
{
    /** On which side (0:left, 1:right) */
    private final int side;
    /** The players`s number, beginning with 0! */
    private final int number;
    /** If true, this action means manual unpenalising, otherwise manual penalising.  */
    private final boolean unpen;
    
    /**
     * Creates a new Manual action.
     * Look at the ActionBoard before using this.
     * 
     * @param side      On which side (0:left, 1:right)
     * @param number    The players`s number, beginning with 0!
     * @param unpen     If true, this action means manual unpenalising,
     *                  otherwise manual penalising.
     */
    public Manual(int side, int number, boolean unpen)
    {
        super(ActionType.NET);
        this.side = side;
        this.number = number;
        this.unpen = unpen;
    }

    @Override
    public void perform(GameState state)
    {
        if (!unpen) {
            state.team[side].player[number].penalty = Penalty.Manual;
            state.whenPenalized[side][number] = state.getTime();

            if (state.playMode != PlayMode.Initial && state.playMode != PlayMode.Finished) {
                Log.state(state, "Manually Penalised " + state.team[side].teamColor + " " + (number+1));
            }
        } else {
            state.team[side].player[number].penalty = Penalty.None;

            if (state.playMode != PlayMode.Initial && state.playMode != PlayMode.Finished) {
                Log.state(state, "Manually Unpenalised " + state.team[side].teamColor + " " + (number+1));
            }
        }
    }
    
    @Override
    public boolean isLegal(GameState state)
    {
        return true;
    }
}
