package data;

import common.annotations.NotNull;

import java.io.Serializable;

/**
 * Immutable model of a message received from a coach, to be sent out to players after some delay.
 *
 * SPL only.
 *
 * @author Sebastian Koralewski
 * @author Drew Noakes https://drewnoakes.com
 */
public class SPLCoachMessage implements Serializable
{
    /** The size of the SPL coach message, in bytes. */
    public static final int SIZE = 40;

    public static final long SPL_COACH_MESSAGE_RECEIVE_INTERVAL = 10000; // in ms
    private static final long SPL_COACH_MESSAGE_MIN_SEND_INTERVAL = 3000; // in ms
    private static final long SPL_COACH_MESSAGE_MAX_SEND_INTERVAL = 6000; // in ms

    /** The coach's team's uniquely identifying team number for the tournament. */
    public final byte teamNumber;
    /** Contents of the coach's message. */
    @NotNull public final byte[] bytes;
    /** The delay in millis that the message will be held back. */
    private final long sendTime;

    public SPLCoachMessage(byte teamNumber, @NotNull byte[] bytes)
    {
        // All chars after the first zero are zeroed
        int k = 0;
        while (k < bytes.length && bytes[k] != 0) {
            k++;
        }
        while (k < bytes.length) {
            bytes[k++] = 0;
        }

        this.teamNumber = teamNumber;
        this.bytes = bytes;
        this.sendTime = generateSendIntervalForSPLCoachMessage() + System.currentTimeMillis();
    }

    /** The remaining period of time before the message may be distributed to players, in milliseconds. */
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
