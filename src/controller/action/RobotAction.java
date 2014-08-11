package controller.action;

import common.annotations.NotNull;
import controller.Game;
import data.GameState;
import data.PlayerInfo;

/**
 * Interface for actions which may be executed against a particular robot.
 */
public interface RobotAction
{
    /**
     * Performs the actions on the specified robot.
     */
    void executeForRobot(@NotNull Game game, @NotNull GameState state, @NotNull PlayerInfo player, int side, int number);
}
