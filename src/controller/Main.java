package controller;

import common.ApplicationLock;
import common.Log;
import controller.action.ActionBoard;
import controller.net.*;
import controller.net.protocol.*;
import controller.ui.GCGUI;
import controller.ui.GUI;
import controller.ui.KeyboardListener;
import controller.ui.StartInput;
import data.GameState;
import data.TeamColor;
import rules.Rules;
import data.Teams;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public static final String version = "GC2 1.2";
    
    /** Relative directory of where logs are stored */
    private final static String LOG_DIRECTORY = "logs";
    
    private static Pattern IPV4_PATTERN = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");

    private static final String DEFAULT_BROADCAST = "255.255.255.255";

    private Main() {}

    /**
     * The program starts here.
     * 
     * @param args the array of command line arguments provided to the executable
     */
    public static void main(String[] args)
    {
        StartOptions options = parseCommandLineArguments(args);

        ApplicationLock applicationLock = getApplicationLock();

        //collect the start parameters and put them into the first data.
        StartInput.showDialog(options);

        GameState data = new GameState();
        data.team[0].teamNumber = options.teamNumberBlue;
        data.team[1].teamNumber = options.teamNumberRed;
        data.colorChangeAuto = options.colorChangeAuto;
        data.playoff = options.playOff;
        data.kickOffTeam = options.initialKickOffTeam;

        RobotMessageReceiver robotMessageReceiver;
        GameStateSender gameStateSender;
        SPLCoachMessageReceiver splReceiver = null;

        try {
            //sender
            gameStateSender = new GameStateSender(options.broadcastAddress);
            gameStateSender.addProtocol(new GameStateProtocol9());
            if (Rules.league.supportGameStateVersion8)
                gameStateSender.addProtocol(new GameStateProtocol8());
            if (Rules.league.supportGameStateVersion7)
                gameStateSender.addProtocol(new GameStateProtocol7());
            gameStateSender.send(data);
            gameStateSender.start();

            //event-handler
            EventHandler.initialise(gameStateSender);
            EventHandler.getInstance().data = data;

            robotMessageReceiver = new RobotMessageReceiver();
            robotMessageReceiver.addProtocol(new RobotStatusProtocol1());
            robotMessageReceiver.addProtocol(new RobotStatusProtocol2());
            robotMessageReceiver.start();

            if (Rules.league.isCoachAvailable) {
                splReceiver = new SPLCoachMessageReceiver();
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

        //log
        initialiseLogging();

        Log.toFile("League = "+Rules.league.leagueName);
        Log.toFile("Play-off = "+data.playoff);
        Log.toFile("Auto color change = "+data.colorChangeAuto);
        Log.toFile("Using broadcast address " + options.broadcastAddress);

        //ui
        ActionBoard.init();
        Log.state(data, Teams.getNames(false)[data.team[0].teamNumber] +" vs "+Teams.getNames(false)[data.team[1].teamNumber]);
        GCGUI gui = new GUI(options.fullScreenMode, data);
        new KeyboardListener();
        EventHandler.getInstance().setGUI(gui);
        gui.update(data);

        //clock runs until window is closed
        Clock.getInstance().start();

        // shutdown
        Log.toFile("Shutdown GameController");
        try {
            applicationLock.release();
        } catch (IOException e) {
            Log.error("Error while trying to release the application lock.");
        }

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

        System.exit(0);
    }

    private static ApplicationLock getApplicationLock()
    {
        final ApplicationLock applicationLock = new ApplicationLock("GameController");
        try {
            if (!applicationLock.acquire()) {
                JOptionPane.showMessageDialog(null,
                        "An instance of GameController already exists.",
                        "Multiple instances",
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
        return applicationLock;
    }

    private static void initialiseLogging()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-S");

        final File logDir = new File(LOG_DIRECTORY);
        if (!logDir.exists() && !logDir.mkdirs()) {
            Log.init("log_" + df.format(new Date(System.currentTimeMillis())) + ".txt");
        } else {
            final File logFile = new File(logDir,
                "log_"+df.format(new Date(System.currentTimeMillis()))+".txt");
            Log.init(logFile.getPath());
        }
    }

    private static StartOptions parseCommandLineArguments(String[] args)
    {
        StartOptions options = new StartOptions();
        options.broadcastAddress = DEFAULT_BROADCAST;
        options.fullScreenMode = true;
        options.initialKickOffTeam = null;
        options.playOff = null;

        Rules.league = Rules.LEAGUES[0];

        for (int i=0; i<args.length; i++) {
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
                if (hasAnotherArg && Rules.trySetLeague(args[++i]))
                    continue;
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
                + "\n  (-l | --league) %s%sselect league (default is spl)"
                + "\n  (-w | --window)                 set window mode (default is fullscreen)"
                + "\n  (--knockout | --playoff) <val>  set whether knockout/playoff game (yes/no)"
                + "\n";

        String leagues = "";
        for (Rules rules : Rules.LEAGUES) {
            leagues += (leagues.equals("") ? "" : " | ") + rules.leagueDirectory;
        }
        if (leagues.contains("|")) {
            leagues = "(" + leagues + ")";
        }
        System.out.printf(HELP_TEMPLATE,
                DEFAULT_BROADCAST,
                leagues,
                leagues.length() < 17
                    ? "                ".substring(leagues.length())
                    : "\n                                  ");
    }
}
