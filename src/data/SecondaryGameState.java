package data;

/**
 * Enum of secondary game states.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public enum SecondaryGameState
{
    Normal((byte)0),
    PenaltyShootout((byte)1),
    Overtime((byte)2),
    Timeout((byte)3);

    private final byte value;

    SecondaryGameState(byte value)
    {
        this.value = value;
    }

    public byte getValue()
    {
        return value;
    }

    public static SecondaryGameState fromValue(byte value)
    {
        switch (value)
        {
            case 0: return Normal;
            case 1: return PenaltyShootout;
            case 2: return Overtime;
            case 3: return Timeout;
            default:
                throw new AssertionError("Invalid SecondaryGameState enum value: " + value);
        }
    }
}
