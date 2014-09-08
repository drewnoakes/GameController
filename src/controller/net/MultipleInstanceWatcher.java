package controller.net;

import common.annotations.NotNull;
import controller.Config;
import controller.net.protocol.GameStateProtocol9;
import data.GameStateSnapshot;
import data.League;

import java.net.SocketException;

/**
 * Listens for other instances of the game controller running on the same network.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class MultipleInstanceWatcher
{
    private final MessageReceiver<GameStateSnapshot> receiver;
    private long lastOtherGameSeenTime;

    public MultipleInstanceWatcher(@NotNull League league, final int gameId) throws SocketException
    {
        lastOtherGameSeenTime = -1;

        receiver = new MessageReceiver<GameStateSnapshot>(
            Config.GAME_STATE_PORT,
            500,
            new MessageHandler<GameStateSnapshot>()
            {
                @Override
                public void handle(GameStateSnapshot message)
                {
                    if (message.getGameId() != gameId)
                    {
                        lastOtherGameSeenTime = System.currentTimeMillis();
                    }
                }
            });
        receiver.addProtocol(new GameStateProtocol9(league, gameId));
        receiver.start();
    }

    /**
     * Gets a value indicating whether another game controller has been observed on the network within
     * the last few seconds.
     *
     * @return <code>true</code> if another game controller is active, otherwise <code>false</code>.
     */
    public boolean isOtherGameControllerActive()
    {
        return lastOtherGameSeenTime != -1
                && (System.currentTimeMillis() - lastOtherGameSeenTime < 5000);
    }

    public void stop() throws InterruptedException
    {
        receiver.stop();
    }
}