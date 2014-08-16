package leagues;

/**
 * This class sets attributes given by the humanoid-league rules.
 *
 * @author Michel-Zen
 */
public abstract class HL extends LeagueSettings
{
    HL()
    {
        /** If the colors change automatically. */
        changeColoursEachPeriod = false;
        /** If the clock may stop in certain play modes (Ready, Set) in a play-off game. */
        playOffTimeStop = false;
        /** Time in seconds one half is long. */
        halfTime = 10*60;
        /** Length of the 'ready' play mode, in seconds. */
        readyTime = 30;
        /** Time in seconds between first and second half. */
        pauseTime = 5*60;
        /** If left and right side may both have the first kickoff. */
        kickoffChoice = true;
        /** Time in seconds the ball is blocked after kickoff. */
        kickoffTime = 10;
        /** Time in seconds before a global game stuck can be called. */
        minDurationBeforeStuck = 30;
        /** If there is an overtime before penalty-shoot in a play-off game. */
        overtime = true;
        /** Time in seconds one overtime half is long. */
        overtimeTime = 5*60;
        /** If the game starts with penalty-shoots. */
        startWithPenalty = false;
        /** Time in seconds between second half and penalty shoot. */
        pausePenaltyShootOutTime = 0;
        /** If there can be a penalty-shoot retry. */
        penaltyShotRetries = true;
        /** Time in seconds one penalty shoot is long. */
        penaltyShotTime = 1*60;
        /** If there is a sudden-death. */
        suddenDeath = false;
        /** Time in seconds one penalty shoot is long in sudden-death. */
        penaltyShotTimeSuddenDeath = 2*60; // does not matter
        /** Number of penalty-shoots for each team when a half has 10minutes. */
        numberOfPenaltyShotsShort = 5;
        /** Number of penalty-shoots for each team after full 10minutes playing. */
        numberOfPenaltyShotsLong = 5;
        /** if robots should return from penalties when the game state changes. */
        returnRobotsInGameStoppages = false;
        /** Time in seconds one team has as timeOut. */
        timeOutTime = 2*60;
        /** Whether calling a timeout gives the opponent the kickoff or not. */
        giveOpponentKickOffOnTimeOut = false;
        /** One time-out per half? */
        timeOutPerHalf = true;
        /** On how many pushes is a robot ejected. */
        pushesToEjection = new int[] {};
        /** Defines if the option for a referee timeout is available */
        isRefereeTimeoutAvailable = false;
        /** Defines if coach is available. */
        isCoachAvailable = false;
        /** Allowed to compensate for lost time? */
        lostTime = false;
        /** Whether to support version 7 of the game state protocol. */
        supportGameStateVersion7 = true;
        /** Whether to support version 8 of the game state protocol. */
        supportGameStateVersion8 = true;
        /** If true, the drop-in player competition is active*/
        dropInPlayerMode = false;
    }
}