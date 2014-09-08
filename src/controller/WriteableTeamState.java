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

    void setPushCount(int pushCount);

    void setTimeOutActive(boolean isTimeOutActive);

    void setTimeOutTaken(boolean taken);

    void setScore(int score);

    void setPenaltyShotCount(int penaltyShotCount);

    void addPenaltyGoal();

    void setTimestampCoachMessage(long timestampCoachMessage);

    @Nullable
    TeamState.QueuedPenalty popQueuedPenalty();

    void enqueuePenalty(long whenPenalized, Penalty penalty);

    void setCoachMessage(byte[] messageBytes);
}
