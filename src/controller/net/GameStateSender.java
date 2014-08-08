package controller.net;

import common.Log;
import controller.Config;
import controller.net.protocol.GameStateProtocol;
import data.GameState;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to send the current {@link data.GameState} to all robots every 500 ms.
 * The packet will be sent via UDP broadcast on port {@link Config#GAME_STATE_PORT}.
 *
 * To prevent race-conditions (the sender is executed in its thread-context), the sender creates a deep copy
 * of {@link data.GameState} via {@link data.GameState#clone()}.
 *
 * @author Marcel Steinbeck
 * @author Drew Noakes https://drewnoakes.com
 */
public class GameStateSender
{
    /** The thread instance owned by this sender class. */
    private final SenderThread senderThread;

    /** The socket, which is used to send the current game-state */
    private final DatagramSocket datagramSocket;

    /** The used inet-address (the broadcast address). */
    private final InetAddress group;

    private final List<GameStateProtocol> protocols = new ArrayList<GameStateProtocol>();

    /** The current deep copy of the game-state. */
    private GameState data;

    /**
     * Creates a new GameStateSender.
     *
     * @throws SocketException      if an error occurs while creating the socket
     * @throws UnknownHostException if the used inet-address is not valid
     */
    public GameStateSender(final String broadcastAddress) throws SocketException, UnknownHostException
    {
        datagramSocket = new DatagramSocket();
        group = InetAddress.getByName(broadcastAddress);
        senderThread = new SenderThread();
    }

    public void addProtocol(GameStateProtocol protocol)
    {
        protocols.add(protocol);
    }

    /**
     * Sets the current game-state to send. Creates a clone of data to prevent race-conditions.
     * See {@link data.GameState#clone()}.
     *
     * @param data the current game-state to send to all robots
     */
    public void send(GameState data)
    {
        this.data = (GameState) data.clone();
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
                GameState data = GameStateSender.this.data;

                if (data != null) {
                    data.updateTimes();

                    for (GameStateProtocol version : protocols) {
                        byte[] bytes = version.toBytes(data);
                        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, GameStateSender.this.group, Config.GAME_STATE_PORT);
                        try {
                            datagramSocket.send(packet);
                            version.incrementPacketNumber();
                        } catch (IOException e) {
                            Log.error("Error while sending");
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    Thread.sleep(Config.GAME_STATE_SEND_PERIOD_MILLIS);
                } catch (InterruptedException e) {
                    interrupt();
                }
            }

            datagramSocket.close();
        }
    }
}
