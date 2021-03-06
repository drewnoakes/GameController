package common;

import common.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * Ensures that only one instance of an application is alive on the computer.
 *
 * @author Marcel Steinbeck
 */
public class ApplicationLock
{
    private final File lockFile;
    private FileLock lock;
    private FileChannel lockChannel;
    private FileOutputStream lockStream;

    /**
     * Creates a new ApplicationLock instance.
     * Every application instance gets it own key.
     *
     * @param key the key of the lock
     */
    public ApplicationLock(@NotNull String key)
    {
        // ensure the path ends with system dependent file-separator
        String tmp_dir = System.getProperty("java.io.tmpdir");
        if (!tmp_dir.endsWith(System.getProperty("file.separator"))) {
            tmp_dir += System.getProperty("file.separator");
        }

        // Create a lock file in a system temporary directory
        lockFile = new File(tmp_dir + key + ".app_lock");
    }

    /**
     * Acquires a the lock.
     *
     * @return true if no other application acquired a lock before, false otherwise
     * @throws IOException if an error occurred while trying to lock
     */
    public boolean acquire() throws IOException
    {
        lockStream = new FileOutputStream(lockFile);
        lockChannel = lockStream.getChannel();
        lock = lockChannel.tryLock();
        return null != lock;
    }

    /**
     * Releases the lock
     *
     * @throws IOException if an error occurred while trying to unlock
     */
    public void release() throws IOException
    {
        if (lock.isValid()) {
            lock.release();
        }
        if (lockStream != null) {
            lockStream.close();
        }
        if (lockChannel.isOpen()) {
            lockChannel.close();
        }
    }
}