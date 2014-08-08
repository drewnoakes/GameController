package controller.net.protocol;

import common.annotations.Nullable;
import controller.net.RobotStatus;
import data.RobotMessage;

import java.nio.ByteBuffer;

/**
 * Implements robot status network protocol, version 2.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class RobotStatusProtocol2 extends RobotStatusProtocol
{
    public RobotStatusProtocol2()
    {
        super((byte)2);
    }

    @Override
    public int getMessageSize()
    {
        return
            4 + // header
            1 + // version
            1 + // team
            1 + // player
            1;  // message
    }

    @Nullable
    @Override
    public RobotMessage fromBytes(ByteBuffer buffer)
    {
        if (!verifyHeader(buffer))
            return null;

        byte team = buffer.get();
        byte player = buffer.get();
        byte statusByte = buffer.get();

        RobotStatus status = RobotStatus.fromValue(statusByte);

        if (status == null)
            return null;

        return new RobotMessage(team, player, status);
    }
}
