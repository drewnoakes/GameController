package data;

import common.annotations.NotNull;

import java.util.Iterator;

public interface ReadOnlyPair<T> extends Iterable<T>
{
    @NotNull
    T get(TeamColor color);

    @NotNull
    T get(UISide side);

    @Override
    Iterator<T> iterator();
}
