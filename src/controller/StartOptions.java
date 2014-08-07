package controller;

/** Models options that apply through the entire game and must be specified before the game commences.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class StartOptions
{
    /** Team number of the first (blue) team. */
    public byte teamNumberBlue;

    /** Team number of the second (red) team. */
    public byte teamNumberRed;

    /** Whether teams change colour at half time or not. */
    public boolean colorChangeAuto;

    /**
     * Specifies which team has kick off.
     * 0 for blue, 1 for red, -1 for unspecified.
     */
    public int kickOffTeamIndex;

    /**
     * Whether this is a play off game (goes into extra time if needed.)
     *
     * This is known as a 'play-off' game in SPL, and a 'knock-out' game in the HL.
     */
    public boolean playOff;

    /** The UDP broadcast IP address. */
    public String broadcastAddress;

    /** Whether the application should appear in full screen. */
    public boolean fullScreenMode;
}
