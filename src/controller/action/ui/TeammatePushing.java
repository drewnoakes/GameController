package controller.action.ui;

import common.Log;
import controller.EventHandler;
import controller.action.ActionType;
import controller.action.GCAction;
import data.AdvancedData;
import data.GameState;
import data.PlayerInfo;
import rules.Rules;

public class TeammatePushing extends GCAction {
    
    public TeammatePushing() {
        super(ActionType.UI);
    }

    @Override
    public void performOn(AdvancedData data, PlayerInfo player, int side, int number) {
        Log.state(data, "Teammate Pushing  " + data.team[side].teamColor + " " + (number+1));
    }

    @Override
    public boolean isLegal(AdvancedData data) {
        return Rules.league.dropInPlayerMode
               && (data.gameState == GameState.Ready
                || data.gameState == GameState.Playing)
               || data.testmode;
    }

    @Override
    public void perform(AdvancedData data) {
        if (EventHandler.getInstance().lastUIEvent == this) {
            EventHandler.getInstance().noLastUIEvent = true;
        }
    }
}
