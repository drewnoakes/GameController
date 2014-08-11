package common;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.Config;
import controller.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Logs to files and stderr.
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