package visualizer;

import common.Log;
import controller.Config;
import controller.net.MessageHandler;
import controller.net.MessageReceiver;
import controller.net.protocol.GameStateProtocol8;
import controller.net.protocol.GameStateProtocol9;
import data.GameStateSnapshot;
import rules.Rules;

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
        processCommandLineArguments(args);
        
        final GUI gui = new GUI();

        new KeyboardListener(gui);

        try {
            gameStateListener = new MessageReceiver<GameStateSnapshot>(
                    Config.GAME_STATE_PORT,
                    500,
                    new MessageHandler<GameStateSnapshot>()
                    {
                        @Override
                        public void handle(GameStateSnapshot state) { gui.update(state); }
                    });
            gameStateListener.addProtocol(new GameStateProtocol9());
            gameStateListener.addProtocol(new GameStateProtocol8());
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

    private static void processCommandLineArguments(String[] args)
    {
        if (args.length > 0 && (args[0].equals("-h") || args[0].equals("--help")) ) {
            printUsage();
            System.exit(0);
        }

        if (args.length >= 2 && (args[0].equals("-l") || args[0].equals("--league"))) {
            if (!Rules.trySetLeague(args[1])) {
                printUsage();
                System.exit(1);
            }
        }
    }

    private static void printUsage()
    {
        final String HELP = "Usage: java -jar GameController.jar <options>"
                + "\n  (-h | --help)                   show this help message"
                + "\n  (-l | --league) <league-dir>    select league (default is spl)";
        System.out.println(HELP);
    }
}