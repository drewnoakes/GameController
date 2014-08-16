package data;

import common.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Pair<T>
{
    private final Collection<T> both;
    private final UIOrientation orientation;

    private T left;
    private T right;

    public Pair(@NotNull UIOrientation orientation, @NotNull T left, @NotNull T right)
    {
        this.orientation = orientation;
        this.left = left;
        this.right = right;

        List<T> bothList = new ArrayList<T>();
        bothList.add(left);
        bothList.add(right);
        both = Collections.unmodifiableCollection(bothList);
    }

    public T get(TeamColor color)
    {
        return get(orientation.getSide(color));
    }

    public T get(UISide side)
    {
        return side == UISide.Left ? left : right;
    }

    public Collection<T> both()
    {
        return both;
    }

    public void set(TeamColor color, T value)
    {
        set(orientation.getSide(color), value);
    }

    public void set(UISide side, T value)
    {
        if (side == UISide.Left)
            left = value;
        else
            right = value;
    }
}
