package controller;

import common.annotations.NotNull;
import common.annotations.Nullable;
import data.Penalty;
import data.TeamColor;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public interface WriteableTeamState extends ReadOnlyTeamState
{
    void setTeamColor(TeamColor color);

    @NotNull
    WriteablePlayerState getPlayer(int uniformNumber);

    void setScore(int score);

    ////////////////////////// TIMEOUTS

    void setTimeOutActive(boolean isTimeOutActive);

    void setTimeOutTaken(boolean taken);

    ////////////////////////// PENALTY SHOOTOUTS

    void setPenaltyShotCount(int penaltyShotCount);

    void addPenaltyGoal();

    ////////////////////////// PLAYER PENALTIES

    void enqueuePenalty(long whenPenalized, Penalty penalty);

    @Nullable
    TeamState.QueuedPenalty popQueuedPenalty();

    ////////////////////////// SPL-SPECIFIC VALUES

    void setTimestampCoachMessage(long timestampCoachMessage);

    void setCoachMessage(@NotNull byte[] messageBytes);
    void setPushCount(int pushCount);
}
