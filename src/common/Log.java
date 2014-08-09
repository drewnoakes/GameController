package common;

import controller.EventHandler;
import controller.Main;
import data.GameState;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;


/**
 * Used for both logging to files, and tracking the history of states in the timeline.
 *
 * A new log file is created every time the Log is initialised.
 *
 * This class is a singleton!
 *
 * @author Michel Bartsch
 */
public class Log
{
    /** The instance of the singleton. */
    private static Log instance;
    
    /** The writer for normal log messages. */
    private FileWriter file;
    /** The writer for error messages. */
    private FileWriter errorFile;
    /** The timeline. */
    private final LinkedList<GameState> states = new LinkedList<GameState>();

    /** The format of timestamps. */
    public static final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy.M.dd-kk.mm.ss");
    
    private Log() {}
    
    /**
     * Initialises the log, creating the output file. Must be called before using this class.
     * 
     * @param path the path of the directory into which the log file should be created.
     */
    public synchronized static void init(String path)
    {
        if (instance != null) {
            throw new IllegalStateException("logger already initialized");
        }

        instance = new Log();

        try {
            instance.file = new FileWriter(new File(path));
        } catch (IOException e) {
            error("cannot write to logfile "+path);
        }

        toFile(Main.version);
    }
    
    /**
     * Appends a line to the log file. Prepends the string with a timestamp.
     *
     * @param s the string to be written in the file.
     */
    public static void toFile(String s)
    {
        assert(instance != null);
        try {
            instance.file.write(createTimestamp() + ": " + s + '\n');
            instance.file.flush();
        } catch (IOException e) {
            error("cannot write to logfile!");
        }
    }
    
    /**
     * Puts a copy of the given state into the timeline, attaching the message
     * to it and writing it to the file using toFile method.
     * This should be used at the very end of all actions that are meant to be
     * in the timeline.
     * 
     * @param state the current state that have just been changed and should
     *              go into the timeline.
     * @param message a message describing what happened to the state.
     */
    public static void state(GameState state, String message)
    {
        assert(instance != null);

        // Make a clone of the mutable state object
        GameState stateClone = (GameState)state.clone();

        stateClone.message = message;
        instance.states.add(stateClone);
        toFile(message);
    }
    
    /**
     * Reverts the game state to some prior position in the timeline. Supports 'undo'.
     *
     * If a game state change is undone, the time when it was left is restored.
     * Thereby, there whole remaining log is moved into the new time frame.
     * 
     * @param states the number of states to go back
     * @return the message of the state reverted to
     */
    public static String goBack(int states)
    {
        assert(instance != null);
        if (states >= instance.states.size()) {
            states = instance.states.size()-1;
        }
        
        long laterTimestamp = instance.states.getLast().whenCurrentPlayModeBegan;
        long earlierTimestamp = 0;
        long timeInCurrentState = instance.states.getLast().getTime() - laterTimestamp;
        for (int i=0; i<states; i++) {
            earlierTimestamp = instance.states.getLast().whenCurrentPlayModeBegan;
            instance.states.removeLast();
        }
        if (laterTimestamp != instance.states.getLast().whenCurrentPlayModeBegan) {
            long timeOffset = laterTimestamp - earlierTimestamp + timeInCurrentState;
            for (GameState state : instance.states) {
                state.whenCurrentPlayModeBegan += timeOffset;
            }
        }
        GameState state = (GameState) instance.states.getLast().clone();
        EventHandler.getInstance().state = state;
        return state.message;
    }
    
    /**
     * Gets an array of the last N messages of states in the timeline.
     * 
     * @param states the number of states back you want to have the messages for
     * 
     * @return the messages attached to the states, beginning with the latest as an
     *         arrays of length equals to <code>states</code>.
     */
    public static String[] getLast(int states)
    {
        assert(instance != null);
        String[] out = new String[states];
        for (int i=0; i<states; i++) {
            if (instance.states.size()-1-i >= 0) {
                out[i] = instance.states.get(instance.states.size()-1-i).message;
            } else {
                out[i] = "";
            }
        }
        return out;
    }

    /**
     * Appends a line to the error file. Prepends the string with a timestamp.
     *
     * Creates the error log file if it does not already exist.
     * 
     * This can be used before initialising the log!
     * 
     * @param s the string to be written to the error log file.
     */
    public static void error(String s)
    {
        // TODO documentation says this function can be called before initialisation, but that's not true
        assert(instance != null);
        System.err.println(s);
        try {
            if (instance.errorFile == null) {
                instance.errorFile = new FileWriter(new File("error.txt"));
            }
            instance.errorFile.write(createTimestamp() + ": " + s + '\n');
            instance.errorFile.flush();
        } catch (IOException e) {
             System.err.println("cannot write to error file!");
        }
    }

    /** Produces a uniformly formatted string representing the current time to be prepended to log file entries. */
    private static String createTimestamp()
    {
        return timestampFormat.format(new Date(System.currentTimeMillis()));
    }

    /**
     * Closes the Log file(s) and tears down the singleton.
     *
     * @throws IOException if an error occurred while trying to close the FileWriters
     */
    public static void close() throws IOException
    {
        if (instance.errorFile != null) {
            instance.errorFile.close();
        }
        instance.file.close();

        instance = null;
    }
}