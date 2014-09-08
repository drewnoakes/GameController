package controller.net.protocol;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.ReadOnlyGameState;
import data.GameStateSnapshot;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Base class for game state network protocols. Subclasses implement specific versions.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public abstract class GameStateProtocol implements ReceivingProtocol<GameStateSnapshot>
{
    private static final String HEADER = "RGme";

    private final byte versionNumber;

    protected byte nextPacketNumber = 0;

    protected GameStateProtocol(byte versionNumber)
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
     * @param buffer the bytes to parse.
     * @return the {@link data.GameStateSnapshot} if successfully parsed, otherwise <code>null</code>.
     */
    @Nullable
    public abstract GameStateSnapshot fromBytes(@NotNull ByteBuffer buffer);

    /**
     * Formats an instance of {@link controller.ReadOnlyGameState} for network transmission.
     *
     * @param state the source of data for the message.
     * @return the byte array to be sent via the network.
     */
    @NotNull
    public abstract byte[] toBytes(@NotNull ReadOnlyGameState state);

    /**
     * Increments the packet number, ready for the next message.
     *
     * Should only be incremented after a successful transmission, so
     * clients track receive errors and not send errors.
     */
    public void incrementPacketNumber()
    {
        nextPacketNumber++;
    }

    /** Verifies the buffer starts with the expected header for this version of protocol. */
    protected boolean verifyHeader(ByteBuffer buffer)
    {
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // Header
        byte[] headerBytes = new byte[4];
        buffer.get(headerBytes, 0, 4);
        String header = new String(headerBytes);

        if (!header.equals(HEADER))
            return false;

        // Version
        byte version = buffer.get();
        return version == getVersionNumber();
    }

    /** Create a new buffer and writes the correct header for this version of the protocol. */
    protected ByteBuffer writeHeader()
    {
        ByteBuffer buffer = ByteBuffer.allocate(getMessageSize());
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        buffer.put(HEADER.getBytes(), 0, 4);

        return buffer;
    }
}
