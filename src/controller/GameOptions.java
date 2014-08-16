package controller;

import common.annotations.NotNull;
import common.annotations.Nullable;
import data.*;

/**
 * Models options provided as arguments to the executable.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class GameOptions
{
    /** The selected RoboCup league. */
    @NotNull
    public final League league;

    /** Specifies which team has the first kick off, or <code>null</code> for unspecified. */
    @Nullable
    public final TeamColor initialKickOffColor;

    /**
     * Whether this is a play off game (goes into extra time if needed).
     * <p>
     * This is known as a 'play-off' game in SPL, and a 'knock-out' game in the HL.
     * <p>
     * <code>null</code> indicates that no value has been specified so far.
     */
    @Nullable
    public final Boolean isPlayOff;

    /** The UDP broadcast IP address. */
    @NotNull
    public final String broadcastAddress;

    /** Whether the application should appear in full screen. */
    public final boolean isFullScreen;

    /** Whether the team colours change at the end of each game period. */
    public final boolean changeColoursEachPeriod;

    /** The configuration of teams on the UI. */
    @NotNull
    public final UIOrientation orientation;

    /** The selected pair of teams. */
    @NotNull
    public final Pair<Team> teams;

    public GameOptions(@NotNull String broadcastAddress, boolean isFullScreen, @NotNull League league,
                       @Nullable Boolean isPlayOff, @NotNull UIOrientation orientation,
                       @NotNull Pair<Team> teams, @Nullable TeamColor initialKickOffColor,
                       boolean changeColoursEachPeriod)
    {
        this.broadcastAddress = broadcastAddress;
        this.isFullScreen = isFullScreen;
        this.league = league;
        this.isPlayOff = isPlayOff;
        this.orientation = orientation;
        this.teams = teams;
        this.initialKickOffColor = initialKickOffColor;
        this.changeColoursEachPeriod = changeColoursEachPeriod;
    }
}
