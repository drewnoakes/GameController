package visualizer;

import common.Log;
import controller.Config;
import controller.net.MessageHandler;
import controller.net.MessageReceiver;
import controller.net.protocol.GameStateProtocol8;
import controller.net.protocol.GameStateProtocol9;
import data.GameStateSnapshot;
import data.League;

import java.net.SocketException;

/**
 * Launches the Game State Visualiser, and manages its lifetime including graceful shutdown.
 *
 * @author Michel Bartsch
 * @author Drew Noakes https://drewnoakes.com
 */
public class Main
{
    private static MessageReceiver<GameStateSnapshot> gameStateListener;

    private Main() {}

    /**
     * Entry point for the executable.
     * 
     * @param args command line arguments for the application
     */
    public static void main(String[] args)
    {
        final VisualizerOptions options = parseCommandLineArguments(args);
        final VisualizerUI ui = new VisualizerUI(options);

        new KeyboardListener(ui);

        try {
            gameStateListener = new MessageReceiver<GameStateSnapshot>(
                    Config.GAME_STATE_PORT,
                    500,
                    new MessageHandler<GameStateSnapshot>()
                    {
                        @Override
                        public void handle(GameStateSnapshot state) { ui.update(state); }
                    });
            gameStateListener.addProtocol(new GameStateProtocol9(options.getLeague(), -1));
            gameStateListener.start();
        } catch (SocketException e) {
            System.err.println("Exception binding listener");
            System.exit(1);
        }
    }

    /**
     * This should be called when the program is shutting down to close
     * sockets and finally exit.
     */
    public static void exit()
    {
        try {
            gameStateListener.stop();
        } catch (InterruptedException e) {
            Log.error("Waiting for gameStateListener to shutdown was interrupted.");
        }
        System.exit(0);
    }

    private static VisualizerOptions parseCommandLineArguments(String[] args)
    {
        if (args.length > 0 && (args[0].equals("-h") || args[0].equals("--help")) ) {
            printUsage();
            System.exit(0);
        }

        League league = League.SPL;
        
        if (args.length >= 2 && (args[0].equals("-l") || args[0].equals("--league"))) {
            league = League.findByDirectoryName(args[1]);
            if (league == null) {
                printUsage();
                System.exit(1);
            }
        }
        
        return new VisualizerOptions(league);
    }

    private static void printUsage()
    {
        System.out.println("Usage: java -jar GameController.jar <options>"
                + "\n  (-h | --help)                   show this help message"
                + "\n  (-l | --league) <league-dir>    select league (default is spl)");
    }
}