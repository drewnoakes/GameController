package controller.net;

import common.Interval;
import common.Log;
import common.annotations.NotNull;
import controller.Config;
import controller.net.protocol.GameStateProtocol;
import controller.Game;
import controller.GameState;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to send the current {@link controller.GameState} to all robots every 500 ms.
 * The packet will be sent via UDP broadcast on port {@link Config#GAME_STATE_PORT}.
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

    /** The game about which to publish state. */
    private final Game game;

    /**
     * Creates a new GameStateSender.
     *
     * @throws SocketException      if an error occurs while creating the socket
     * @throws UnknownHostException if the used inet-address is not valid
     */
    public GameStateSender(@NotNull Game game, @NotNull String broadcastAddress) throws SocketException, UnknownHostException
    {
        this.game = game;
        datagramSocket = new DatagramSocket();
        group = InetAddress.getByName(broadcastAddress);
        senderThread = new SenderThread();
    }

    public void addProtocol(GameStateProtocol protocol)
    {
        protocols.add(protocol);
    }

    public void start()
    {
        assert(protocols.size() != 0);
        assert(!senderThread.isAlive());
        senderThread.start();
    }

    public void stop() throws InterruptedException
    {
        assert(senderThread.isAlive());
        senderThread.interrupt();
        senderThread.join();
        datagramSocket.close();
    }

    private void sendState()
    {
        GameState state = game.getGameState();

        state.updateTimes();

        for (GameStateProtocol version : protocols) {
            try {
                byte[] bytes = version.toBytes(state);
                assert(bytes.length == version.getMessageSize());
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length, group, Config.GAME_STATE_PORT);
                datagramSocket.send(packet);
                version.incrementPacketNumber();
            } catch (Exception e) {
                Log.error("Error while sending game state");
                e.printStackTrace();
            }
        }
    }

    private class SenderThread extends Thread
    {
        @Override
        public void run()
        {
            Interval interval = new Interval(Config.GAME_STATE_SEND_PERIOD_MILLIS);

            while (!isInterrupted()) {
                GameStateSender.this.sendState();

                try {
                    interval.sleep();
                } catch (InterruptedException e) {
                    interrupt();
                }
            }
        }
    }
}
