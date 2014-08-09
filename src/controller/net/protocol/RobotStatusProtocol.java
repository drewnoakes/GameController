package controller.net.protocol;

import data.RobotMessage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Base class for robot status network protocols. Subclasses implement specific versions.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public abstract class RobotStatusProtocol implements ReceivingProtocol<RobotMessage>
{
    private static final String HEADER = "RGrt";

    private final byte versionNumber;

    protected RobotStatusProtocol(byte versionNumber)
    {
        this.versionNumber = versionNumber;
    }

    public byte getVersionNumber()
    {
        return versionNumber;
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
}
