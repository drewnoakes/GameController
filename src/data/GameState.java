package data;

/**
 * Enum of primary game states.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public enum GameState
{
    /**
     * The game is in 'initial' state.
     * We are waiting for the referee to call 'ready'.
     */
    Initial((byte)0),

    /**
     * The game is in 'ready' state.
     * Robots may position automatically, or be positioned manually.
     * The referee will call 'set' soon.
     */
    Ready((byte)1),

    /**
     * The game is in 'set' state.
     * Robots must stop moving, and robot handlers must leave the field.
     * The referee will call 'play' soon.
     */
    Set((byte)2),

    /** The game is in 'playing' state. */
    Playing((byte)3),

    /**
     * The game is in 'finished' state as a half has completed.
     * There may yet be another half, extra time or penalties.
     */
    Finished((byte)4);

    private final byte value;

    GameState(byte value)
    {
        this.value = value;
    }

    /** Get the numeric value used in network messages for this game state. */
    public byte getValue()
    {
        return value;
    }

    /** Decode a numeric value from a network message. */
    public static GameState fromValue(byte value)
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
