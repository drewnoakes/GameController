package controller;

import common.annotations.NotNull;
import common.annotations.Nullable;
import data.*;

import java.util.*;

/**
 * Models the state of a team at a given moment.
 *
 * @author Michel Bartsch
 * @author Drew Noakes https://drewnoakes.com
 */
public class TeamState implements WriteableTeamState, ReadOnlyTeamState, Iterable<PlayerState>
{
    // NOTE if a new field is added here, be sure to include it in the copy constructor

    /** Immutable metadata about the team. */
    @NotNull private final Team team;
    @NotNull private TeamColor teamColor;
    /** The list of players in this team. Does not include the coach. */
    @NotNull private final List<PlayerState> players;
    /** Data about the team's coach (only used in SPL). */
    @Nullable private final PlayerState coach;
    /** Keeps the penalties for the players if there are substituted. */
    private final Queue<QueuedPenalty> penaltyQueue;

    private int score;
    private int penaltyShotCount;
    private short penaltyShotFlags;
    private int pushCount;
    private boolean isTimeOutActive;
    private boolean isTimeOutTaken;

    // TODO introduce a CoachMessage class
    /** The last coach message (only used in SPL). */
    @Nullable private byte[] coachMessage;
    private long timestampCoachMessage;

    public TeamState(@NotNull Game game, @NotNull Team team, @NotNull TeamColor teamColor)
    {
        this.team = team;
        this.teamColor = teamColor;

        this.penaltyQueue = new ArrayDeque<QueuedPenalty>();

        League league = game.league();

        List<PlayerState> players = new ArrayList<PlayerState>(league.settings().teamSize);

        for (int uniformNumber = 1; uniformNumber <= league.settings().teamSize; uniformNumber++) {
            players.add(new PlayerState(game, uniformNumber, false));
        }

        this.players = Collections.unmodifiableList(players);

        if (league.isSPLFamily()) {
            coach = new PlayerState(game, -1, true);
            coachMessage = new byte[SPLCoachMessage.SIZE];
        } else {
            coach = null;
        }
    }

    private TeamState(@NotNull TeamState source)
    {
        // Note, we don't deep clone the immutable team metadata object
        team = source.team;

        // Deep clone the player states
        players = new ArrayList<PlayerState>();
        for (PlayerState player : source.players)
            players.add(player.clone());

        // Shallow clone the penalty queue (its items are immutable)
        penaltyQueue = new ArrayDeque<QueuedPenalty>(source.penaltyQueue);

        // Make copies of most field values
        coach = source.coach;
        teamColor = source.teamColor;
        score = source.score;
        penaltyShotCount = source.penaltyShotCount;
        penaltyShotFlags = source.penaltyShotFlags;
        coachMessage = source.coachMessage;
        timestampCoachMessage = source.timestampCoachMessage;
        pushCount = source.pushCount;
        isTimeOutActive = source.isTimeOutActive;
        isTimeOutTaken = source.isTimeOutTaken;
    }

    @Override
    public TeamState clone()
    {
        return new TeamState(this);
    }

    @Override
    public int getTeamNumber()
    {
        return team.getNumber();
    }

    @NotNull
    @Override
    public String getTeamName()
    {
        return team.getName();
    }

    @NotNull
    @Override
    public TeamColor getTeamColor()
    {
        return teamColor;
    }

    @Override
    public void setTeamColor(@NotNull TeamColor color)
    {
        teamColor = color;
    }

    @Override
    @NotNull
    public WriteablePlayerState getPlayer(int uniformNumber)
    {
        if (uniformNumber < 1 || uniformNumber > getPlayerCount())
            throw new IllegalArgumentException("Invalid player uniform number: " + uniformNumber);

        return players.get(uniformNumber - 1);
    }

    @Override
    public int getPlayerCount()
    {
        return players.size();
    }

    /**
     * Gets the number of non-substitute robots in the team.
     *
     * @return the number of robots without substitute penalty on the side
     */
    @Override
    public int getNumberOfRobotsInPlay()
    {
        // TODO shouldn't this always be equal to the number defined in the league config?
        int count = 0;

        for (PlayerState player : players)
            if (player.getPenalty() != Penalty.Substitute)
                count++;

        return count;
    }

    ////////////////////////// TIMEOUTS //////////////////////////

    @Override
    public boolean isTimeOutActive()
    {
        return isTimeOutActive;
    }

    @Override
    public void setTimeOutActive(boolean isTimeOutActive)
    {
        this.isTimeOutActive = isTimeOutActive;
    }

    @Override
    public boolean isTimeOutTaken()
    {
        return isTimeOutTaken;
    }

    @Override
    public void setTimeOutTaken(boolean taken)
    {
        this.isTimeOutTaken = taken;
    }

    ////////////////////////// SCORE //////////////////////////

    @Override
    public int getScore()
    {
        return score;
    }

    @Override
    public void setScore(int score)
    {
        this.score = score;
    }

    ////////////////////////// PENALTIES //////////////////////////

    @Override
    public int getPenaltyShotCount()
    {
        return penaltyShotCount;
    }

    @Override
    public void setPenaltyShotCount(int penaltyShotCount)
    {
        this.penaltyShotCount = penaltyShotCount;
    }

    @Override
    public void addPenaltyGoal()
    {
        assert(penaltyShotCount > 0);
        penaltyShotFlags |= 1 << (penaltyShotCount - 1);
    }

    @Override
    public boolean getPenaltyResult(int attemptIndex)
    {
        if (attemptIndex < 0 || attemptIndex >= getPenaltyShotCount())
            throw new IllegalArgumentException("Attempt index must be zero or greater, and less than the penalty shot count.");

        return ((1 << attemptIndex) & penaltyShotFlags) != 0;
    }

    @Override
    public short getPenaltyShotFlags()
    {
        return penaltyShotFlags;
    }

    ////////////////////////// SPL COACH //////////////////////////

    /** The timestamp of when coach message was received. */
    @Override
    public long getTimestampCoachMessage()
    {
        return timestampCoachMessage;
    }

    @Override
    public void setTimestampCoachMessage(long timestampCoachMessage)
    {
        this.timestampCoachMessage = timestampCoachMessage;
    }

    @Override
    @NotNull
    public PlayerState getCoach()
    {
        if (coach == null)
            throw new AssertionError("Coach is only used in the SPL.");

        return coach;
    }

    @Override
    @Nullable
    public byte[] getCoachMessage()
    {
        return coachMessage;
    }

    @Override
    public void setCoachMessage(@NotNull byte[] messageBytes)
    {
        this.coachMessage = messageBytes;
    }

    ////////////////////////// SPL PUSHING //////////////////////////

    @Override
    public int getPushCount()
    {
        return pushCount;
    }

    @Override
    public void setPushCount(int pushCount)
    {
        this.pushCount = pushCount;
    }

    ////////////////////////// PENALTY QUEUE DATA //////////////////////////

    @Override
    @Nullable
    public QueuedPenalty popQueuedPenalty()
    {
        // Removes and returns the head of the queue, or returns null if the queue is empty
        return penaltyQueue.poll();
    }

    public void clearPenaltyQueue()
    {
        penaltyQueue.clear();
    }

    @Override
    public void enqueuePenalty(long whenPenalized, Penalty penalty)
    {
        penaltyQueue.add(new QueuedPenalty(whenPenalized, penalty));
    }

    /** Models state about a penalty which has been queued for transfer between players. */
    public class QueuedPenalty
    {
        private final long whenPenalized;
        private final Penalty penalty;

        public QueuedPenalty(long whenPenalized, Penalty penalty)
        {
            this.whenPenalized = whenPenalized;
            this.penalty = penalty;
        }

        public long getWhenPenalized()
        {
            return whenPenalized;
        }

        public Penalty getPenalty()
        {
            return penalty;
        }
    }

    ////////////////////////// PLAYER ITERATION //////////////////////////

    // Note that Java is not covariant/contravariant across generic interfaces.
    // so it is not possible to put this player iteration on ReadableTeamState and
    // WriteableTeamState in a consistent fashion.

    @Override
    public Iterator<PlayerState> iterator()
    {
        return players.iterator();
    }
}
