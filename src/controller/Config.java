package controller;

/**
 * A set of static configuration values.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class Config
{
    /**
     * UDP port that robot status messages are broadcast upon.
     * Robot status messages allow manual penalisation/unpenalisation
     * and allow the GC to know which players are online.
     */
    public static final int ROBOT_STATUS_PORT = 3838;

    /**
     * UDP port for broadcast of game state packets.
     * Game state packets convey referee data to the players.
     */
    public static final int GAME_STATE_PORT = 3838;

    /**
     * The number of milliseconds between broadcasts of game state.
     */
    public static final int GAME_STATE_SEND_PERIOD_MILLIS = 500;

    /**
     * UDP port that SPL coach messages are broadcast upon.
     */
    public static final int SPL_COACH_MESSAGE_PORT = 3839;

    /** Relative directory of where logs are stored */
    public final static String LOG_DIRECTORY = "logs";

    private Config() {}
}
