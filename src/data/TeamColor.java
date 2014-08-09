package data;

import rules.Rules;
import rules.SPL;

import java.awt.*;

/**
 * Enum of team colours.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public enum TeamColor
{
    Blue((byte)0),
    Red((byte)1);

    private static final Color[] splColors = { Color.BLUE, Color.RED };

    private static final Color[] hlColors = {
        // Cyan (not at full brightness)
        new Color(0.0f, 0.75f, 0.75f),
        // Magenta (not at full brightness)
        new Color(0.75f, 0.0f, 0.7f)
    };


    private final byte value;

    TeamColor(byte value)
    {
        this.value = value;
    }

    public byte getValue()
    {
        return value;
    }

    public Color getColor()
    {
        return (Rules.league instanceof SPL ? splColors : hlColors)[value];
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
