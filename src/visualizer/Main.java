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
 * The game-state-visualizer-program starts in this class.
 * The main components are initialised here.
 *
 * @author Michel Bartsch
 */
public class Main
{        
    private static final String HELP = "Usage: java -jar GameController.jar <options>"
            + "\n  [-h | --help]                   display help"
            + "\n  [-l | --league] <league-dir>    given league is preselected";
    private static final String COMMAND_HELP = "--help";
    private static final String COMMAND_HELP_SHORT = "-h";
    private final static String COMMAND_LEAGUE = "--league";
    private final static String COMMAND_LEAGUE_SHORT = "-l";
    
    private static MessageReceiver<GameStateSnapshot> gameStateListener;

    private Main() {}

    /**
     * The program starts here.
     * 
     * @param args  This is ignored.
     */
    public static void main(String[] args)
    {
        //commands
        if ((args.length > 0)
                && ((args[0].equalsIgnoreCase(COMMAND_HELP_SHORT))
                  || (args[0].equalsIgnoreCase(COMMAND_HELP))) ) {
            System.out.println(HELP);
            System.exit(0);
        }
        if ((args.length >= 2) && ((args[0].equals(COMMAND_LEAGUE_SHORT)) || (args[0].equals(COMMAND_LEAGUE)))) {
            for (int i=0; i < Rules.LEAGUES.length; i++) {
                if (Rules.LEAGUES[i].leagueDirectory.equals(args[1])) {
                    Rules.league = Rules.LEAGUES[i];
                    break;
                }
            }
        }
        
        final GUI gui = new GUI();
        new KeyboardListener(gui);
        try {
            gameStateListener = new MessageReceiver<GameStateSnapshot>(
                    Config.GAME_STATE_PORT,
                    500,
                    new MessageHandler<GameStateSnapshot>()
                    {
                        @Override
                        public void handle(GameStateSnapshot message) { gui.update(message); }
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
}