package controller.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import common.Log;
import controller.Config;
import controller.net.protocol.RobotStatusProtocol;
import data.RobotMessage;

/**
 * Asynchronously receives messages from robots via UDP broadcast.
 *
 * Client code must call start, then stop when no longer required.
 *
 * Received messages are passed to {@link RobotWatcher#update(data.RobotMessage)}.
 *
 * @author Marcel Steinbeck
 * @author Drew Noakes https://drewnoakes.com
 */
public class RobotMessageReceiver
{
    /** The used socket to receive the packages. */
    private final DatagramSocket datagramSocket;

    /** The thread instance owned by this receiver class. */
    private final RobotMessageReceiverThread receiverThread;

    private final List<RobotStatusProtocol> protocols = new ArrayList<RobotStatusProtocol>();

    /**
     * Creates a new RobotMessageReceiver.
     *
     * @throws SocketException the an error occurs while creating the socket
     */
    public RobotMessageReceiver() throws SocketException
    {
        datagramSocket = new DatagramSocket(null);
        datagramSocket.setReuseAddress(true);
        datagramSocket.setSoTimeout(500);
        datagramSocket.bind(new InetSocketAddress(Config.ROBOT_STATUS_PORT));

        receiverThread = new RobotMessageReceiverThread();
    }

    public void addProtocol(RobotStatusProtocol protocol)
    {
        protocols.add(protocol);
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

    private class RobotMessageReceiverThread extends Thread
    {
        @Override
        public void run() {
            // Find the maximum message size across protocols, so we can allocate
            // a sufficient buffer for the largest message.
            int bufferSize = 0;
            for (RobotStatusProtocol protocol : protocols)
                bufferSize = Math.max(bufferSize, protocol.getMessageSize());

            while (!isInterrupted()) {
                final ByteBuffer buffer = ByteBuffer.wrap(new byte[bufferSize]);
                final DatagramPacket packet = new DatagramPacket(buffer.array(), buffer.array().length);

                try {
                    // Block until a packet is received, or we time out
                    datagramSocket.receive(packet);

                    // Try to decode this message using all protocols
                    for (RobotStatusProtocol protocol : protocols) {
                        buffer.rewind();
                        RobotMessage message = protocol.fromBytes(buffer);
                        if (message != null) {
                            // Message decoded successfully. Process and break.
                            RobotWatcher.update(message);
                            break;
                        }
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
