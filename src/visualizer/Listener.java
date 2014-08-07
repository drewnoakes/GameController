package visualizer;

import common.Log;
import controller.net.protocol.NetworkProtocol;
import controller.net.protocol.NetworkProtocol8;
import data.GameControlData;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

/**
 * @author Michel Bartsch
 * 
 * This class receives the GameControlData from the GameController.
 */
public class Listener
{
    /** The thread instance owned by this listener class. */
    private final ListenerThread listenerThread;
    /** The GUI to listen for, its update method will be called. */
    private final GUI gui;
    /** The protocol we are listening to. */
    private final NetworkProtocol networkProtocol;

    /** Some attributes for receiving. */
    private DatagramSocket datagramSocket;

    /**
     * Creates a new Listener.
     */
    public Listener(GUI gui)
    {
        this.gui = gui;
        try {
            datagramSocket = new DatagramSocket(null);
            datagramSocket.setReuseAddress(true);
            datagramSocket.setSoTimeout(500);
            datagramSocket.bind(new InetSocketAddress(GameControlData.GAMECONTROLLER_GAMEDATA_PORT));
        } catch (SocketException e) {
            Log.error("Error on start listening to port " + GameControlData.GAMECONTROLLER_GAMEDATA_PORT);
            System.exit(1);
        }

        networkProtocol = new NetworkProtocol8();

        listenerThread = new ListenerThread();
    }

    public void start()
    {
        listenerThread.start();
    }

    public void stop() throws InterruptedException
    {
        listenerThread.interrupt();
        listenerThread.join();
    }

    private class ListenerThread extends Thread
    {
        @Override
        public void run()
        {
            while (!isInterrupted()) {
                final ByteBuffer buffer = ByteBuffer.wrap(new byte[networkProtocol.getMessageSize()]);

                final DatagramPacket packet = new DatagramPacket(buffer.array(), buffer.array().length);

                try {
                    datagramSocket.receive(packet);
                    buffer.rewind();
                    final GameControlData data = networkProtocol.fromBytes(buffer);
                    if (data != null) {
                        gui.update(data);
                    }
                } catch (SocketTimeoutException e) { // ignore, because we set a timeout
                } catch (IOException e) {
                    Log.error("Error while listening to port " + GameControlData.GAMECONTROLLER_GAMEDATA_PORT);
                }
            }
        }
    }
}