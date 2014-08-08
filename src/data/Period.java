package data;

/**
 * Enum of game periods. Includes normal, overtime, timeouts and penalties.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public enum Period
{
    /** A normal period of play. Includes two halves. */
    Normal((byte)0),

    /** The game is in penalty shoot outs. */
    PenaltyShootout((byte)1),

    /**
     * Overtime periods which follow normal periods that conclude with
     * a draw, in matches where a winner must be decided.
     */
    Overtime((byte)2),

    /** The game suspended temporarily in a timeout. */
    Timeout((byte)3);

    private final byte value;

    Period(byte value)
    {
        this.value = value;
    }

    public byte getValue()
    {
        return value;
    }

    public static Period fromValue(byte value)
    {
        switch (value)
        {
            case 0: return Normal;
            case 1: return PenaltyShootout;
            case 2: return Overtime;
            case 3: return Timeout;
            default:
                throw new AssertionError("Invalid Period enum value: " + value);
        }
    }
}
