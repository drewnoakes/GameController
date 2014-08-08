package common;

/**
 * When used in a loop, causes a series of events to occur at a regular interval.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class Interval
{
    private final long periodMillis;
    private long nextAtMillis;

    /**
     * Initialises this interval with the specified number of millis, and starts it from now.
     * @param periodMillis the period in between occurrences
     */
    public Interval(long periodMillis)
    {
        this.periodMillis = periodMillis;

        this.nextAtMillis = System.currentTimeMillis() + this.periodMillis;
    }

    /**
     * If there is some time to wait until the next interval starts, sleeps, otherwise falls through.
     * @throws InterruptedException if the sleep was interrupted
     */
    public void sleep() throws InterruptedException
    {
        long sleep = this.nextAtMillis - System.currentTimeMillis();

        if (sleep > 0)
            Thread.sleep(sleep);

        this.nextAtMillis += this.periodMillis;
    }
}
