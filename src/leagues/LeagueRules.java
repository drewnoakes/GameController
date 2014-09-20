package leagues;

/**
 * Models game rules specific to a particular league.
 * <p>
 * This data is read-only. Implementations should be immutable.
 *
 * @author Michel Bartsch
 * @author Drew Noakes https://drewnoakes.com
 */
public interface LeagueRules
{   
    /** How many robots are in a team. */
    public int getTeamSize();

    /** How many robots of each team may play at one time. */
    public int getRobotsPlaying();

    /** If the clock may stop in certain play modes (Ready, Set) in a play-off game. */
    public boolean isPlayOffTimeStop();

    /** Time in seconds one half is long. */
    int getHalfTime();

    /** Length of the 'ready' play mode, in seconds. */
    int getReadyTime();

    /** Time in seconds between first and second half. */
    int getPauseTime();

    /** If left and right side may both have the first kickoff. */
    boolean isKickoffChoice();

    /** Time in seconds the ball is blocked after kickoff. */
    int getKickoffTime();

    /** Time in seconds before a global game stuck can be called. */
    int getMinDurationBeforeStuck();

    /** If there is an overtime before the penalty shoot-out in a play-off game. */
    boolean isOvertime();

    /** Time in seconds one overtime half is long. */
    int getOvertimeTime();

    /** If the game starts with penalty-shots. */
    boolean isStartWithPenalty();

    /** Time in seconds between second half and penalty shoot-out. */
    int getPausePenaltyShootOutTime();

    /** Time in seconds one penalty shoot is long. */
    int getPenaltyShotTime();

    /** If there can be a penalty-shot retry. */
    boolean isPenaltyShotRetries();

    /** Time in seconds one penalty shoot is long in sudden-death. */
    int getPenaltyShotTimeSuddenDeath();

    /** Number of penalty-shots for each team when a half has 10minutes. */
    int getNumberOfPenaltyShotsShort();

    /** Number of penalty-shots for each team after full 10minutes playing. */
    int getNumberOfPenaltyShotsLong();

    /** if robots should return from penalties when the game state changes. */
    boolean isReturnRobotsInGameStoppages();

    /** Time in seconds one team has as timeOut. */
    int getTimeOutTime();

    /** Whether calling a timeout gives the opponent the kickoff or not. */
    boolean isGiveOpponentKickOffOnTimeOut();

    /** Time in seconds of a referee timeout*/
    int getRefereeTimeout();

    /** Defines if the option for a referee timeout is available. */
    boolean isRefereeTimeoutAvailable();

    /** One time-out per half? */
    boolean isTimeOutPerHalf();

    /** On how many pushes is a robot ejected. */
    int[] getPushesToEjection();

    /** Defines if coach is available. */
    boolean isCoachAvailable();

    /** Allowed to compensate for lost time? */
    boolean isLostTime();

    /** Whether to support version 7 of the game state protocol. */
    boolean isSupportGameStateVersion7();

    /** Whether to support version 8 of the game state protocol. */
    boolean isSupportGameStateVersion8();

    /** If true, the drop-in player competition is active. */
    boolean isDropInPlayerMode();
}
