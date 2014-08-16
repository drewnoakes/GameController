package controller;

import java.io.Serializable;
import java.util.ArrayList;

import common.Log;
import common.annotations.NotNull;
import controller.action.ActionBoard;
import data.*;
import leagues.LeagueSettings;

/**
 * This class extends the GameState that is send to the robots. It
 * contains all the additional information the GameController needs to
 * represent a state of the game, for example time in millis.
 *
 * There are no synchronized get and set methods because in this architecture.
 * Only actions in their {@link controller.Action#execute} method are
 * allowed to write into this and they are all in the same thread.
 *
 * @author Michel Bartsch
 */
public class GameState extends GameStateSnapshot implements Cloneable
{
    transient Game game;

    /** How much time summed up before the current play mode? (ms)*/
    public long timeBeforeCurrentPlayMode;
    
    /** When was switched to the current play mode? (ms) */
    public long whenCurrentPlayModeBegan;
    
    /** When was the last drop-in? (ms, 0 = never) */
    public long whenDropIn;
    
    /** When was each player penalized last (ms, 0 = never)? */
    public final long[][] whenPenalized;

    /** Which players were already ejected? */
    public final boolean[][] ejected;
    
    /** Pushing counters for each team, 0:left side, 1:right side. */
    public final int[] pushes = {0, 0};
    
    /** If true, the referee set a timeout. */
    public boolean refereeTimeout = false;

    /** If true, this team is currently taking a timeOut, 0:left side, 1:right side. */
    public final boolean[] timeOutActive = {false, false};
    
    /** TimeOut counters for each team, 0:left side, 1:right side. */
    public boolean[] timeOutTaken = {false, false};
    
    /** If true, left side has the kickoff. */
    public boolean leftSideKickoff = true;

    /** If true, the testmode has been activated. */
    public boolean testmode = false;

    /** If true, the clock has manually been paused in the testmode. */
    public boolean manPause = false;
    
    /** If true, the clock has manually been started in the testmode. */
    public boolean manPlay = false;
    
    /** When was the last manual intervention to the clock? */
    public long manWhenClockChanged;
    
    /** Time offset resulting from manually stopping the clock. */
    public long manTimeOffset;
    
    /** Time offset resulting from starting the clock when it should be stopped. */
    public long manRemainingGameTimeOffset;

    /** Used to backup the secondary game state during a timeout. */
    public Period previousPeriod = Period.Normal;

    /** Keeps the penalties for the players if there are substituted. */
    public final ArrayList<ArrayList<PenaltyQueueData>> penaltyQueueForSubPlayers = new ArrayList<ArrayList<PenaltyQueueData>>();

    /** The timestamps of when coach messages were received. */
    public final long timestampCoachMessage[] = {0, 0};

    /** Keep the coach messages. */
    public final ArrayList<SPLCoachMessage> splCoachMessageQueue = new ArrayList<SPLCoachMessage>();

    public GameState(@NotNull Game game)
    {
        super(game);

        this.game = game;

        if (game.settings().startWithPenalty) {
            period = Period.PenaltyShootout;
        }

        for (int i=0; i<2; i++) {
            for (int j=0; j < team[i].player.length; j++) {
                if (j >= game.settings().robotsPlaying) {
                    team[i].player[j].penalty = Penalty.Substitute;
                }
            }
            penaltyQueueForSubPlayers.add(new ArrayList<PenaltyQueueData>());
        }

        whenPenalized = game.settings().isCoachAvailable ? new long[2][game.settings().teamSize+1] : new long[2][game.settings().teamSize];
        ejected = game.settings().isCoachAvailable ? new boolean[2][game.settings().teamSize+1] : new boolean[2][game.settings().teamSize];
    }

    /**
     * Returns the current time. Can be stopped in test mode.
     * @return The current time in ms. May become incompatible to
     *         the time delivered by System.currentTimeMillis().
     */
    public long getTime()
    {
        return manPause ? manWhenClockChanged : System.currentTimeMillis() + manTimeOffset;
    }
    
    /**
     * Returns the number of seconds since a certain timestamp.
     * @param millis The timestamp in ms.
     * @return The number of seconds since the timestamp.
     */
    public int getSecondsSince(long millis)
    {
        return millis == 0 ? 100000 : (int) (getTime() - millis) / 1000;
    }
    
    /**
     * The number of seconds until a certain duration is over. The time
     * already passed is specified as a timestamp when it began.
     *
     * @param millis The timestamp in ms.
     * @param durationInSeconds The full duration in s.
     * @return The number of seconds that still remain from the duration. Can be negative.
     */
    public int getRemainingSeconds(long millis, int durationInSeconds)
    {
        return durationInSeconds - getSecondsSince(millis);
    }

    /**
     * Update all durations in the GameState packet.
     */
    public void updateTimes()
    {
        secsRemaining = (short) getRemainingGameTime();
        dropInTime = whenDropIn == 0 ? -1 : (short) getSecondsSince(whenDropIn);
        Integer subT = getSecondaryTime(0);

        if (subT == null) {
            secondaryTime = 0;
        } else {
            secondaryTime = (short)(int)subT;
        }
        for (int side = 0; side < team.length; ++side) {
            for (int number = 0; number < team[side].player.length; ++number) {
                PlayerState player = team[side].player[number];
                player.secsTillUnpenalised = player.penalty == Penalty.None
                        ? 0
                        : (byte) getRemainingPenaltyTime(side, number);
            }
        }
    }
    
    /**
     * Add the time passed in the current play mode to the time that already passed before.
     * Is usually called during changes of the play mode.
     */
    public void addTimeInCurrentPlayMode()
    {
        timeBeforeCurrentPlayMode += getTime() - whenCurrentPlayModeBegan;
    }
    
    /**
     * Calculates the remaining game time in the current phase of the game.
     * This is what the primary clock will show.
     * @return The remaining number of seconds.
     */
    public int getRemainingGameTime()
    {
        int regularNumberOfPenaltyShots = game.isPlayOff()
                ? game.settings().numberOfPenaltyShotsLong
                : game.settings().numberOfPenaltyShotsShort;

        int duration = period == Period.Timeout
                ? secsRemaining
                : period == Period.Normal
                    ? game.settings().halfTime
                    : period == Period.Overtime
                        ? game.settings().overtimeTime
                        : Math.max(team[0].penaltyShot, team[1].penaltyShot) > regularNumberOfPenaltyShots
                            ? game.settings().penaltyShotTimeSuddenDeath
                            : game.settings().penaltyShotTime;
        int timePlayed = playMode == PlayMode.Initial// during timeouts
                || (playMode == PlayMode.Ready || playMode == PlayMode.Set)
                && (game.isPlayOff() && game.settings().playOffTimeStop || timeBeforeCurrentPlayMode == 0)
                || playMode == PlayMode.Finished
            ? (int) ((timeBeforeCurrentPlayMode + manRemainingGameTimeOffset + (manPlay ? System.currentTimeMillis() - manWhenClockChanged : 0)) / 1000)
            : getSecondsSince(whenCurrentPlayModeBegan - timeBeforeCurrentPlayMode - manRemainingGameTimeOffset);

        return duration - timePlayed;
    }

    /**
     * The method returns the remaining pause time.
     * @return The remaining number of seconds of the game pause or null if there currently is no pause.
     */
    public Integer getRemainingPauseTime()
    {
        if (period == Period.Normal
                && (playMode == PlayMode.Initial && !firstHalf && !timeOutActive[0] && !timeOutActive[1]
                || playMode == PlayMode.Finished && firstHalf)) {
            return getRemainingSeconds(whenCurrentPlayModeBegan, game.settings().pauseTime);
        } else if (game.settings().pausePenaltyShootOutTime != 0 && game.isPlayOff() && team[0].score == team[1].score
                && (playMode == PlayMode.Initial && period == Period.PenaltyShootout && !timeOutActive[0] && !timeOutActive[1]
                || playMode == PlayMode.Finished && !firstHalf)) {
            return getRemainingSeconds(whenCurrentPlayModeBegan, game.settings().pausePenaltyShootOutTime);
        } else {
            return null;
        }
    }
    
    /**
     * Resets the penalize time of all players to 0.
     * This does not unpenalize them.
     */
    public void resetPenaltyTimes()
    {
        for (long[] players : whenPenalized) {
            for (int i = 0; i < players.length; ++i) {
                players[i] = 0;
            }
        }
    }
    
    /**
     * Resets all penalties.
     */
    public void resetPenalties()
    {
        for (int i = 0; i < team.length; ++i) {
            pushes[i] = 0;
            for (int j = 0; j < game.settings().teamSize; j++) {
                if (!ActionBoard.robotButton[i][j].isCoach() && team[i].player[j].penalty != Penalty.Substitute) {
                    team[i].player[j].penalty = Penalty.None;
                    ejected[i][j] = false;
                }
            }
        }
        resetPenaltyTimes();
        for (int i = 0; i < 2; i++) {
            penaltyQueueForSubPlayers.get(i).clear();
        }
    }
    
    /**
     * Calculates the remaining time a certain robot has to stay penalized.
     * @param side 0 or 1 depending on whether the robot's team is shown left or right.
     * @param number The robot's number starting with 0.
     * @return The number of seconds the robot has to stay penalized.
     */
    public int getRemainingPenaltyTime(int side, int number)
    {
        Penalty penalty = team[side].player[number].penalty;
        assert penalty == Penalty.Manual || penalty == Penalty.Substitute || penalty.getDurationSeconds() != -1;
        return penalty == Penalty.Manual || penalty == Penalty.Substitute ? 0
                : playMode == PlayMode.Ready && game.settings().returnRobotsInGameStoppages && whenPenalized[side][number] >= whenCurrentPlayModeBegan
                ? game.settings().readyTime - getSecondsSince(whenCurrentPlayModeBegan)
                : Math.max(0, getRemainingSeconds(whenPenalized[side][number], penalty.getDurationSeconds()));
    }
    
    /**
     * Calculates the Number of robots in play (not substitute) on one side
     * @param side 0 or 1 depending on whether the team is shown left or right.
     * @return The number of robots without substitute penalty on the side
     */
    public int getNumberOfRobotsInPlay(int side)
    {
        int count = 0;
        for (int i=0; i<team[side].player.length; i++) {
            if (team[side].player[i].penalty != Penalty.Substitute) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Determines the secondary time. Although this is a GUI feature, the secondary time
     * will also be encoded in the network packet.
     * @param timeKickOffBlockedOvertime In case the kickOffBlocked time is delivered, this
     *                                   parameter specified how long negative values will
     *                                   be returned before the time is switched off.
     * @return The secondary time in seconds or null if there currently is none.
     */
    public Integer getSecondaryTime(int timeKickOffBlockedOvertime)
    {
        int timeKickOffBlocked = getRemainingSeconds(whenCurrentPlayModeBegan, game.settings().kickoffTime);
        if (nextKickOffColor == null) {
            timeKickOffBlocked = 0;
        }
        if (playMode == PlayMode.Initial && (timeOutActive[0] || timeOutActive[1])) {
            return getRemainingSeconds(whenCurrentPlayModeBegan, game.settings().timeOutTime);
        }
        else if (playMode == PlayMode.Initial && refereeTimeout) {
            return getRemainingSeconds(whenCurrentPlayModeBegan, game.settings().refereeTimeout);
        }
        else if (playMode == PlayMode.Ready) {
            return getRemainingSeconds(whenCurrentPlayModeBegan, game.settings().readyTime);
        } else if (playMode == PlayMode.Playing && period != Period.PenaltyShootout
                && timeKickOffBlocked >= -timeKickOffBlockedOvertime) {
            if (timeKickOffBlocked > 0) {
                return timeKickOffBlocked;
            } else {
                return null;
            }
        } else {
            return getRemainingPauseTime();
        }
    }

    /**
     * Dispatch the coach messages. Since coach messages are texts, the messages are zeroed
     * after the first zero character, to avoid the transport of information the
     * GameStateVisualizer would not show.
     */
    public void updateCoachMessages()
    {
        int i = 0;
        while (i < splCoachMessageQueue.size()) {
            if (splCoachMessageQueue.get(i).getRemainingTimeToSend() == 0) {
                int j = getTeamIndex(splCoachMessageQueue.get(i).teamNumber);
                if (j != -1) {
                    byte[] message = splCoachMessageQueue.get(i).message;

                    // All chars after the first zero are zeroed, too
                    int k = 0;
                    while (k < message.length && message[k] != 0) {
                        k++;
                    }
                    while (k < message.length) {
                        message[k++] = 0;
                    }

                    team[j].coachMessage = message;
                    Log.toFile("Coach Message Team " + team[j].teamColor + " " + new String(message));
                    splCoachMessageQueue.remove(i);
                    break;
                }
            } else {
                i++;
            }
        }
    }

    public class PenaltyQueueData implements Serializable
    {
        public final long whenPenalized;
        public final Penalty penalty;

        public PenaltyQueueData(long whenPenalized, Penalty penalty)
        {
            this.whenPenalized = whenPenalized;
            this.penalty = penalty;
        }
    }

    public void addToPenaltyQueue(int side, long whenPenalized, Penalty penalty)
    {
        penaltyQueueForSubPlayers.get(side).add(new PenaltyQueueData(whenPenalized, penalty));
    }
}
