package controller;

import common.Interval;
import controller.action.ActionBoard;

/**
 * Periodically fires action ClockTick action at {@link ActionBoard#clock}.
 *
 * @author Michel Bartsch
 */
public class Clock
{
    /**
     * The time in millis to sleep before next ClockTick. This does not mean
     * it fires after this time, it will always take some more millis depending
     * on the performance.
     */
    private static final int HEARTBEAT = 500; // 2Hz

    /** The instance of the singleton. */
    private static Clock instance;

    /** The thread of this clock. */
    private Thread thread;
    
    /**
     * Returns the instance of the singleton. If the Clock wasn't initialized once before, a new instance will
     * be created and returned (lazy instantiation)
     *
     * @return  The instance of the Clock
     */
    public static Clock getInstance()
    {
        if (instance == null) {
            instance = new Clock();
        }
        return instance;
    }

    /**
     * Blocks until {@link Clock#stop} is called, firing {@link ActionBoard#clock}
     * every {@link Clock#HEARTBEAT} milliseconds.
     */
    public void start()
    {
        thread = Thread.currentThread();

        Interval interval = new Interval(HEARTBEAT);

        while (!thread.isInterrupted()) {
            ActionBoard.clock.invoke();
            
            try {
                interval.sleep();
            } catch (InterruptedException e) {
                thread.interrupt();
            }
        }

        // clean interrupted status
        Thread.interrupted();
    }

    public void stop()
    {
        thread.interrupt();
    }
}