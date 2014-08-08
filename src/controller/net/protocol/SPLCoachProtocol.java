package controller.net.protocol;

import common.annotations.Nullable;
import data.SPLCoachMessage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Base class for the SPL coach communication protocols. Subclasses implement specific versions.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public abstract class SPLCoachProtocol
{
    private static final String HEADER = "SPLC";

    protected final byte versionNumber;

    protected SPLCoachProtocol(byte versionNumber)
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
     * @return the {@link data.RobotMessage} if successfully parsed, otherwise null
     */
    @Nullable
    public abstract SPLCoachMessage fromBytes(ByteBuffer buffer);

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
}
