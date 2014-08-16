package controller.net.protocol;

import common.annotations.NotNull;
import common.annotations.Nullable;
import data.Pair;
import data.SPLCoachMessage;
import data.Team;
import data.TeamColor;

import java.nio.ByteBuffer;

/**
 * Implements the SPL coach network protocol, version 2.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class SPLCoachProtocol2 extends SPLCoachProtocol
{
    @NotNull
    private final Pair<Team> teams;

    public SPLCoachProtocol2(@NotNull Pair<Team> teams)
    {
        super((byte)2);

        this.teams = teams;
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
    public SPLCoachMessage fromBytes(@NotNull ByteBuffer buffer)
    {
        if (!verifyHeader(buffer))
            return null;

        byte team = buffer.get();
        if (team != teams.get(TeamColor.Blue).getNumber() && team != teams.get(TeamColor.Red).getNumber())
            return null;

        byte[] message = new byte[SPLCoachMessage.SPL_COACH_MESSAGE_SIZE];
        buffer.get(message);

        return new SPLCoachMessage(team, message);
    }
}
