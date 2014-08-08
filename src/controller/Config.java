package controller;

/**
 * A set of static configuration values.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class Config
{
    /**
     * UDP port for broadcast of return packets.
     * Return packets indicate a robot's status to the game controller.
     */
    public static final int RETURN_DATA_PORT = 3838;

    /**
     * UDP port for broadcast of game state packets.
     * Game state packets convey referee data to the players.
     */
    public static final int GAME_DATA_PORT = 3838;

    /**
     * The number of milliseconds between sends of the game data.
     */
    public static final int GAME_DATA_SEND_PERIOD_MILLIS = 500;
}
