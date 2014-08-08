package controller.net;

/**
 * Enum of robot statuses, as advertised by the robots themselves.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public enum RobotStatus
{
    ManuallyPenalised((byte)0),

    ManuallyUnpenalised((byte)1),

    Alive((byte)2);

    private final byte value;

    RobotStatus(byte value)
    {
        this.value = value;
    }

    /** Get the numeric value used in network messages for this RobotStatus. */
    public byte getValue()
    {
        return value;
    }

    /** Decode a numeric value from a network message. */
    public static RobotStatus fromValue(byte value)
    {
        switch (value)
        {
            case 0: return ManuallyPenalised;
            case 1: return ManuallyUnpenalised;
            case 2: return Alive;
            default:
                throw new AssertionError("Invalid RobotStatus enum value: " + value);
        }
    }
}
