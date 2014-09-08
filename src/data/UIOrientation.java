package data;

/**
 * Holds the assignment of {@link TeamColor} to {@link UISide}.
 * <p>
 * There should be one instance of this object per game. It may be updated during the
 * execution of a game, and components which distinguish between teams will become
 * aware of the change.
 *
 * @see data.Pair
 * @author Drew Noakes https://drewnoakes.com
 */
public class UIOrientation
{
    private boolean isFlipped = false;

    public void flip()
    {
        isFlipped = !isFlipped;
    }

    public boolean isFlipped()
    {
        return isFlipped;
    }

    public UISide getSide(TeamColor color)
    {
        return (color == TeamColor.Blue) != isFlipped ? UISide.Left : UISide.Right;
    }

    public TeamColor getColor(UISide side)
    {
        return (side == UISide.Left) != isFlipped ? TeamColor.Blue : TeamColor.Red;
    }
}
