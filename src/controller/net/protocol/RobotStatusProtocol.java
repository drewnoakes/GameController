package controller.net.protocol;

import common.annotations.Nullable;
import data.RobotMessage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Base class for robot status network protocols. Subclasses implement specific versions.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public abstract class RobotStatusProtocol
{
    protected static final String GAMECONTROLLER_RETURN_STRUCT_HEADER = "RGrt";

    protected final byte versionNumber;

    protected RobotStatusProtocol(byte versionNumber)
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
    public abstract RobotMessage fromBytes(ByteBuffer buffer);

    protected boolean verifyHeader(ByteBuffer buffer)
    {
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // Header
        byte[] headerBytes = new byte[4];
        buffer.get(headerBytes, 0, 4);
        String header = new String(headerBytes);

        if (!header.equals(GAMECONTROLLER_RETURN_STRUCT_HEADER))
            return false;

        // Version
        byte version = buffer.get();
        return version == getVersionNumber();
    }
}
