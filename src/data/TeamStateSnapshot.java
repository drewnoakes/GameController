package data;

import common.annotations.NotNull;
import common.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Immutable model of a team's state, as received from a Game Controller.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class TeamStateSnapshot
{
    private final int teamNumber;
    @NotNull private final TeamColor teamColor;
    private final byte score;
    private final byte penaltyShotCount;
    private final short penaltyShotFlags;
    @NotNull private final List<PlayerStateSnapshot> players;
    @Nullable private final byte[] coachMessage;
    @Nullable private final PlayerStateSnapshot coach;

    public TeamStateSnapshot(int teamNumber, @NotNull TeamColor teamColor, byte score,
                             byte penaltyShotCount, short penaltyShotFlags, @NotNull List<PlayerStateSnapshot> players,
                             @Nullable byte[] coachMessage, @Nullable PlayerStateSnapshot coach)
    {
        this.teamNumber = teamNumber;
        this.teamColor = teamColor;
        this.score = score;
        this.penaltyShotCount = penaltyShotCount;
        this.penaltyShotFlags = penaltyShotFlags;
        this.players = Collections.unmodifiableList(players);
        this.coachMessage = coachMessage;
        this.coach = coach;
    }

    /** This team's uniquely identifying number. */
    public int getTeamNumber()
    {
        return teamNumber;
    }

    /** This team's uniform colour. */
    @NotNull
    public TeamColor getTeamColor()
    {
        return teamColor;
    }

    /** The team's current score. */
    public byte getScore()
    {
        return score;
    }

    /** The number of penalty shots this team has taken. */
    public byte getPenaltyShotCount()
    {
        return penaltyShotCount;
    }

    /** Bit flag, indicating success of penalty shots so far. */
    public short getPenaltyShotFlags()
    {
        return penaltyShotFlags;
    }

    /** Data about the players in this team. */
    @NotNull
    public List<PlayerStateSnapshot> getPlayers()
    {
        return players;
    }

    /** The last coach message (only used in SPL). */
    @Nullable
    public byte[] getCoachMessage()
    {
        return coachMessage;
    }

    /** Data about the team's coach (only used in SPL). */
    @Nullable
    public PlayerStateSnapshot getCoach()
    {
        return coach;
    }
}
