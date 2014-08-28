package controller;

import common.ApplicationLock;
import common.Interval;
import common.Log;
import controller.action.ActionBoard;
import controller.action.ActionTrigger;
import controller.action.net.SPLCoachMessageReceived;
import controller.net.*;
import controller.net.protocol.*;
import controller.ui.ControllerUI;
import controller.ui.GameOptionsUI;
import controller.ui.KeyboardListener;
import data.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Random;
import java.util.regex.Pattern;
import javax.swing.*;

/**
 * Main class for the game controller application.
 * <p/>
 * Manages command line arguments and the lifetime of all sub-components.
 *
 * @author Michel Bartsch
 * @author Drew Noakes https://drewnoakes.com
 */
public class Main
{
    /** The version of the GameController. Only used in log file. */
    public static final String version = "GC2 1.3";
    
    private static final Pattern IPV4_PATTERN = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");

    private static final String DEFAULT_BROADCAST = "255.255.255.255";

    @SuppressWarnings("FieldCanBeLocal")
    private static ApplicationLock applicationLock;

    private Main() {}

    /**
     * The program starts here.
     * 
     * @param args the array of command line arguments provided to the executable
     */
    public static void main(String[] args)
    {
        ensureSingleInstanceRunning();

        int gameControllerId = new Random().nextInt();

        // Process command line input
        GameOptions options = parseCommandLineArguments(args);

        while (true) {
            // Start a new log file for each game
            Log.initialise();

            // Show UI to configure the starting parameters
            options = GameOptionsUI.configure(options);

            runGame(new Game(options, gameControllerId));

            try {
                Log.close();
            } catch (IOException e) {
                Log.error("Error while trying to close the log.");
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void runGame(final Game game)
    {
        final RobotWatcher robotWatcher = new RobotWatcher(game.league());
        final GameStateSender gameStateSender;
        final MessageReceiver robotMessageReceiver;
        final MultipleInstanceWatcher multipleInstanceWatcher;
        MessageReceiver splReceiver = null;

        try {
            gameStateSender = new GameStateSender(game, game.broadcastAddress());
            gameStateSender.addProtocol(new GameStateProtocol9(game.league(), game.gameControllerId()));
            if (game.settings().supportGameStateVersion8)
                gameStateSender.addProtocol(new GameStateProtocol8(game.league()));
            if (game.settings().supportGameStateVersion7)
                gameStateSender.addProtocol(new GameStateProtocol7(game.league()));
            gameStateSender.start();

            robotMessageReceiver = new MessageReceiver<RobotMessage>(
                    Config.ROBOT_STATUS_PORT,
                    500,
                    new MessageHandler<RobotMessage>()
                    {
                        @Override
                        public void handle(RobotMessage message) { robotWatcher.update(game, message); }
                    });
            robotMessageReceiver.addProtocol(new RobotStatusProtocol1());
            robotMessageReceiver.addProtocol(new RobotStatusProtocol2());
            robotMessageReceiver.start();

            multipleInstanceWatcher = new MultipleInstanceWatcher(game.league(), game.gameControllerId());

            if (game.league().isSPLFamily() && game.settings().isCoachAvailable) {
                splReceiver = new MessageReceiver<SPLCoachMessage>(
                        Config.SPL_COACH_MESSAGE_PORT,
                        500,
                        new MessageHandler<SPLCoachMessage>()
                        {
                            @Override
                            public void handle(SPLCoachMessage message)
                            {
                                robotWatcher.updateCoach(game, message.teamNumber);
                                game.apply(new SPLCoachMessageReceived(message), ActionTrigger.Network);
                            }
                        });
                splReceiver.addProtocol(new SPLCoachProtocol2(game.teams()));
                splReceiver.start();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error while setting up GameController on port: " + Config.ROBOT_STATUS_PORT + ".",
                    "Error on configured port",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
            return;
        }

        Log.toFile("League = " + game.league().getName());
        Log.toFile("Play-off = " + game.isPlayOff());
        Log.toFile("Auto color change = " + game.changeColoursEachPeriod());
        Log.toFile("Using broadcast address " + game.broadcastAddress());

        ActionBoard.initalise(game.league());

        ControllerUI ui = new ControllerUI(game, game.isFullScreen(), robotWatcher, multipleInstanceWatcher);

        KeyboardListener keyboardListener = new KeyboardListener(game);

        // Execute the clock until shutdown is requested
        Interval interval = new Interval(500);
        while (!game.isShutdownRequested()) {
            game.apply(ActionBoard.clock, ActionTrigger.Clock);
            try {
                interval.sleep();
            } catch (InterruptedException e) {
                return;
            }
        }

        // Stop game

        Log.toFile("Stopping game");

        keyboardListener.close();
        ui.close();

        try {
            gameStateSender.stop();
            robotMessageReceiver.stop();
            multipleInstanceWatcher.stop();
            if (splReceiver != null)
                splReceiver.stop();
        } catch (InterruptedException e) {
            Log.error("Waiting for threads to shutdown was interrupted.");
        }
    }

    private static void ensureSingleInstanceRunning()
    {
        applicationLock = new ApplicationLock("GameController");
        try {
            if (!applicationLock.acquire()) {
                JOptionPane.showMessageDialog(null,
                        "An instance is already running on this computer.",
                        "RoboCup Game Controller",
                        JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error while trying to acquire the application lock.",
                    "IOError",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
    }

    private static GameOptions parseCommandLineArguments(String[] args)
    {
        String broadcastAddress = DEFAULT_BROADCAST;
        boolean isFullScreen = true;
        TeamColor initialKickOffColor = null;
        Boolean isPlayOff = null;
        League league = League.getAllLeagues().iterator().next();
        int teamNumberBlue = 0;
        int teamNumberRed = 0;

        for (int i = 0; i < args.length; i++) {
            boolean hasAnotherArg = args.length > i + 1;
            boolean hasAnotherTwoArgs = args.length > i + 2;
            // Dispatch based on argument string.
            // This would be nicer if using Java 1.7 which supports switching on strings.
            if (args[i].equals("-b") || args[i].equals("--broadcast")) {
                if (hasAnotherArg && IPV4_PATTERN.matcher(args[++i]).matches()) {
                    broadcastAddress = args[i];
                    continue;
                }
            } else if (args[i].equals("-l") || args[i].equals("--league")) {
                if (hasAnotherArg) {
                    league = League.findByDirectoryName(args[++i]);
                    if (league != null)
                        continue;
                }
            } else if (args[i].equals("-t") || args[i].equals("--teams")) {
                if (hasAnotherTwoArgs) {
                    try {
                        teamNumberBlue = Integer.parseInt(args[++i]);
                        teamNumberRed = Integer.parseInt(args[++i]);
                        continue;
                    } catch (NumberFormatException e) {}
                }
            } else if (args[i].equals("-k") || args[i].equals("--kickoff")) {
                if (hasAnotherArg) {
                    String colour = args[++i];
                    if (colour.equals("blue")) {
                        initialKickOffColor = TeamColor.Blue;
                        continue;
                    } else if (colour.equals("red")) {
                        initialKickOffColor = TeamColor.Red;
                        continue;
                    }
                }
            } else if (args[i].equals("--knockout") || args[i].equals("--playoff")) {
                if (hasAnotherArg) {
                    String val = args[++i];
                    if (val.equals("yes") || val.equals("true") || val.equals("1")) {
                        isPlayOff = true;
                        continue;
                    } else if (val.equals("no") || val.equals("false") || val.equals("0")) {
                        isPlayOff = false;
                        continue;
                    }
                }
            } else if (args[i].equals("-w") || args[i].equals("--window")) {
                isFullScreen = false;
                continue;
            }

            printUsage();
            System.exit(1);
        }

        if (!league.hasTeamNumber(teamNumberBlue) || !league.hasTeamNumber(teamNumberRed)) {
            System.err.println("Invalid team number(s) for league");
            System.exit(1);
        }

        Team teamBlue = league.getTeam(teamNumberBlue);
        Team teamRed = league.getTeam(teamNumberRed);
        UIOrientation orientation = new UIOrientation();
        Pair<Team> teams = new Pair<Team>(orientation, teamBlue, teamRed);

        boolean changeColoursEachPeriod = false; // TODO do we need an argument for this?

        return new GameOptions(
                broadcastAddress, isFullScreen, league, isPlayOff, orientation,
                teams, initialKickOffColor, changeColoursEachPeriod);
    }

    private static void printUsage()
    {
        final String HELP_TEMPLATE = "Usage: java -jar GameController.jar {options}"
                + "\n  (-h | --help)                   show this help message"
                + "\n  (-b | --broadcast) <address>    set broadcast ip address (default is %s)"
                + "\n  (-t | --teams) <blue> <red>     set team numbers"
                + "\n  (-k | --kickoff) <colour>       set kickoff team colour ('blue' or 'red')"
                + "\n  (-l | --league) %s%sselect league (default is %s)"
                + "\n  (-w | --window)                 set window mode (default is fullscreen)"
                + "\n  (--knockout | --playoff) <val>  set whether knockout/playoff game (yes/no)"
                + "\n";

        Collection<League> allLeagues = League.getAllLeagues();
        assert(allLeagues.size() != 0);

        StringBuilder leagues = new StringBuilder("(");
        for (League league : allLeagues) {
            if (leagues.length() != 1)
                leagues.append(" | ");
            leagues.append(league.getDirectoryName());
        }
        leagues.append(')');
        System.out.printf(HELP_TEMPLATE,
                DEFAULT_BROADCAST,
                leagues,
                leagues.length() < 17
                    ? "                ".substring(leagues.length())
                    : "\n                                  ",
                allLeagues.iterator().next().getDirectoryName());
    }
}
