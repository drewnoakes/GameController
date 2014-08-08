package controller.action.ui;

import common.Log;
import controller.EventHandler;
import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;
import data.PlayMode;
import data.PlayerInfo;
import rules.Rules;

public class TeammatePushing extends GCAction {
    
    public TeammatePushing() {
        super(ActionType.UI);
    }

    @Override
    public void performOn(GameState state, PlayerInfo player, int side, int number) {
        Log.state(state, "Teammate Pushing  " + state.team[side].teamColor + " " + (number+1));
    }

    @Override
    public void perform(GameState state) {
        if (EventHandler.getInstance().lastUIAction == this) {
            EventHandler.getInstance().noLastUIAction = true;
        }
    }

    @Override
    public boolean isLegal(GameState state) {
        return Rules.league.dropInPlayerMode
                && (state.playMode == PlayMode.Ready || state.playMode == PlayMode.Playing)
                || state.testmode;
    }
}
