package controller.net.protocol;

import data.GameControlData;

import java.nio.ByteBuffer;

/**
 * Base class for network protocol versions.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public abstract class NetworkProtocol
{
    protected final byte versionNumber;

    protected byte packetNumber = 0;

    protected NetworkProtocol(byte versionNumber)
    {
        this.versionNumber = versionNumber;
    }

    public byte getVersionNumber()
    {
        return versionNumber;
    }

    /**
     * The size in bytes of each fixed-size network message, as formatted by this protocol version.
     */
    public abstract int getMessageSize();

    /**
     * Attempts to parse the provided byte array as a message of this protocol version.
     *
     * @param buffer the bytes to parse
     * @return the {@link data.GameControlData} if successfully parsed, otherwise null
     */
    public abstract GameControlData fromBytes(ByteBuffer buffer);

    /**
     * Formats an instance of {@link GameControlData} for network transmission.
     *
     * @param data The source of data for the message
     * @return the byte array to be sent via the network
     */
    public abstract byte[] toBytes(GameControlData data);

    /**
     * Increments the packet number for the sent message.
     *
     * Should only be incremented after a successful transmission, so
     * clients track receive errors and not send errors.
     */
    public void incrementPacketNumber()
    {
        packetNumber++;
    }
}