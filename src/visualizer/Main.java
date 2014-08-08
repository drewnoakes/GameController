package visualizer;

import common.Log;
import data.Rules;

/**
 * @author Michel Bartsch
 * 
 * The game-state-visualizer-program starts in this class.
 * The main components are initialised here.
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
    
    private static Listener listener;

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
        
        GUI gui = new GUI();
        new KeyboardListener(gui);
        listener = new Listener(gui);
        listener.start();
    }
    
    /**
     * This should be called when the program is shutting down to close
     * sockets and finally exit.
     */
    public static void exit()
    {
        listener.interrupt();
        try {
            listener.join();
        } catch (InterruptedException e) {
            Log.error("Waiting for listener to shutdown was interrupted.");
        }
        System.exit(0);
    }
}