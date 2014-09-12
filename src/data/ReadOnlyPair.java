package data;

import common.annotations.NotNull;

import java.util.Iterator;

public interface ReadOnlyPair<T> extends Iterable<T>
{
    @NotNull
    T get(@NotNull TeamColor color);

    @NotNull
    T get(@NotNull UISide side);

    @Override
    Iterator<T> iterator();
}
