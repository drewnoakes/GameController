package controller.net.protocol;

import common.annotations.Nullable;
import controller.ActionHandler;
import data.SPLCoachMessage;
import data.TeamInfo;

import java.nio.ByteBuffer;

/**
 * Implements the SPL coach network protocol, version 2.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class SPLCoachProtocol2 extends SPLCoachProtocol
{
    private final byte teamNumberBlue;
    private final byte teamNumberRed;

    public SPLCoachProtocol2(byte teamNumberBlue, byte teamNumberRed)
    {
        super((byte)2);

        this.teamNumberBlue = teamNumberBlue;
        this.teamNumberRed = teamNumberRed;
    }

    @Override
    public int getMessageSize()
    {
        return
            4 + // header size
            1 + // byte for the version
            1 + // team number
            SPLCoachMessage.SPL_COACH_MESSAGE_SIZE;
    }

    @Nullable
    @Override
    public SPLCoachMessage fromBytes(ByteBuffer buffer)
    {
        if (!verifyHeader(buffer))
            return null;

        byte team = buffer.get();
        if (team != teamNumberBlue && team != teamNumberRed)
            return null;

        byte[] message = new byte[SPLCoachMessage.SPL_COACH_MESSAGE_SIZE];
        buffer.get(message);

        return new SPLCoachMessage(team, message);
    }
}
