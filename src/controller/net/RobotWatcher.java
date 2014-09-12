package controller.net;

import common.annotations.NotNull;
import controller.Game;
import controller.ReadOnlyTeamState;
import controller.action.ActionBoard;
import controller.action.ActionTrigger;
import data.League;
import data.RobotMessage;
import data.Penalty;
import data.UISide;

/**
 * Processes messages received from robots, triggering manual penalisation/unpenalisation and tracking who is online.
 *
 * @author Marcel Steinbeck
 * @author Michel Bartsch
 * @author Drew Noakes https://drewnoakes.com
 */
public class RobotWatcher
{
    /** The number of robots on each team, including any coach. */
    private final int robotCount;

    /** A timestamp when the last reply from each robot was received. */
    private final long[][] robotLastHeardTime;

    /** Last status received from each robot. */
    private final RobotStatus[][] robotLastStatus;

    /** The calculated information about the online-status. */
    private final RobotOnlineStatus[][] status;

    private final static int MILLIS_UNTIL_ROBOT_IS_OFFLINE = 4*1000;
    private final static int MILLIS_UNTIL_ROBOT_HAS_HIGH_LATENCY = 2*1000;

    public RobotWatcher(@NotNull League league)
    {
        robotCount = league.settings().teamSize + (league.settings().isCoachAvailable ? 1 : 0);
        robotLastStatus = new RobotStatus[2][robotCount];
        status = new RobotOnlineStatus[2][robotCount];
        robotLastHeardTime = new long[2][robotCount];

        // Initialise array structures
        for (int i  = 0; i < 2; i++) {
            for (int j = 0; j < robotCount; j++) {
                robotLastStatus[i][j] = null;
                status[i][j] = RobotOnlineStatus.UNKNOWN;
            }
        }
    }

    private int getTeamIndex(Game game, int teamNumber)
    {
        if (game.teams().get(UISide.Left).getNumber() == teamNumber)
            return 0;
        if (game.teams().get(UISide.Right).getNumber() == teamNumber)
            return 1;
        return -1;
    }
    
    /**
     * Integrates messages received from robots, updating corresponding timestamps and triggering
     * actions required by manual penalising/unpenalising of the robot.
     *
     * @param game the active game
     * @param robotMessage a message received from a robot
     */
    public synchronized void update(@NotNull Game game, @NotNull RobotMessage robotMessage)
    {
        int teamIndex = getTeamIndex(game, robotMessage.getTeamNumber());

        if (teamIndex == -1)
            return;

        int number = robotMessage.getUniformNumber();

        if (number <= 0 || number > game.settings().teamSize)
            return;

        int i = number - 1;

        robotLastHeardTime[teamIndex][i] = System.currentTimeMillis();

        if (robotLastStatus[teamIndex][i] == robotMessage.getStatus())
            return;

        robotLastStatus[teamIndex][i] = robotMessage.getStatus();

        ReadOnlyTeamState team = game.getGameState().getTeam(robotMessage.getTeamNumber());

        assert(team != null);

        // TODO need a better way of tracking robot online statuses -- it's based off team numbers, not sides
        UISide side = game.teams().get(UISide.Left).getNumber() == robotMessage.getTeamNumber() ? UISide.Left : UISide.Right;

        if (robotMessage.getStatus() == RobotStatus.ManuallyPenalised) {
            if (team.getPlayer(number).getPenalty() == Penalty.None)
                game.apply(ActionBoard.manualPen.get(side)[i], ActionTrigger.Network);
        } else if (robotMessage.getStatus() == RobotStatus.ManuallyUnpenalised) {
            if (team.getPlayer(number).getPenalty() != Penalty.None)
                game.apply(ActionBoard.manualUnpen.get(side)[i], ActionTrigger.Network);
        }
    }

    public synchronized void updateCoach(@NotNull Game game, byte teamNumber)
    {
        int teamIndex = getTeamIndex(game, teamNumber);

        if (teamIndex != -1)
            robotLastHeardTime[teamIndex][game.settings().teamSize] = System.currentTimeMillis();
    }

    /**
     * Calculates new online-status for each robot.
     * 
     * @return the updated online-status of each robot.
     */
    @NotNull
    public synchronized RobotOnlineStatus[][] updateRobotOnlineStatus()
    {
        long currentTime = System.currentTimeMillis();

        for (int i=0; i<2; i++) {
            int robotsOffline = 0;
            for (int j=0; j < status[i].length; j++) {
                long age = currentTime - robotLastHeardTime[i][j];
                if (age > MILLIS_UNTIL_ROBOT_IS_OFFLINE) {
                    status[i][j] = RobotOnlineStatus.OFFLINE;
                    // If the whole team is offline...
                    if (++robotsOffline >= robotCount) {
                        // ...set all robots on the team to 'unknown' status
                        for (int k=0; k < robotCount; k++) {
                            status[i][k] = RobotOnlineStatus.UNKNOWN;
                        }
                    }
                } else if (age > MILLIS_UNTIL_ROBOT_HAS_HIGH_LATENCY) {
                    status[i][j] = RobotOnlineStatus.HIGH_LATENCY;
                } else {
                    status[i][j] = RobotOnlineStatus.ONLINE;
                }
            }
        }
        return status;
    }
}