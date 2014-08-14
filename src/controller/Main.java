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
import controller.ui.KeyboardListener;
import controller.ui.StartUI;
import data.*;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.swing.*;


/**
 * Main class for the game controller application.
 *
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

    private Main() {}

    /**
     * The program starts here.
     * 
     * @param args the array of command line arguments provided to the executable
     */
    public static void main(String[] args)
    {
        ensureSingleInstanceRunning();

        while (true) {
            runGameController(args);
        }
    }

    @SuppressWarnings("unchecked")
    private static void runGameController(String[] args)
    {
        // Process command line input
        StartOptions options = parseCommandLineArguments(args);

        // Allow the user to specify the starting parameters for the game
        StartUI.showDialog(options);

        final Game game = new Game(options);

        final RobotWatcher robotWatcher = new RobotWatcher(options.league);
        final GameStateSender gameStateSender;
        final MessageReceiver robotMessageReceiver;
        MessageReceiver splReceiver = null;

        try {
            gameStateSender = new GameStateSender(game, options.broadcastAddress);
            gameStateSender.addProtocol(new GameStateProtocol9(game.league()));
            if (game.settings().supportGameStateVersion8)
                gameStateSender.addProtocol(new GameStateProtocol8(game.league()));
            if (game.settings().supportGameStateVersion7)
                gameStateSender.addProtocol(new GameStateProtocol7(game.league()));
            gameStateSender.start();

            robotMessageReceiver = new MessageReceiver<RobotMessage>(
                    game.options().league,
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

            if (game.options().league.isSPLFamily() && game.settings().isCoachAvailable) {
                splReceiver = new MessageReceiver<SPLCoachMessage>(
                        game.options().league,
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
                splReceiver.addProtocol(new SPLCoachProtocol2(options.teamNumberBlue, options.teamNumberRed));
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

        Log.initialise();

        Log.toFile("League = " + options.league.getName());
        Log.toFile("Play-off = " + options.playOff);
        Log.toFile("Auto color change = " + options.colorChangeAuto);
        Log.toFile("Using broadcast address " + options.broadcastAddress);

        ActionBoard.initalise(options.league);

        ControllerUI ui = new ControllerUI(game, options.fullScreenMode, robotWatcher);

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
            if (splReceiver != null)
                splReceiver.stop();
        } catch (InterruptedException e) {
            Log.error("Waiting for threads to shutdown was interrupted.");
        }

        try {
            Log.close();
        } catch (IOException e) {
            Log.error("Error while trying to close the log.");
        }
    }

    private static void ensureSingleInstanceRunning()
    {
        final ApplicationLock applicationLock = new ApplicationLock("GameController");
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

    private static StartOptions parseCommandLineArguments(String[] args)
    {
        StartOptions options = new StartOptions();
        options.broadcastAddress = DEFAULT_BROADCAST;
        options.fullScreenMode = true;
        options.initialKickOffTeam = null;
        options.playOff = null;
        options.league = League.getAllLeagues()[0];

        for (int i = 0; i < args.length; i++) {
            boolean hasAnotherArg = args.length > i + 1;
            boolean hasAnotherTwoArgs = args.length > i + 2;
            // Dispatch based on argument string.
            // This would be nicer if using Java 1.7 which supports switching on strings.
            if (args[i].equals("-b") || args[i].equals("--broadcast")) {
                if (hasAnotherArg && IPV4_PATTERN.matcher(args[++i]).matches()) {
                    options.broadcastAddress = args[i];
                    continue;
                }
            } else if (args[i].equals("-l") || args[i].equals("--league")) {
                if (hasAnotherArg) {
                    options.league = League.findByDirectoryName(args[++i]);
                    if (options.league != null)
                        continue;
                }
            } else if (args[i].equals("-t") || args[i].equals("--teams")) {
                if (hasAnotherTwoArgs) {
                    options.teamNumberBlue = Byte.parseByte(args[++i]);
                    options.teamNumberRed = Byte.parseByte(args[++i]);
                    continue;
                }
            } else if (args[i].equals("-k") || args[i].equals("--kickoff")) {
                if (hasAnotherArg) {
                    String colour = args[++i];
                    if (colour.equals("blue")) {
                        options.initialKickOffTeam = TeamColor.Blue;
                        continue;
                    } else if (colour.equals("red")) {
                        options.initialKickOffTeam = TeamColor.Red;
                        continue;
                    }
                }
            } else if (args[i].equals("--knockout") || args[i].equals("--playoff")) {
                if (hasAnotherArg) {
                    String val = args[++i];
                    if (val.equals("yes") || val.equals("true") || val.equals("1")) {
                        options.playOff = true;
                        continue;
                    } else if (val.equals("no") || val.equals("false") || val.equals("0")) {
                        options.playOff = false;
                        continue;
                    }
                }
            } else if (args[i].equals("-w") || args[i].equals("--window")) {
                options.fullScreenMode = false;
                continue;
            }

            printUsage();
            System.exit(0);
        }

        return options;
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

        StringBuilder leagues = new StringBuilder();
        League[] allLeagues = League.getAllLeagues();
        if (allLeagues.length > 1) {
            leagues.append('(');
        }
        for (League league : allLeagues) {
            if (leagues.length() != 1)
                leagues.append(" | ");
            leagues.append(league.getDirectoryName());
        }
        if (allLeagues.length > 1) {
            leagues.append(')');
        }
        System.out.printf(HELP_TEMPLATE,
                DEFAULT_BROADCAST,
                leagues,
                leagues.length() < 17
                    ? "                ".substring(leagues.length())
                    : "\n                                  ",
                allLeagues[0].getDirectoryName());
    }
}
