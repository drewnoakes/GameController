package data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Enum of the two sides of the UI, left and right.
 * <p>
 * Values of this enum are used to identify teams by their location within the UI.
 * This needn't map directly to team colours, as colours may be arranged in either
 * of their two configurations.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public enum UISide
{
    Left,
    Right;

    private static Collection<UISide> both;

    static
    {
        List<UISide> both = new ArrayList<UISide>();
        both.add(Left);
        both.add(Right);
        UISide.both = Collections.unmodifiableCollection(both);
    }

    public static Iterable<UISide> both()
    {
        return both;
    }
}
