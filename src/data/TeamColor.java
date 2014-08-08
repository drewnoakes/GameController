package data;

import java.awt.*;

/**
 * Enum of team colours.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public enum TeamColor
{
    Blue((byte)0, Color.BLUE),
    Red((byte)1, Color.RED);

    private final byte value;
    private final Color color;

    TeamColor(byte value, Color color)
    {
        this.value = value;
        this.color = color;
    }

    public byte getValue()
    {
        return value;
    }

    public Color getColor()
    {
        return color;
    }

    public static TeamColor fromValue(byte value)
    {
        switch (value)
        {
            case 0: return Blue;
            case 1: return Red;
            case 2: return null; // special handling for 'drop ball' value
            default:
                throw new AssertionError("Invalid TeamColor enum value: " + value);
        }
    }

    public TeamColor other()
    {
        return this == Blue ? Red : Blue;
    }
}
