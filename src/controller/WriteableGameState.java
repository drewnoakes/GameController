package controller;

import common.annotations.NotNull;
import common.annotations.Nullable;
import data.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public interface WriteableGameState extends ReadOnlyGameState
{
    @Nullable WriteableTeamState getTeam(int teamNumber);
    @NotNull WriteableTeamState getTeam(@NotNull TeamColor teamColor);
    @NotNull WriteableTeamState getTeam(@NotNull UISide side);

    ////////////////////////// CLOCK & TIMING

    /**
     * Add the time passed in the current play mode to the time that already passed before.
     * Is usually called during changes of the play mode.
     */
    void addTimeInCurrentPlayMode();

    void setTimeBeforeCurrentPlayMode(long timeBeforeCurrentPlayMode);

    void setWhenCurrentPlayModeBegan(long whenCurrentPlayModeBegan);


    void setWhenDropIn(long whenDropIn);

    void setManPause(boolean manPause);

    void setManPlay(boolean manPlay);

    void setManWhenClockChanged(long manWhenClockChanged);

    void setManTimeOffset(long manTimeOffset);

    void setManRemainingGameTimeOffset(long manRemainingGameTimeOffset);

    ////////////////////////// PENALTIES

    /**
     * Resets all penalties.
     */
    void resetPenalties();

    /** Resets the penalized-at-time of all players to 0. Does not unpenalize them. */
    void resetPenaltyTimes();

    ////////////////////////// PLAY MODE, PERIOD & HALF

    void setPlayMode(@NotNull PlayMode playMode);

    void setPeriod(@NotNull Period period);

    void setPreviousPeriod(Period previousPeriod);

    void setFirstHalf(boolean firstHalf);

    ////////////////////////// KICK OFF AND DROP IN

    void setNextKickOffColor(@Nullable TeamColor nextKickOffColor);

    void setLeftSideKickoff(boolean leftSideKickoff);

    void setLastDropInColor(@Nullable TeamColor lastDropInColor);

    ////////////////////////// MISCELLANEOUS

    void setTestMode(boolean testmode);

    ////////////////////////// SPL-SPECIFIC VALUES

    void setRefereeTimeoutActive(boolean refereeTimeout);

    /**
     * Dispatch the coach messages. Since coach messages are texts, the messages are zeroed
     * after the first zero character, to avoid the transport of information the
     * GameStateVisualizer would not show.
     */
    void updateCoachMessages();

    void enqueueSplCoachMessage(@NotNull SPLCoachMessage message);
}
