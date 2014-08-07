package controller.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

import common.Log;

import data.GameControlData;
import data.GameControlReturnData;

/**
 *
 * @author Marcel Steinbeck
 *
 * This class is used to receive a packet send by a robot on port {@link GameControlData#GAMECONTROLLER_RETURNDATA_PORT} via UDP
 * over broadcast.
 * If a package was received, this class will invoke {@link RobotWatcher#update(data.GameControlReturnData)} to update
 * the robots online status.
 *
 * This class is a singleton!
 */
public class GameControlReturnDataReceiver
{
    /* SINGLETON MEMBERS ------------------------------------------------------------------- */

    /** The instance of the singleton. */
    private static GameControlReturnDataReceiver instance;

    /**
     * Returns the instance of the singleton. If the Receiver wasn't initialized once before, a new instance will
     * be created and returned (lazy instantiation)
     *
     * @return  The instance of the Receiver
     * @throws IllegalStateException if the first creation of the singleton throws an exception
     */
    public synchronized static GameControlReturnDataReceiver getInstance()
    {
        if (instance == null) {
            try {
                instance = new GameControlReturnDataReceiver();
            } catch (SocketException e) {
                throw new IllegalStateException("fatal: Error while setting up Receiver.", e);
            }
        }
        return instance;
    }

    /* INSTANCE MEMBERS ------------------------------------------------------------------- */

    /** The used socket to receive the packages. */
    private final DatagramSocket datagramSocket;

    /** The thread instance owned by this receiver class. */
    private final GameControlReturnDataReceiverThread receiverThread;

    /**
     * Creates a new Receiver.
     *
     * @throws SocketException the an error occurs while creating the socket
     */
    private GameControlReturnDataReceiver() throws SocketException
    {
        datagramSocket = new DatagramSocket(null);
        datagramSocket.setReuseAddress(true);
        datagramSocket.setSoTimeout(500);
        datagramSocket.bind(new InetSocketAddress(GameControlData.GAMECONTROLLER_RETURNDATA_PORT));

        receiverThread = new GameControlReturnDataReceiverThread();
    }

    public void start()
    {
        receiverThread.start();
    }

    public void stop() throws InterruptedException
    {
        receiverThread.interrupt();
        receiverThread.join();
    }

    private class GameControlReturnDataReceiverThread extends Thread
    {
        @Override
        public void run() {
           while (!isInterrupted()) {
               final ByteBuffer buffer = ByteBuffer.wrap(new byte[Math.max(GameControlReturnData.SIZE, GameControlReturnData.SIZE1)]);
               final GameControlReturnData player = new GameControlReturnData();

               final DatagramPacket packet = new DatagramPacket(buffer.array(), buffer.array().length);

                try {
                    datagramSocket.receive(packet);
                    buffer.rewind();
                    if (player.fromByteArray(buffer)) {
                        RobotWatcher.update(player);
                    }
                } catch (SocketTimeoutException e) { // ignore, because we set a timeout
                } catch (IOException e) {
                    Log.error("something went wrong while receiving : " + e.getMessage());
                }
            }

            datagramSocket.close();
        }
    }
}
