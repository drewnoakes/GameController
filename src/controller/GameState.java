package controller;

import common.annotations.NotNull;
import common.annotations.Nullable;
import data.*;
import leagues.LeagueRules;

/**
 * Models the complete state of a game at a given moment in time.
 *
 * <ul>
 *     <li>Implements both read-only and writeable interfaces.</li>
 *     <li>{@link GameState#clone()} produces an exact copy. This mechanism is used to enable 'undo' functionality.</li>
 * </ul>
 *
 * Game state modifications are controlled by {@link controller.Game}, and
 * carried out by instances of {@link controller.Action} via their
 * {@link controller.Action#execute} method. All other consumers of this
 * object should receive it via the {@link ReadOnlyGameState} interface.
 *
 * @author Michel Bartsch
 * @author Drew Noakes https://drewnoakes.com
 */
public class GameState implements WriteableGameState, ReadOnlyGameState
{
    @NotNull private final Game game;
    @NotNull private final ReadOnlyPair<TeamState> teams;

    // NOTE if a new field is added here, be sure to include it in the copy constructor

    private long timeBeforeCurrentPlayMode;
    private long whenCurrentPlayModeBegan;
    private long whenDropIn;
    private boolean isRefereeTimeoutActive;
    private boolean isTestMode;
    private boolean manPause;
    private boolean manPlay;
    private long manWhenClockChanged;
    private long manTimeOffset;
    private long manRemainingGameTimeOffset;
    @NotNull private Period previousPeriod;
    @NotNull private PlayMode playMode;
    private boolean isFirstHalf;
    @Nullable private TeamColor nextKickOffColor;
    @NotNull private Period period;
    @Nullable private TeamColor lastDropInColor;

    /** Initialises a new GameState instance for a particular game. */
    public GameState(@NotNull Game game)
    {
        this.game = game;

        this.teams = new Pair<TeamState>(
                game.uiOrientation(),
                new TeamState(game, game.teams().get(UISide.Left), game.uiOrientation().getColor(UISide.Left)),
                new TeamState(game, game.teams().get(UISide.Right), game.uiOrientation().getColor(UISide.Right)));

        for (TeamState team : teams) {
            // Set extra players as substitutes
            for (int uniformNumber = game.rules().getRobotsPlaying() + 1; uniformNumber <= team.getPlayerCount(); uniformNumber++)
                team.getPlayer(uniformNumber).setPenalty(Penalty.Substitute);
        }

        playMode = PlayMode.Initial;
        isFirstHalf = true;
        nextKickOffColor = TeamColor.Blue;
        isRefereeTimeoutActive = false;
        isTestMode = false;
        manPause = false;
        manPlay = false;
        period = game.rules().isGameStartedWithPenaltyShots() ? Period.PenaltyShootout : Period.Normal;
        previousPeriod = Period.Normal;
    }

    /** Private copy constructor. */
    private GameState(@NotNull GameState source)
    {
        // Note, we don't deep clone the game or coach message queues
        game = source.game;

        // Deep clone the team states
        teams = new Pair<TeamState>(
                source.game.uiOrientation(),
                source.teams.get(TeamColor.Blue).clone(),
                source.teams.get(TeamColor.Red).clone());

        // Make copies of most field values
        timeBeforeCurrentPlayMode = source.timeBeforeCurrentPlayMode;
        whenCurrentPlayModeBegan = source.whenCurrentPlayModeBegan;
        whenDropIn = source.whenDropIn;
        isRefereeTimeoutActive = source.isRefereeTimeoutActive;
        isTestMode = source.isTestMode;
        manPause = source.manPause;
        manPlay = source.manPlay;
        manWhenClockChanged = source.manWhenClockChanged;
        manTimeOffset = source.manTimeOffset;
        manRemainingGameTimeOffset = source.manRemainingGameTimeOffset;
        previousPeriod = source.previousPeriod;
        playMode = source.playMode;
        isFirstHalf = source.isFirstHalf;
        nextKickOffColor = source.nextKickOffColor;
        period = source.period;
        lastDropInColor = source.lastDropInColor;
    }

    @NotNull
    public GameState clone()
    {
        return new GameState(this);
    }

    @Nullable
    @Override
    public WriteableTeamState getTeam(int teamNumber)
    {
        TeamState left = teams.get(UISide.Left);
        if (left.getTeamNumber() == teamNumber)
            return left;

        TeamState right = teams.get(UISide.Left);
        if (right.getTeamNumber() == teamNumber)
            return right;

        return null;
    }

    @NotNull
    @Override
    public WriteableTeamState getTeam(@NotNull TeamColor teamColor)
    {
        return teams.get(teamColor);
    }

    @NotNull
    @Override
    public WriteableTeamState getTeam(@NotNull UISide side)
    {
        return teams.get(side);
    }

    //////////////////////// CLOCK

    @Override
    public long getTime()
    {
        return isManPause() ? getManWhenClockChanged() : System.currentTimeMillis() + getManTimeOffset();
    }

    @Override
    public int getSecondsSince(long millis)
    {
        return millis == 0 ? 100000 : (int) (getTime() - millis) / 1000;
    }
    
    @Override
    public int getRemainingSeconds(long millis, int durationInSeconds)
    {
        return durationInSeconds - getSecondsSince(millis);
    }

    ////////////////////////
    
    @Override
    public void addTimeInCurrentPlayMode()
    {
        setTimeBeforeCurrentPlayMode(getTimeBeforeCurrentPlayMode() + getTime() - getWhenCurrentPlayModeBegan());
    }

    @Override
    public int getSecsRemaining()
    {
        int regularNumberOfPenaltyShots = game.isPlayOff()
                ? game.rules().getNumberOfPenaltyShotsInPlayOffGame()
                : game.rules().getNumberOfPenaltyShotsInNormalGame();

        int duration = is(Period.Timeout)
                ? getSecsRemaining() // TODO fix this infinite recursion bug -- should return last computed value (or something else)
                : is(Period.Normal)
                    ? game.rules().getNormalHalfDurationSeconds()
                    : is(Period.Overtime)
                        ? game.rules().getOvertimeHalfDurationSeconds()
                        : Math.max(teams.get(UISide.Left).getPenaltyShotCount(), teams.get(UISide.Right).getPenaltyShotCount()) > regularNumberOfPenaltyShots
                            ? game.rules().getPenaltyShotDurationSecondsInSuddenDeath()
                            : game.rules().getPenaltyShotDurationSeconds();

        int timePlayed = playMode == PlayMode.Initial// during timeouts
                || (playMode == PlayMode.Ready || playMode == PlayMode.Set)
                && (game.isPlayOff() && game.rules().isPlayOffTimeStop() || getTimeBeforeCurrentPlayMode() == 0)
                || playMode == PlayMode.Finished
            ? (int) ((getTimeBeforeCurrentPlayMode() + getManRemainingGameTimeOffset() + (isManPlay() ? System.currentTimeMillis() - getManWhenClockChanged() : 0)) / 1000)
            : getSecondsSince(getWhenCurrentPlayModeBegan() - getTimeBeforeCurrentPlayMode() - getManRemainingGameTimeOffset());

        return duration - timePlayed;
    }

    @Override
    public Integer getRemainingPauseTime()
    {
        if (period == Period.Normal
                && (playMode == PlayMode.Initial && !isFirstHalf && !isTimeoutActive() || playMode == PlayMode.Finished && isFirstHalf)) {
            return getRemainingSeconds(getWhenCurrentPlayModeBegan(), game.rules().getHalfTimeDurationSeconds());
        }

        if (game.rules().getDurationBeforePenaltyShootoutsStart() != 0 && game.isPlayOff() && areScoresLevel()
                && (playMode == PlayMode.Initial && period == Period.PenaltyShootout && !isTimeoutActive()
                || playMode == PlayMode.Finished && !isFirstHalf)) {
            return getRemainingSeconds(getWhenCurrentPlayModeBegan(), game.rules().getDurationBeforePenaltyShootoutsStart());
        }

        return null;
    }

    @Override
    public void setRemainingPenaltyTimesToZero()
    {
        for (TeamState team : teams) {
            for (WriteablePlayerState player : team)
                player.setWhenPenalized(0);
        }
    }
    
    @Override
    public void resetPenalties()
    {
        for (TeamState team : teams) {
            team.setPushCount(0);
            for (WriteablePlayerState player : team) {
                if (!player.isCoach() && player.getPenalty() != Penalty.Substitute) {
                    player.setPenalty(Penalty.None);
                    player.setEjected(false);
                }
            }
            team.clearPenaltyQueue();
        }

        setRemainingPenaltyTimesToZero();
    }
    
    @Override
    public int getRemainingPenaltyTime(@NotNull ReadOnlyPlayerState player)
    {
        Penalty penalty = player.getPenalty();

        assert(penalty != Penalty.None);

        // Manual/Substitute penalties do not have their durations tracked by the game controller
        if (penalty == Penalty.Manual || penalty == Penalty.Substitute)
            return 0;

        assert(penalty.getDurationSeconds() != -1);

        LeagueRules rules = game.rules();

        // TODO test this -- seems strange that the penalty should have to start after the current play mode began, when the current play mode is 'ready'
        if (rules.arePenaltiesClearedDuringStoppages() && is(PlayMode.Ready) && player.getWhenPenalized() >= getWhenCurrentPlayModeBegan())
            return rules.getReadyPlayModeDurationSeconds() - getSecondsSince(getWhenCurrentPlayModeBegan());

        return Math.max(0, getRemainingSeconds(player.getWhenPenalized(), penalty.getDurationSeconds()));
    }
    
    @Override
    public Integer getSecondaryTime(int timeKickOffBlockedOvertime)
    {
        int timeKickOffBlocked = getNextKickOffColor() != null
                ? getRemainingSeconds(getWhenCurrentPlayModeBegan(), game.rules().getKickOffDurationSeconds())
                : 0;

        if (is(PlayMode.Initial) && isTimeoutActive()) {
            return getRemainingSeconds(getWhenCurrentPlayModeBegan(), game.rules().getTimeoutDurationSeconds());
        }

        if (is(PlayMode.Initial) && isRefereeTimeoutActive()) {
            return getRemainingSeconds(getWhenCurrentPlayModeBegan(), game.rules().getRefereeTimeoutDurationSeconds());
        }

        if (is(PlayMode.Ready)) {
            return getRemainingSeconds(getWhenCurrentPlayModeBegan(), game.rules().getReadyPlayModeDurationSeconds());
        }

        if (is(PlayMode.Playing) && getPeriod() != Period.PenaltyShootout && timeKickOffBlocked >= -timeKickOffBlockedOvertime) {
            return timeKickOffBlocked > 0 ? timeKickOffBlocked : null;
        }

        return getRemainingPauseTime();
    }

    @Override
    public int getSecondaryTime()
    {
        Integer secondaryTime = getSecondaryTime(0);
        return secondaryTime == null ? 0 : secondaryTime;
    }

    @Override
    public long getTimeBeforeCurrentPlayMode()
    {
        return timeBeforeCurrentPlayMode;
    }

    @Override
    public void setTimeBeforeCurrentPlayMode(long timeBeforeCurrentPlayMode)
    {
        this.timeBeforeCurrentPlayMode = timeBeforeCurrentPlayMode;
    }

    @Override
    public long getWhenCurrentPlayModeBegan()
    {
        return whenCurrentPlayModeBegan;
    }

    @Override
    public void setWhenCurrentPlayModeBegan(long whenCurrentPlayModeBegan)
    {
        this.whenCurrentPlayModeBegan = whenCurrentPlayModeBegan;
    }

    @Override
    public long getWhenDropIn()
    {
        return whenDropIn;
    }

    @Override
    public void setWhenDropIn(long whenDropIn)
    {
        this.whenDropIn = whenDropIn;
    }

    @Override
    public boolean isTimeoutActive()
    {
        return getTeam(UISide.Left).isTimeoutActive() ||
               getTeam(UISide.Right).isTimeoutActive();
    }

    @Override
    public boolean isRefereeTimeoutActive()
    {
        return isRefereeTimeoutActive;
    }

    @Override
    public void setRefereeTimeoutActive(boolean isActive)
    {
        isRefereeTimeoutActive = isActive;
    }

    @Override
    public boolean areScoresLevel()
    {
        return teams.get(UISide.Left).getScore() == teams.get(UISide.Right).getScore();
    }

    @Override
    public boolean isTestMode()
    {
        return isTestMode;
    }

    @Override
    public void setTestMode(boolean testmode)
    {
        this.isTestMode = testmode;
    }

    @Override
    public boolean isManPause()
    {
        return manPause;
    }

    @Override
    public void setManPause(boolean manPause)
    {
        this.manPause = manPause;
    }

    @Override
    public boolean isManPlay()
    {
        return manPlay;
    }

    @Override
    public void setManPlay(boolean manPlay)
    {
        this.manPlay = manPlay;
    }

    @Override
    public long getManWhenClockChanged()
    {
        return manWhenClockChanged;
    }

    @Override
    public void setManWhenClockChanged(long manWhenClockChanged)
    {
        this.manWhenClockChanged = manWhenClockChanged;
    }

    @Override
    public long getManTimeOffset()
    {
        return manTimeOffset;
    }

    @Override
    public void setManTimeOffset(long manTimeOffset)
    {
        this.manTimeOffset = manTimeOffset;
    }

    @Override
    public long getManRemainingGameTimeOffset()
    {
        return manRemainingGameTimeOffset;
    }

    @Override
    public void setManRemainingGameTimeOffset(long manRemainingGameTimeOffset)
    {
        this.manRemainingGameTimeOffset = manRemainingGameTimeOffset;
    }

    @Override
    @NotNull
    public Period getPreviousPeriod()
    {
        return previousPeriod;
    }

    @Override
    public void setPreviousPeriod(@NotNull Period previousPeriod)
    {
        this.previousPeriod = previousPeriod;
    }

    @Override
    @NotNull
    public PlayMode getPlayMode()
    {
        return playMode;
    }

    @Override
    public boolean is(@NotNull PlayMode... playModes)
    {
        for (PlayMode p : playModes)
            if (p == playMode)
                return true;
        return false;
    }

    @Override
    public void setPlayMode(@NotNull PlayMode playMode)
    {
        this.playMode = playMode;
    }

    @Override
    public boolean isFirstHalf()
    {
        return isFirstHalf;
    }

    @Override
    public void setFirstHalf(boolean firstHalf)
    {
        this.isFirstHalf = firstHalf;
    }

    @Override
    @Nullable
    public TeamColor getNextKickOffColor()
    {
        return nextKickOffColor;
    }

    @Override
    public void setNextKickOffColor(@Nullable TeamColor nextKickOffColor)
    {
        this.nextKickOffColor = nextKickOffColor;
    }

    @Override
    @NotNull
    public Period getPeriod()
    {
        return period;
    }

    @Override
    public void setPeriod(@NotNull Period period)
    {
        this.period = period;
    }

    @Override
    public boolean is(@NotNull Period... periods)
    {
        for (Period p : periods)
            if (p == period)
                return true;
        return false;
    }

    @Override
    @Nullable
    public TeamColor getLastDropInColor()
    {
        return lastDropInColor;
    }

    @Override
    public void setLastDropInColor(@Nullable TeamColor lastDropInColor)
    {
        this.lastDropInColor = lastDropInColor;
    }

    @Override
    public int getDropInTime()
    {
        return getWhenDropIn() == 0 ? -1 : getSecondsSince(getWhenDropIn());
    }

    @Override
    public int getGameId()
    {
        return game.gameId();
    }

    @Override
    public boolean isPlayOff()
    {
        return game.isPlayOff();
    }
}
