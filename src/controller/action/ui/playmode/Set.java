package controller.action.ui.playmode;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import controller.action.ui.period.FirstHalf;
import data.*;
import rules.Rules;

/**
 * Sets play mode to @{link PlayMode#Set}.
 *
 * @author Michel Bartsch
 */
public class Set extends GCAction
{
    /**
     * Creates a new Set action.
     * Look at the ActionBoard before using this.
     */
    public Set()
    {
        super(ActionType.UI);
    }

    /**
     * Performs this action to manipulate the data (model).
     * 
     * @param data      The current data to work on.
     */
    @Override
    public void perform(GameState data)
    {
        if (data.playMode == PlayMode.Set) {
            return;
        }
        if (Rules.league.returnRobotsInGameStoppages) {
            data.resetPenaltyTimes();
        }
        if (!data.playoff && data.timeBeforeCurrentPlayMode != 0) {
            data.addTimeInCurrentPlayMode();
        }
        data.whenCurrentPlayModeBegan = data.getTime();

        if (data.period == Period.PenaltyShootout) {
            data.timeBeforeCurrentPlayMode = 0;
            if (data.playMode != PlayMode.Initial) {
                data.kickOffTeam = data.kickOffTeam == TeamColor.Blue ? TeamColor.Red : TeamColor.Blue;
                FirstHalf.changeSide(data);
            }

            if (data.playMode != PlayMode.Playing) {
                data.team[data.team[0].teamColor == data.kickOffTeam ? 0 : 1].penaltyShot++;
            }
        }
        data.playMode = PlayMode.Set;
        Log.state(data, "Set");
    }
    
    /**
     * Checks if this action is legal with the given data (model).
     * Illegal actions are not performed by the EventHandler.
     * 
     * @param data      The current data to check with.
     */
    @Override
    public boolean isLegal(GameState data)
    {
        return data.playMode == PlayMode.Ready
            || data.playMode == PlayMode.Set
            || (data.period == Period.PenaltyShootout
              && (data.playMode != PlayMode.Playing || Rules.league.penaltyShotRetries)
              && !data.timeOutActive[0]
              && !data.timeOutActive[1]
              && !data.refereeTimeout)
            || data.testmode;
    }
}
