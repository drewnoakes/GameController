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
    private final byte uniformNumber;
    private final RobotStatus status;

    public RobotMessage(byte teamNumber, byte uniformNumber, RobotStatus status)
    {
        this.teamNumber = teamNumber;
        this.uniformNumber = uniformNumber;
        this.status = status;
    }

    /** The number that uniquely identifies the team in this tournament. */
    public byte getTeamNumber()
    {
        return teamNumber;
    }

    /** The robot's uniform number. */
    public byte getUniformNumber()
    {
        return uniformNumber;
    }

    /** The robot's advertised status. */
    public RobotStatus getStatus()
    {
        return status;
    }
}
