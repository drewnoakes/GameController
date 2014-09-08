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
    @NotNull WriteableTeamState getTeam(TeamColor teamColor);
    @NotNull WriteableTeamState getTeam(UISide side);

    /////

    /**
     * Add the time passed in the current play mode to the time that already passed before.
     * Is usually called during changes of the play mode.
     */
    void addTimeInCurrentPlayMode();

    void setTimeBeforeCurrentPlayMode(long timeBeforeCurrentPlayMode);

    void setWhenCurrentPlayModeBegan(long whenCurrentPlayModeBegan);

    /** Resets the penalized-at-time of all players to 0. Does not unpenalize them. */
    void resetPenaltyTimes();

    /////

    /**
     * Resets all penalties.
     */
    void resetPenalties();

    /**
     * Dispatch the coach messages. Since coach messages are texts, the messages are zeroed
     * after the first zero character, to avoid the transport of information the
     * GameStateVisualizer would not show.
     */
    void updateCoachMessages();

    void setWhenDropIn(long whenDropIn);

    void setRefereeTimeout(boolean refereeTimeout);

    void setLeftSideKickoff(boolean leftSideKickoff);

    void setTestMode(boolean testmode);

    void setManPause(boolean manPause);

    void setManPlay(boolean manPlay);

    void setManWhenClockChanged(long manWhenClockChanged);

    void setManTimeOffset(long manTimeOffset);

    void setManRemainingGameTimeOffset(long manRemainingGameTimeOffset);

    void setPreviousPeriod(Period previousPeriod);

    void setPlayMode(@NotNull PlayMode playMode);

    void setFirstHalf(boolean firstHalf);

    void setNextKickOffColor(@Nullable TeamColor nextKickOffColor);

    void setPeriod(@NotNull Period period);

    void setLastDropInColor(@Nullable TeamColor lastDropInColor);

    void enqueueSplCoachMessage(@NotNull SPLCoachMessage message);
}
