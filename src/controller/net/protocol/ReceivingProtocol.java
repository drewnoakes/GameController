package controller.net.protocol;

import common.annotations.Nullable;

import java.nio.ByteBuffer;

public interface ReceivingProtocol<T>
{
    /**
     * The size in bytes of each fixed-size network message, as required by this protocol version.
     */
    int getMessageSize();

    /**
     * Attempts to parse the provided byte array as a message of this protocol version.
     *
     * @param buffer the bytes to parse
     * @return the message object if successfully parsed, otherwise null
     */
    @Nullable
    T fromBytes(ByteBuffer buffer);
}
