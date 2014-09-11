package data;

import common.annotations.NotNull;
import common.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable model of the game state, as received from a Game Controller.
 *
 * @author Michel Bartsch
 * @author Drew Noakes https://drewnoakes.com
 */
public class GameStateSnapshot
{
    @NotNull private final PlayMode playMode;
    private final boolean firstHalf;
    @Nullable private final TeamColor nextKickOffColor;
    @NotNull private final Period period;
    @Nullable private final TeamColor lastDropInColor;
    private final short dropInTime;
    private final short secsRemaining;
    private final short secondaryTime;
    private final int gameId;
    @NotNull private final League league;
    private final byte packetNumber;
    private final byte playersPerTeam;
    @Nullable private final Boolean isDropInGame;
    @NotNull public final TeamStateSnapshot team1;
    @NotNull public final TeamStateSnapshot team2;

    public GameStateSnapshot(@NotNull PlayMode playMode, boolean firstHalf, @Nullable TeamColor nextKickOffColor,
                             @NotNull Period period, @Nullable TeamColor lastDropInColor, short dropInTime,
                             short secsRemaining, @NotNull TeamStateSnapshot team1, @NotNull TeamStateSnapshot team2,
                             short secondaryTime, int gameId, @NotNull League league, byte packetNumber,
                             byte playersPerTeam, @Nullable Boolean isDropInGame)
    {
        this.playMode = playMode;
        this.firstHalf = firstHalf;
        this.nextKickOffColor = nextKickOffColor;
        this.period = period;
        this.lastDropInColor = lastDropInColor;
        this.dropInTime = dropInTime;
        this.secsRemaining = secsRemaining;
        this.team1 = team1;
        this.team2 = team2;
        this.secondaryTime = secondaryTime;
        this.gameId = gameId;
        this.league = league;
        this.packetNumber = packetNumber;
        this.playersPerTeam = playersPerTeam;
        this.isDropInGame = isDropInGame;
    }

    public Iterable<TeamStateSnapshot> getTeams()
    {
        List<TeamStateSnapshot> teams = new ArrayList<TeamStateSnapshot>();
        teams.add(team1);
        teams.add(team2);
        return teams;
    }

    public TeamStateSnapshot getTeam(TeamColor color)
    {
        return team1.getTeamColor() == color ? team1 : team2;
    }

    /** Data about the first team in the message. */
    @NotNull
    public TeamStateSnapshot getTeam1()
    {
        return team1;
    }

    /** Data about the second team in the message. */
    @NotNull
    public TeamStateSnapshot getTeam2()
    {
        return team2;
    }

    /** Play mode of the game. */
    @NotNull
    public PlayMode getPlayMode()
    {
        return playMode;
    }

    /** Whether the game is currently in the first half. Applies to both normal time and overtime. */
    public boolean isFirstHalf()
    {
        return firstHalf;
    }

    /** Which team has the next kick off. If <code>null</code>, then the next kick off will be a drop ball. */
    @Nullable
    public TeamColor getNextKickOffColor()
    {
        return nextKickOffColor;
    }

    /** The type of active game period (normal, overtime, penalties, timeout). */
    @NotNull
    public Period getPeriod()
    {
        return period;
    }

    /** Color of the team that caused last drop in. If no drop in has occurred yet, will be <code>null</code>. */
    @Nullable
    public TeamColor getLastDropInColor()
    {
        return lastDropInColor;
    }

    /** The number of seconds that have passed since the last drop in. Will be -1 before first drop in. */
    public short getDropInTime()
    {
        return dropInTime;
    }

    /** An estimate of the number of seconds remaining in the current half. */
    public short getSecsRemaining()
    {
        return secsRemaining;
    }

    /**
     * Play-mode-specific sub-time in seconds.
     * <p>
     * For example, may reflect the ten second countdown during kickoff, or the number of seconds
     * remaining during 'ready' play mode, and so forth.
     */
    public short getSecondaryTime()
    {
        return secondaryTime;
    }

    /**
     * A number that uniquely identifies the current game instance.
     * <p>
     * May be used to prevent against problems seen when multiple game controllers are running on the same network.
     */
    public int getGameId()
    {
        return gameId;
    }

    /** Gets the league this message applies to. */
    @NotNull
    public League getLeague()
    {
        return league;
    }

    /** Gets a 8-bit sequence number for the message received from the game controller. */
    public byte getPacketNumber()
    {
        return packetNumber;
    }

    /** Gets the number of player states per team included in this message. */
    public byte getPlayersPerTeam()
    {
        return playersPerTeam;
    }

    /**
     * Gets whether this game is a drop-in (SPL) or play-off (HL) game.
     * <p>
     * Only supported in version 9 of the protocol. In snapshots received via earlier protocols, this value
     * will be <code>null</code>.
     */
    @Nullable
    public Boolean isDropInGame()
    {
        return isDropInGame;
    }
}
