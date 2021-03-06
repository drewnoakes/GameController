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
import common.annotations.NotNull;
import controller.net.protocol.ReceivingProtocol;

/**
 * Asynchronously receives messages via UDP broadcast.
 *
 * Client code must call {@link MessageReceiver#start}, then {@link MessageReceiver#stop} when no longer required.
 *
 * @author Marcel Steinbeck
 * @author Drew Noakes https://drewnoakes.com
 */
public class MessageReceiver<T>
{
    private final MessageHandler<T> handler;
    /** The used socket to receive UDP packets. */
    private final DatagramSocket datagramSocket;
    /** The thread instance owned by this receiver class. */
    private final ReceiverThread receiverThread;
    /** The set of protocols supported by this receiver. */
    private final List<ReceivingProtocol<T>> protocols = new ArrayList<ReceivingProtocol<T>>();

    /**
     * Creates a new MessageReceiver.
     *
     * @param udpPort the port number to listen on.
     * @param timeoutMillis the number of milliseconds to wait for before timing out and trying again
     * @param handler an object to invoke when a message is successfully received
     * @throws SocketException the an error occurs while creating the socket
     */
    public MessageReceiver(int udpPort, int timeoutMillis, @NotNull MessageHandler<T> handler) throws SocketException
    {
        this.handler = handler;

        datagramSocket = new DatagramSocket(null);
        datagramSocket.setReuseAddress(true);
        datagramSocket.setSoTimeout(timeoutMillis);
        datagramSocket.bind(new InetSocketAddress(udpPort));

        receiverThread = new ReceiverThread();
        receiverThread.setName("Message Receiver");
    }

    public void addProtocol(@NotNull ReceivingProtocol<T> protocol)
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

    private class ReceiverThread extends Thread
    {
        @Override
        public void run() {
            // Find the maximum message size across protocols, so we can allocate
            // a sufficient buffer for the largest possible message.
            int bufferSize = 0;
            for (ReceivingProtocol protocol : protocols)
                bufferSize = Math.max(bufferSize, protocol.getMessageSize());

            while (!isInterrupted()) {
                final byte[] bytes = new byte[bufferSize];
                final DatagramPacket packet = new DatagramPacket(bytes, bufferSize);
                final ByteBuffer buffer = ByteBuffer.wrap(bytes);

                try {
                    // Block until a packet is received, or we time out
                    datagramSocket.receive(packet);

                    // Try to decode this message using all protocols
                    for (ReceivingProtocol<T> protocol : protocols) {
                        buffer.rewind();
                        T message = protocol.fromBytes(buffer);
                        if (message != null) {
                            // Message decoded successfully.
                            // Process and break.
                            MessageReceiver.this.handler.handle(message);
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
