package data;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import controller.EventHandler;

/**
 * A message received from the coach, and to be sent out to players after some delay.
 *
 * SPL only.
 *
 * @author Sebastian Koralewski
 * @author Drew Noakes https://drewnoakes.com
 */
public class SPLCoachMessage implements Serializable
{
    public static final long SPL_COACH_MESSAGE_RECEIVE_INTERVAL = 10000; // in ms
    public static final int SPL_COACH_MESSAGE_SIZE = 40;

    private static final long SPL_COACH_MESSAGE_MIN_SEND_INTERVAL = 3000; // in ms
    private static final long SPL_COACH_MESSAGE_MAX_SEND_INTERVAL = 6000; // in ms

    /** The coach's team's uniquely identifying team number for the tournament. */
    public final byte teamNumber;
    /** Contents of the coach's message. */
    public final byte[] message;
    /** The delay in millis that the message will be held back. */
    private final long sendTime;

    public SPLCoachMessage(byte teamNumber, byte[] message)
    {
        this.teamNumber = teamNumber;
        this.message = message;
        this.sendTime = generateSendIntervalForSPLCoachMessage() + System.currentTimeMillis();
    }

    /** The remaining period of time before the message may be distrubuted to players, in milliseconds. */
    public long getRemainingTimeToSend()
    {
        long remainingTime = sendTime - System.currentTimeMillis();
        return remainingTime > 0 ? remainingTime : 0;
    }

    private static long generateSendIntervalForSPLCoachMessage()
    {
        return (long) (Math.random() * (SPLCoachMessage.SPL_COACH_MESSAGE_MAX_SEND_INTERVAL
                - SPLCoachMessage.SPL_COACH_MESSAGE_MIN_SEND_INTERVAL))
                + SPLCoachMessage.SPL_COACH_MESSAGE_MIN_SEND_INTERVAL;
    }
}
