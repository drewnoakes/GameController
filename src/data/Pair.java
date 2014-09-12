package data;

import common.annotations.NotNull;

import java.util.*;

/**
 * Holds two objects that map to the two teams playing a game.
 * <p>
 * Objects are assigned to the left/right side of the UI and may be
 * looked up by {@link UISide} or by {@link TeamColor}.
 * <p>
 * The mapping between side and colour is held in a shared instance
 * of {@link UIOrientation}.
 *
 * @param <T> the type of contained objects.
 */
public class Pair<T> implements ReadOnlyPair<T>
{
    @NotNull private final UIOrientation orientation;

    @NotNull private T left;
    @NotNull private T right;

    public Pair(@NotNull UIOrientation orientation, @NotNull T left, @NotNull T right)
    {
        this.orientation = orientation;
        this.left = left;
        this.right = right;
    }

    @Override
    @NotNull
    public T get(@NotNull TeamColor color)
    {
        return get(orientation.getSide(color));
    }

    @Override
    @NotNull
    public T get(@NotNull UISide side)
    {
        return side == UISide.Left ? left : right;
    }

    public void set(@NotNull TeamColor color, @NotNull T value)
    {
        set(orientation.getSide(color), value);
    }

    public void set(@NotNull UISide side, @NotNull T value)
    {
        if (side == UISide.Left)
            left = value;
        else
            right = value;
    }

    ////////////////////////////////// Iterator support

    @Override
    public Iterator<T> iterator()
    {
        return new PairIterator(this);
    }

    private class PairIterator implements Iterator<T>
    {
        @NotNull private final Pair<T> pair;
        private int index;

        private PairIterator(@NotNull Pair<T> pair)
        {
            this.pair = pair;
            this.index = -1;
        }

        @Override
        public boolean hasNext()
        {
            return index != 1;
        }

        @Override
        public T next()
        {
            if (index == -1)
            {
                index++;
                return pair.left;
            }

            if (index == 0)
            {
                index++;
                return pair.right;
            }

            throw new NoSuchElementException();
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
