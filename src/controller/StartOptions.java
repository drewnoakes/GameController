package controller;

import data.TeamColor;

/** Models options that apply through the entire game and must be specified before the game commences.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class StartOptions
{
    /** Team number of the first (blue) team (index 0). */
    public byte teamNumberBlue;

    /** Team number of the second (red) team (index 1). */
    public byte teamNumberRed;

    /** Whether teams change colour at half time or not. */
    public boolean colorChangeAuto;

    /**
     * Specifies which team has kick off, or null for unspecified.
     */
    public TeamColor initialKickOffTeam;

    /**
     * Whether this is a play off game (goes into extra time if needed).
     *
     * This is known as a 'play-off' game in SPL, and a 'knock-out' game in the HL.
     *
     * A null value indicates that no value has been specified so far.
     */
    public Boolean playOff;

    /** The UDP broadcast IP address. */
    public String broadcastAddress;

    /** Whether the application should appear in full screen. */
    public boolean fullScreenMode;

    public byte teamNumberByIndex(int index)
    {
        assert(index == 0 || index == 1);

        return index == 0 ? teamNumberBlue : teamNumberRed;
    }

    public void setTeamNumberByIndex(int index, byte teamNumber)
    {
        assert(index == 0 || index == 1);

        if (index == 0)
            teamNumberBlue = teamNumber;
        else
            teamNumberRed = teamNumber;
    }
}
