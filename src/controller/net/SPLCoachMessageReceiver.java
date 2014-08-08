package controller.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

import common.Log;
import controller.action.net.SPLCoachMessageReceived;
import controller.net.protocol.SPLCoachProtocol;
import controller.net.protocol.SPLCoachProtocol2;
import data.SPLCoachMessage;

public class SPLCoachMessageReceiver
{
    private final DatagramSocket datagramSocket;

    private final SPLCoachMessageReceiverThread receiverThread;

    private final SPLCoachProtocol coachProtocol;

    public SPLCoachMessageReceiver() throws SocketException
    {
        datagramSocket = new DatagramSocket(null);
        datagramSocket.setReuseAddress(true);
        datagramSocket.setSoTimeout(500);
        datagramSocket.bind(new InetSocketAddress(SPLCoachMessage.SPL_COACH_MESSAGE_PORT));

        receiverThread = new SPLCoachMessageReceiverThread();

        coachProtocol = new SPLCoachProtocol2();
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

    private class SPLCoachMessageReceiverThread extends Thread
    {
        @Override
        public void run()
        {
            while (!isInterrupted()) {
                try {
                    final byte[] bytes = new byte[coachProtocol.getMessageSize()];

                    final DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                    datagramSocket.receive(packet);

                    final ByteBuffer buffer = ByteBuffer.wrap(bytes);
                    buffer.rewind();

                    final SPLCoachMessage coach = coachProtocol.fromBytes(buffer);
                    if (coach != null)
                        new SPLCoachMessageReceived(coach).actionPerformed(null);
                } catch (SocketTimeoutException e) { // ignore, because we set a timeout
                } catch (IOException e) {
                    Log.error("something went wrong while receiving the coach packages : " + e.getMessage());
                }
            }

            datagramSocket.close();
        }
    }
}
