package controller.net;

import common.Log;
import data.AdvancedData;
import data.GameControlData;
import rules.Rules;

import java.io.IOException;
import java.net.*;

/**
 * @author Marcel Steinbeck
 *
 * This class is used to send the current {@link GameControlData} (game-state) to all robots every 500 ms.
 * The package will be send via UDP on port {@link GameControlData#GAMECONTROLLER_GAMEDATA_PORT} over broadcast.
 *
 * To prevent race-conditions (the sender is executed in its thread-context), the sender creates a deep copy
 * of {@link GameControlData} via {@link AdvancedData#clone()}.
 *
 * This class is a singleton!
 */
public class Sender
{
    /* SINGLETON MEMBERS ------------------------------------------------------------------- */

    /** The instance of the singleton. */
    private static Sender instance;

    /**
     * Initialises the Sender. This needs to be called before {@link #getInstance()} is available.
     * @param broadcastAddress      the broadcast address to use
     * @throws SocketException          if an error occurs while creating the socket
     * @throws UnknownHostException     if the used inet-address is not valid
     * @throws IllegalStateException    if the sender is already initialized
     */
    public synchronized static void initialize(final String broadcastAddress) throws SocketException, UnknownHostException
    {
        if (null != instance) {
            throw new IllegalStateException("sender is already initialized");
        } else {
            instance = new Sender(broadcastAddress);
        }
    }

    /**
     * Returns the instance of the singleton.
     *
     * @return  The instance of the Sender
     * @throws  IllegalStateException if the Sender is not initialized yet
     */
    public synchronized static Sender getInstance()
    {
        if (null == instance) {
            throw new IllegalStateException("sender is not initialized yet");
        } else {
            return instance;
        }
    }

    /* INSTANCE MEMBERS ------------------------------------------------------------------- */

    /** The thread instance owned by this sender class. */
    private final SenderThread senderThread;

    /** The socket, which is used to send the current game-state */
    private final DatagramSocket datagramSocket;

    /** The used inet-address (the broadcast address). */
    private final InetAddress group;

    /** The packet number that is increased with each packet sent. */
    private byte packetNumber = 0;

    /** The current deep copy of the game-state. */
    private AdvancedData data;

    /**
     * Creates a new Sender.
     *
     * @throws SocketException      if an error occurs while creating the socket
     * @throws UnknownHostException if the used inet-address is not valid
     */
    private Sender(final String broadcastAddress) throws SocketException, UnknownHostException
    {
        assert(instance == null);

        datagramSocket = new DatagramSocket();
        group = InetAddress.getByName(broadcastAddress);
        senderThread = new SenderThread();
    }

    /**
     * Sets the current game-state to send. Creates a clone of data to prevent race-conditions.
     * See {@link AdvancedData#clone()}.
     *
     * @param data the current game-state to send to all robots
     */
    public void send(AdvancedData data)
    {
        this.data = (AdvancedData) data.clone();
    }

    public void start()
    {
        senderThread.start();
    }

    public void stop() throws InterruptedException
    {
        senderThread.interrupt();
        senderThread.join();
    }

    private class SenderThread extends Thread
    {
        @Override
        public void run()
        {
            while (!isInterrupted()) {
                // Take a copy of the reference to prevent errors cause when data is modified while this thread is running
                AdvancedData data = Sender.this.data;

                if (data != null) {
                    data.updateTimes();
                    data.packetNumber = packetNumber;
                    byte[] arr = data.toByteArray().array();
                    DatagramPacket packet = new DatagramPacket(arr, arr.length, Sender.this.group, GameControlData.GAMECONTROLLER_GAMEDATA_PORT);

                    try {
                        datagramSocket.send(packet);
                        packetNumber++;
                    } catch (IOException e) {
                        Log.error("Error while sending");
                        e.printStackTrace();
                    }
                }

                if (data != null) {
                    if (Rules.league.compatibilityToVersion7) {
                        byte[] arr = data.toByteArray7().array();
                        DatagramPacket packet = new DatagramPacket(arr, arr.length, Sender.this.group, GameControlData.GAMECONTROLLER_GAMEDATA_PORT);

                        try {
                            datagramSocket.send(packet);
                        } catch (IOException e) {
                            Log.error("Error while sending");
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    interrupt();
                }
            }

            datagramSocket.close();
        }
    }
}
