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
    /**
     * Gets the maximum number of robots allowed on a team, including substitutes.
     * <p>
     * Robots must have sequential uniform numbers running from one up to this number.
     */
    public int getTeamSize();

    /** Gets the maximum number of robots a team may have on the field at any time, excluding substituted players and coaches. */
    public int getRobotsPlaying();

    /** If the clock may stop in certain play modes (Ready, Set) in a play-off game. */
    public boolean isPlayOffTimeStop();

    /** Gets the duration of each game half, in seconds. */
    int getNormalHalfDurationSeconds();

    /** Time in seconds one overtime half is long. */
    int getOvertimeHalfDurationSeconds();

    /** Gets the duration of the 'ready' play mode, in seconds. */
    int getReadyPlayModeDurationSeconds();

    /** Gets the duration of the delay between successive halves, both of normal and extra periods. */
    int getHalfTimeDurationSeconds();

    /** Gets whether the kick off team may be chosen during the 'initial' play mode of the first half of normal time. */
    boolean isKickOffTeamChoosableAtStartOfGame();

    /**
     * Gets the number of seconds for which the kick-off taking team have exclusive access to the ball and
     * centre circle, unless they move the ball sufficiently during this time.
     */
    int getKickOffDurationSeconds();

    /** Time in seconds before a global game stuck can be called (SPL only). */
    int getMinDurationBeforeStuckGameAllowed();

    /** If there is an overtime before the penalty shootout in a play-off game, in case of no winner during normal time. */
    boolean isOvertimeAllowed();

    /** If the game starts with penalty shootouts directly. */
    boolean isGameStartedWithPenaltyShots();

    /** Gets the duration of the delay between the last half and penalty shootouts. */
    int getDurationBeforePenaltyShootoutsStart();

    /** Time in seconds one penalty shoot is long. */
    int getPenaltyShotDurationSeconds();

    /** Gets whether a penalty attempt may be retried by returning the play mode to {@link data.PlayMode#Set}. */
    boolean arePenaltyShotRetriesAllowed();

    /** Time in seconds one penalty shoot is long in sudden-death. */
    int getPenaltyShotDurationSecondsInSuddenDeath();

    /** Gets the number of regular penalty shots in a normal (non-play-off) game. After this number occurs, sudden death commences. */
    int getNumberOfPenaltyShotsInNormalGame();

    /** Gets the number of regular penalty shots in a play-off game. After this number occurs, sudden death commences. */
    int getNumberOfPenaltyShotsInPlayOffGame();

    /**
     * Gets whether robot penalties should be lifted when the game stops (play mode changes to initial/ready/set/finish).
     * <p>
     * This causes the remaining penalty time to become zero, and the user must still manually unpenalize the robots
     * in the UI.
     */
    boolean arePenaltiesClearedDuringStoppages();

    /** Gets the maximum duration of a team's timeout, in seconds. Timeouts may be ended before completion. */
    int getTimeoutDurationSeconds();

    /** Whether calling a timeout gives the opponent the subsequent kick off or not. */
    boolean isKickOffGivenToOpponentAfterTimeout();

    /** Gets the duration of a referee timeout, in seconds. */
    int getRefereeTimeoutDurationSeconds();

    /** Defines if the option for a referee timeout is available. */
    boolean isRefereeTimeoutAvailable();

    /** Gets whether each team is allowed only time timeout per game half or not. */
    boolean isTeamAllowedOnlyOneTimeoutPerHalf();

    /** On how many pushes is a robot ejected. */
    int[] getPushesToEjection();

    /** Gets whether a coach may be used in this game (SPL only). */
    boolean isCoachAvailable();

    /** Gets whether the referee is able to add additional time to account for lost/stoppage time. */
    boolean isStoppageTimeAllowed();

    /** Gets whether to support legacy version 7 of the {@link controller.net.protocol.GameStateProtocol}. */
    boolean isLegacyGameStateVersion7Broadcast();

    /** Gets whether to support legacy version 8 of the {@link controller.net.protocol.GameStateProtocol}. */
    boolean isLegacyGameStateVersion8Broadcast();

    /** If true, the drop-in player competition is active. */
    boolean isDropInPlayerMode();
}
