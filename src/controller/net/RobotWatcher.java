package controller.net;

import controller.ActionHandler;
import controller.action.ActionBoard;
import data.RobotMessage;
import data.Penalty;
import rules.Rules;

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
    private final int robotCount = Rules.league.teamSize + (Rules.league.isCoachAvailable ? 1 : 0);

    /** A timestamp when the last reply from each robot was received. */
    private final long[][] robotLastHeardTime = new long[2][robotCount];

    /** Last status received from each robot. */
    private final RobotStatus[][] robotLastStatus = new RobotStatus[2][robotCount];

    /** The calculated information about the online-status. */
    private final RobotOnlineStatus[][] status = new RobotOnlineStatus[2][robotCount];

    private final static int MILLIS_UNTIL_ROBOT_IS_OFFLINE = 4*1000;
    private final static int MILLIS_UNTIL_ROBOT_HAS_HIGH_LATENCY = 2*1000;

    public RobotWatcher()
    {
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
     * @param robotMessage a message received from a robot
     */
    public synchronized void update(RobotMessage robotMessage)
    {
        int team;
        if (robotMessage.getTeamNumber() == ActionHandler.getInstance().state.team[0].teamNumber) {
            team = 0;
        } else if (robotMessage.getTeamNumber() == ActionHandler.getInstance().state.team[1].teamNumber) {
            team = 1;
        } else {
            return;
        }
        int number = robotMessage.getPlayerNumber();
        if (number <= 0 || number > Rules.league.teamSize) {
            return;
        }
        robotLastHeardTime[team][number-1] = System.currentTimeMillis();
        if (robotLastStatus[team][number-1] != robotMessage.getStatus()) {
            robotLastStatus[team][number-1] = robotMessage.getStatus();
            if ((robotMessage.getStatus() == RobotStatus.ManuallyPenalised)
                    && (ActionHandler.getInstance().state.team[team].player[number-1].penalty == Penalty.None)) {
                ActionBoard.manualPen[team][number-1].invoke();
            } else if ((robotMessage.getStatus() == RobotStatus.ManuallyUnpenalised)
                    && (ActionHandler.getInstance().state.team[team].player[number-1].penalty != Penalty.None)) {
                ActionBoard.manualUnpen[team][number-1].invoke();
            }
        }
    }

    public synchronized void updateCoach(int teamNumber)
    {
        int team;
        if (teamNumber == ActionHandler.getInstance().state.team[0].teamNumber) {
            team = 0;
        } else if (teamNumber == ActionHandler.getInstance().state.team[1].teamNumber) {
            team = 1;
        } else {
            return;
        }
        robotLastHeardTime[team][Rules.league.teamSize] = System.currentTimeMillis();
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