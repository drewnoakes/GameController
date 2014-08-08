package data;

import controller.net.RobotStatus;

/**
 * The content of a message received from a robot, indicating its identity and advertised status.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class RobotMessage
{
    private final byte teamNumber;
    private final byte playerNumber;
    private final RobotStatus status;

    public RobotMessage(byte teamNumber, byte playerNumber, RobotStatus status)
    {
        this.teamNumber = teamNumber;
        this.playerNumber = playerNumber;
        this.status = status;
    }

    /** The number that uniquely identifies the team in this tournament. */
    public byte getTeamNumber()
    {
        return teamNumber;
    }

    /** The robot's uniform number. */
    public byte getPlayerNumber()
    {
        return playerNumber;
    }

    /** The robot's advertised status. */
    public RobotStatus getStatus()
    {
        return status;
    }
}
