package controller.action;

import common.annotations.NotNull;
import controller.*;
import data.UISide;

/**
 * Interface for actions which may be executed against a particular robot.
 */
public interface RobotAction
{
    /**
     * Performs the actions on the specified robot.
     */
    void executeForRobot(@NotNull Game game, @NotNull WriteableGameState state, @NotNull WriteableTeamState team,
                         @NotNull WriteablePlayerState player, @NotNull UISide side);
}
