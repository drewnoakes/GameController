package visualizer;

import common.Log;
import controller.Config;
import controller.net.protocol.GameStateProtocol;
import controller.net.protocol.GameStateProtocol8;
import data.GameState;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

/**
 * This class receives @{link GameState} from the GameController.
 *
 * @author Michel Bartsch
 */
public class GameStateListener
{
    /** The thread instance owned by this listener class. */
    private final ListenerThread listenerThread;
    /** The GUI to listen for, its update method will be called. */
    private final GUI gui;
    /** The protocol we are listening to. */
    private final GameStateProtocol networkProtocol;

    /** Some attributes for receiving. */
    private DatagramSocket datagramSocket;

    /**
     * Creates a new GameStateListener.
     */
    public GameStateListener(GUI gui)
    {
        this.gui = gui;
        try {
            datagramSocket = new DatagramSocket(null);
            datagramSocket.setReuseAddress(true);
            datagramSocket.setSoTimeout(500);
            datagramSocket.bind(new InetSocketAddress(Config.GAME_STATE_PORT));
        } catch (SocketException e) {
            Log.error("Error on start listening to port " + Config.GAME_STATE_PORT);
            System.exit(1);
        }

        networkProtocol = new GameStateProtocol8();

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
                    Log.error("Error while listening to port " + Config.GAME_STATE_PORT);
                }
            }
        }
    }
}