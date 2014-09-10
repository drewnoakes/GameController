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
 * Users must call {@link Log#initialise} before calling {@link Log#toFile} in order to create
 * a new log file.
 *
 * When done, call {@link Log#close} to release the log file.
 *
 * @author Michel Bartsch
 * @author Drew Noakes https://drewnoakes.com
 */
public class Log
{
    /** The writer for normal log messages. */
    @Nullable
    private static FileWriter file;
    /** The writer for error messages. */
    @Nullable
    private static FileWriter errorFile;

    /** The format of timestamps. */
    public static final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy.M.dd-kk.mm.ss");
    
    private Log() {}
    
    /**
     * Initialises the log, creating the output file. Must be called before using this class.
     */
    public synchronized static void initialise()
    {
        if (file != null) {
            throw new IllegalStateException("Log already initialized");
        }

        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-S");
        final String timestamp = df.format(new Date(System.currentTimeMillis()));
        final File logDir = new File(Config.LOG_DIRECTORY);
        final String path;
        if (!logDir.exists() && !logDir.mkdirs()) {
            // Attempting to create the log directory failed. Just log in the current folder.
            path = "log_" + timestamp + ".txt";
        } else {
            // Log directory existed, or was successfully created
            File logFile = new File(logDir, "log_" + timestamp +".txt");
            path = logFile.getPath();
        }

        try {
            file = new FileWriter(new File(path));
        } catch (IOException e) {
            error("cannot write to logfile " + path);
        }

        toFile(Main.version);
    }
    
    /**
     * Appends a line to the log file. Prepends the string with a timestamp.
     *
     * @param s the string to be written in the file.
     */
    public static void toFile(@NotNull String s)
    {
        assert(file != null);

        try {
            file.write(createTimestamp() + ": " + s + '\n');
            file.flush();
        } catch (IOException e) {
            error("cannot write to logfile!");
        }
    }

    /**
     * Appends a line to the error file. Prepends the string with a timestamp.
     *
     * Creates the error log file if it does not already exist.
     * 
     * This can be used before {@link Log#initialise} is been called.
     * 
     * @param s the string to be written to the error log file.
     */
    public static void error(@NotNull String s)
    {
        System.err.println(s);
        try {
            if (errorFile == null) {
                errorFile = new FileWriter(new File("error.txt"));
            }
            errorFile.write(createTimestamp() + ": " + s + '\n');
            errorFile.flush();
        } catch (IOException e) {
             System.err.println("cannot write to error file!");
        }
    }

    /** Produces a uniformly formatted string representing the current time to be prepended to log file entries. */
    @NotNull
    private static String createTimestamp()
    {
        return timestampFormat.format(new Date(System.currentTimeMillis()));
    }

    /**
     * Closes the Log file(s).
     *
     * @throws IOException if an error occurred while trying to close the FileWriters
     */
    public static void close() throws IOException
    {
        if (errorFile != null) {
            errorFile.close();
            errorFile = null;
        }

        assert(file != null);
        file.close();
        file = null;
    }
}