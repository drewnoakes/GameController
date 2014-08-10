package common;

/**
 * Interface for handlers of an event.
 *
 * Used with {@link Event}.
 *
 * @param <T> the type of data item carried with each event invocation.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public interface EventHandler<T>
{
    void handle(T value);
}
