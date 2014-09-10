package common;

import common.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple event object to which handlers may subscribe for notification of invocations.
 *
 * @param <T> the type of data item carried with each event invocation.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class Event<T>
{
    private final List<EventHandler<T>> handlers = new ArrayList<EventHandler<T>>();

    /**
     * Registers the provided handler for future notifications when this event fires.
     *
     * @param handler a handler for this event.
     */
    public void subscribe(@NotNull EventHandler<T> handler)
    {
        handlers.add(handler);
    }

    /**
     * Fires this event, notifying all subscribed handlers.
     *
     * @param value the data value to include in the event.
     */
    public void fire(T value)
    {
        for (EventHandler<T> handler : handlers)
            handler.handle(value);
    }
}
