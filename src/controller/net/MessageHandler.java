package controller.net;

/**
 * Interface for network protocols to advertise the type(s) they are able to decode.
 *
 * @param <T> The class into which messages are decoded
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public interface MessageHandler<T>
{
    void handle(T message);
}
