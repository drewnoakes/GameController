package controller.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

import common.Log;
import controller.Config;
import data.GameControlReturnData;

/**
 * Asynchronously receives robot status packets via UDB broadcast.
 *
 * Client code must call start, then stop when no longer required.
 *
 * Received messages are passed to {@link RobotWatcher#update(data.GameControlReturnData)}.
 *
 * @author Marcel Steinbeck
 */
public class GameControlReturnDataReceiver
{
    /** The used socket to receive the packages. */
    private final DatagramSocket datagramSocket;

    /** The thread instance owned by this receiver class. */
    private final GameControlReturnDataReceiverThread receiverThread;

    /**
     * Creates a new Receiver.
     *
     * @throws SocketException the an error occurs while creating the socket
     */
    public GameControlReturnDataReceiver() throws SocketException
    {
        datagramSocket = new DatagramSocket(null);
        datagramSocket.setReuseAddress(true);
        datagramSocket.setSoTimeout(500);
        datagramSocket.bind(new InetSocketAddress(Config.RETURN_DATA_PORT));

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
