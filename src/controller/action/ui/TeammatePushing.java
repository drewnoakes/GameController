package controller.action.ui;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.EventHandler;
import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;
import data.PlayMode;
import data.PlayerInfo;
import rules.Rules;

public class TeammatePushing extends GCAction
{
    public TeammatePushing()
    {
        super(ActionType.UI);
    }

    @Override
    public void performOn(@NotNull GameState state, @NotNull PlayerInfo player, int side, int number)
    {
        log(state, null, "Teammate Pushing  " + state.team[side].teamColor + " " + (number + 1));
    }

    @Override
    public void perform(@NotNull GameState state, @Nullable String message)
    {
        if (EventHandler.getInstance().lastUIAction == this) {
            EventHandler.getInstance().noLastUIAction = true;
        }
    }

    @Override
    public boolean isLegal(GameState state)
    {
        return Rules.league.dropInPlayerMode
                && (state.playMode == PlayMode.Ready || state.playMode == PlayMode.Playing)
                || state.testmode;
    }
}
