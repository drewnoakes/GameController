package leagues;

/**
 * This class holds attributes defining rules.
 *
 * @author Michel Bartsch
 * @author Drew Noakes https://drewnoakes.com
 */
public abstract class LeagueSettings
{   
    protected LeagueSettings() {}

    /** How many robots are in a team. */
    public int teamSize;
    /** How many robots of each team may play at one time. */
    public int robotsPlaying;
    /** If the colors change automatically. */
    public boolean colorChangeAuto;
    /** If the clock may stop in certain play modes (Ready, Set) in a play-off game. */
    public boolean playOffTimeStop;
    /** Time in seconds one half is long. */
    public int halfTime;
    /** Length of the 'ready' play mode, in seconds. */
    public int readyTime;
    /** Time in seconds between first and second half. */
    public int pauseTime;
    /** If left and right side may both have the first kickoff. */
    public boolean kickoffChoice;
    /** Time in seconds the ball is blocked after kickoff. */
    public int kickoffTime;
    /** Time in seconds before a global game stuck can be called. */
    public int minDurationBeforeStuck;
    /** If there is an overtime before the penalty shoot-out in a play-off game. */
    public boolean overtime;
    /** Time in seconds one overtime half is long. */
    public int overtimeTime;
    /** If the game starts with penalty-shots. */
    public boolean startWithPenalty;
    /** Time in seconds between second half and penalty shoot-out. */
    public int pausePenaltyShootOutTime;
    /** Time in seconds one penalty shoot is long. */
    public int penaltyShotTime;
    /** If there can be a penalty-shot retry. */
    public boolean penaltyShotRetries;
    /** If there is a sudden-death. */
    public boolean suddenDeath;
    /** Time in seconds one penalty shoot is long in sudden-death. */
    public int penaltyShotTimeSuddenDeath;
    /** Number of penalty-shots for each team when a half has 10minutes. */
    public int numberOfPenaltyShotsShort;
    /** Number of penalty-shots for each team after full 10minutes playing. */
    public int numberOfPenaltyShotsLong;
    /** if robots should return from penalties when the game state changes. */
    public boolean returnRobotsInGameStoppages;
    /** Time in seconds one team has as timeOut. */
    public int timeOutTime;
    /** Whether calling a timeout gives the opponent the kickoff or not. */
    public boolean giveOpponentKickOffOnTimeOut;
    /** Time in seconds of a referee timeout*/
    public int refereeTimeout;
    /** Defines if the option for a referee timeout is available. */ 
    public boolean isRefereeTimeoutAvailable;
    /** One time-out per half? */
    public boolean timeOutPerHalf;
    /** On how many pushes is a robot ejected. */
    public int[] pushesToEjection;
    /** Defines if coach is available. */
    public boolean isCoachAvailable;
    /** Allowed to compensate for lost time? */
    public boolean lostTime;
    /** Whether to support version 7 of the game state protocol. */
    public boolean supportGameStateVersion7;
    /** Whether to support version 8 of the game state protocol. */
    public boolean supportGameStateVersion8;
    /** If true, the drop-in player competition is active*/
    public boolean dropInPlayerMode;
}
