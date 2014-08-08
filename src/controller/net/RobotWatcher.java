package controller.net;

import controller.EventHandler;
import controller.action.ActionBoard;
import data.RobotMessage;
import data.Penalty;
import rules.Rules;

/**
 * Processes messages received from robots, triggering manual penalisation/unpenalisation and tracking who is online.
 *
 * This class is a singleton!
 *
 * @author Marcel Steinbeck
 * @author Michel Bartsch
 */
public class RobotWatcher
{
    /** The instance of the singleton. */
    private static final RobotWatcher instance = new RobotWatcher();

    /** A timestamp when the last reply from each robot was received. */
    private final long [][] robotLastHeardTime = Rules.league.isCoachAvailable ? new long[2][Rules.league.teamSize+1] : new long[2][Rules.league.teamSize];
    /** Last status received from each robot. */
    private final RobotStatus[][] robotLastStatus = Rules.league.isCoachAvailable ? new RobotStatus[2][Rules.league.teamSize+1] : new RobotStatus[2][Rules.league.teamSize];
    /** The calculated information about the online-status. */
    private final RobotOnlineStatus [][] status = Rules.league.isCoachAvailable ? new RobotOnlineStatus[2][Rules.league.teamSize+1] : new RobotOnlineStatus[2][Rules.league.teamSize];

    private final static int MILLIS_UNTIL_ROBOT_IS_OFFLINE = 4*1000;
    private final static int MILLIS_UNTIL_ROBOT_HAS_HIGH_LATENCY = 2*1000;

    /**
     * Creates a new RobotWatcher.
     */
    private RobotWatcher()
    {
        // Initialise array structures
        for (int i  = 0; i < 2; i++) {
            for (int j = 0; j < Rules.league.teamSize; j++) {
                robotLastStatus[i][j] = null;
                status[i][j] = RobotOnlineStatus.UNKNOWN;
            }
            if (Rules.league.isCoachAvailable) {
                status[i][Rules.league.teamSize] = RobotOnlineStatus.UNKNOWN;
            }
        }
    }
    
    /**
     * Integrates messages received from robots, updating corresponding timestamps and firing
     * actions required by manual penalising/unpenalising of the robot.
     * 
     * @param robotMessage a message received from a robot
     */
    public static synchronized void update(RobotMessage robotMessage)
    {
        int team;
        if (robotMessage.getTeamNumber() == EventHandler.getInstance().state.team[0].teamNumber) {
            team = 0;
        } else if (robotMessage.getTeamNumber() == EventHandler.getInstance().state.team[1].teamNumber) {
            team = 1;
        } else {
            return;
        }
        int number = robotMessage.getPlayerNumber();
        if (number <= 0 || number > Rules.league.teamSize) {
            return;
        }
        instance.robotLastHeardTime[team][number-1] = System.currentTimeMillis();
        if (instance.robotLastStatus[team][number-1] != robotMessage.getStatus()) {
            instance.robotLastStatus[team][number-1] = robotMessage.getStatus();
            if ((robotMessage.getStatus() == RobotStatus.ManuallyPenalised)
                    && (EventHandler.getInstance().state.team[team].player[number-1].penalty == Penalty.None)) {
                ActionBoard.manualPen[team][number-1].actionPerformed(null);
            } else if ((robotMessage.getStatus() == RobotStatus.ManuallyUnpenalised)
                    && (EventHandler.getInstance().state.team[team].player[number-1].penalty != Penalty.None)) {
                ActionBoard.manualUnpen[team][number-1].actionPerformed(null);
            }
        }
    }

    /**
     * Calculates new online-status for each robot.
     * 
     * @return The updated online-status of each robot.
     */
    public static synchronized RobotOnlineStatus[][] updateRobotOnlineStatus()
    {
        long currentTime = System.currentTimeMillis();
        for (int i=0; i<2; i++) {
            int robotsOffline = 0;
            for (int j=0; j < instance.status[i].length; j++) {
                if (currentTime - instance.robotLastHeardTime[i][j] > MILLIS_UNTIL_ROBOT_IS_OFFLINE) {
                    instance.status[i][j] = RobotOnlineStatus.OFFLINE;
                    if (++robotsOffline >= Rules.league.teamSize + (Rules.league.isCoachAvailable ? 1 : 0)) {
                        for (int k=0; k < Rules.league.teamSize; k++) {
                            instance.status[i][k] = RobotOnlineStatus.UNKNOWN;
                        }
                        if (Rules.league.isCoachAvailable) {
                            instance.status[i][Rules.league.teamSize] = RobotOnlineStatus.UNKNOWN;
                        }
                    }
                } else if (currentTime - instance.robotLastHeardTime[i][j] > MILLIS_UNTIL_ROBOT_HAS_HIGH_LATENCY) {
                    instance.status[i][j] = RobotOnlineStatus.HIGH_LATENCY;
                } else {
                    instance.status[i][j] = RobotOnlineStatus.ONLINE;
                }
            }
        }
        return instance.status;
    }
    
    public static synchronized void updateCoach(byte team)
    {
        instance.robotLastHeardTime[team][Rules.league.teamSize] = System.currentTimeMillis();
    }
}