package rules;

/**
 * This class sets attributes given by the spl rules.
 *
 * @author Michel-Zen
 */
public class SPL extends Rules
{
    SPL()
    {
        /** The league´s name this rules are for. */
        leagueName = "SPL";
        /** The league´s directory name with its teams and icons. */
        leagueDirectory = "spl";
        /** How many robots are in a team. */
        teamSize = 6; // 5 players + 1 sub
        /** How many robots of each team may play at one time. */
        robotsPlaying = 5;
        /** If the colors change automatically. */
        colorChangeAuto = false;
        /** If the clock may stop in certain play modes (Ready, Set) in a play-off game. */
        playOffTimeStop = true;
        /** Time in seconds one half is long. */
        halfTime = 10*60;
        /** Length of the 'ready' play mode, in seconds. */
        readyTime = 45;
        /** Time in seconds between first and second half. */
        pauseTime = 10*60;
        /** If left and right side may both have the first kickoff. */
        kickoffChoice = false;
        /** Time in seconds the ball is blocked after kickoff. */
        kickoffTime = 10;
        /** Time in seconds before a global game stuck can be called. */
        minDurationBeforeStuck = 15;
        /** If there is an overtime before penalty-shoot in a play-off game. */
        overtime = false;
        /** Time in seconds one overtime half is long. */
        overtimeTime = 0;
        /** If the game starts with penalty-shoots. */
        startWithPenalty = false;
       /** Time in seconds between second half and penalty shoot. */
        pausePenaltyShootOutTime = 5*60;
        /** Time in seconds one penalty shoot is long. */
        penaltyShotTime = 1*60;
        /** If there can be a penalty-shoot retry. */
        penaltyShotRetries = false;
        /** If there is a sudden-death. */
        suddenDeath = true;
        /** Time in seconds one penalty shoot is long in sudden-death. */
        penaltyShotTimeSuddenDeath = 2*60;
        /** Number of penalty-shoots for each team when a half has 10minutes. */
        numberOfPenaltyShotsShort = 3;
        /** Number of penalty-shoots for each team after full 10minutes playing. */
        numberOfPenaltyShotsLong = 5;
        /** if robots should return from penalties when the game state changes. */
        returnRobotsInGameStoppages = true;
        /** Time in seconds one team has as timeOut. */
        timeOutTime = 5*60;
        /** Whether calling a timeout gives the opponent the kickoff or not. */
        giveOpponentKickOffOnTimeOut = true;
        /** Time in seconds of a referee timeout*/
        refereeTimeout = 10*60;
        /** Defines if the option for a referee timeout is available */
        isRefereeTimeoutAvailable = true;
        /** One time-out per half? */
        timeOutPerHalf = false;
        /** On how many pushes is a robot ejected. */
        pushesToEjection = new int[] {4, 6, 8, 10, 12};
        /** Defines if coach is available. */
        isCoachAvailable = true;
        /** Allowed to compensate for lost time? */
        lostTime = true;
        /** Whether to support version 7 of the game state protocol. */
        supportGameStateVersion7 = false;
        /** Whether to support version 8 of the game state protocol. */
        supportGameStateVersion8 = false;
        /** If true, the drop-in player competition is active*/
        dropInPlayerMode = false;
    }
}