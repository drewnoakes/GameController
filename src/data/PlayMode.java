package data;

/**
 * Enum of game play modes.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public enum PlayMode
{
    /**
     * The game is in 'initial' play mode.
     * We are waiting for the referee to call 'ready'.
     */
    Initial((byte)0),

    /**
     * The game is in 'ready' play mode.
     * Robots may position automatically, or be positioned manually.
     * The referee will call 'set' soon.
     */
    Ready((byte)1),

    /**
     * The game is in 'set' play mode.
     * Robots must stop moving, and robot handlers must leave the field.
     * The referee will call 'play' soon.
     */
    Set((byte)2),

    /** The game is in 'playing' play mode. */
    Playing((byte)3),

    /**
     * The game is in 'finished' play mode as a half has completed.
     * There may yet be another half, extra time or penalties.
     */
    Finished((byte)4);

    private final byte value;

    PlayMode(byte value)
    {
        this.value = value;
    }

    /** Get the numeric value used in network messages for this play mode. */
    public byte getValue()
    {
        return value;
    }

    /** Decode a numeric value from a network message. */
    public static PlayMode fromValue(byte value)
    {
        switch (value)
        {
            case 0: return Initial;
            case 1: return Ready;
            case 2: return Set;
            case 3: return Playing;
            case 4: return Finished;
            default:
                throw new AssertionError("Invalid enum value: " + value);
        }
    }
}
