package controller.net.protocol;

import common.annotations.Nullable;
import controller.net.RobotStatus;
import data.RobotMessage;

import java.nio.ByteBuffer;

/**
 * Implements robot status network protocol, version 1.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class RobotStatusProtocol1 extends RobotStatusProtocol
{
    public RobotStatusProtocol1()
    {
        super((byte)1);
    }

    @Override
    public int getMessageSize()
    {
        return
            4 + // header
            4 + // version
            2 + // team
            2 + // player
            4;  // message
    }

    @Nullable
    @Override
    public RobotMessage fromBytes(ByteBuffer buffer)
    {
        if (!verifyHeader(buffer))
            return null;

        byte team = (byte)buffer.getShort();
        byte player = (byte)buffer.getShort();
        byte statusByte = (byte)buffer.getInt();

        RobotStatus status = RobotStatus.fromValue(statusByte);

        if (status == null)
            return null;

        return new RobotMessage(team, player, status);
    }
}

