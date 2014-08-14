package controller.net;

import controller.Game;
import controller.action.ActionBoard;
import controller.action.ActionTrigger;
import data.League;
import data.RobotMessage;
import data.Penalty;

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

    public RobotWatcher(League league)
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
    
    /**
     * Integrates messages received from robots, updating corresponding timestamps and firing
     * actions required by manual penalising/unpenalising of the robot.
     *
     * @param game the active game
     * @param robotMessage a message received from a robot
     */
    public synchronized void update(Game game, RobotMessage robotMessage)
    {
        int team = game.getGameState().getTeamIndex(robotMessage.getTeamNumber());
        if (team == -1)
            return;

        int number = robotMessage.getPlayerNumber();
        if (number <= 0 || number > game.settings().teamSize)
            return;

        int i = number - 1;

        robotLastHeardTime[team][i] = System.currentTimeMillis();

        if (robotLastStatus[team][i] == robotMessage.getStatus())
            return;

        robotLastStatus[team][i] = robotMessage.getStatus();

        if (robotMessage.getStatus() == RobotStatus.ManuallyPenalised) {
            if (game.getGameState().team[team].player[i].penalty == Penalty.None)
                game.apply(ActionBoard.manualPen[team][i], ActionTrigger.Network);
        } else if (robotMessage.getStatus() == RobotStatus.ManuallyUnpenalised) {
            if (game.getGameState().team[team].player[i].penalty != Penalty.None)
                game.apply(ActionBoard.manualUnpen[team][i], ActionTrigger.Network);
        }
    }

    public synchronized void updateCoach(Game game, byte teamNumber)
    {
        int team = game.getGameState().getTeamIndex(teamNumber);
        if (team != -1)
            robotLastHeardTime[team][game.settings().teamSize] = System.currentTimeMillis();
    }

    /**
     * Calculates new online-status for each robot.
     * 
     * @return the updated online-status of each robot.
     */
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