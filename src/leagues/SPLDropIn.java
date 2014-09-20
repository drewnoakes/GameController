package leagues;

/**
 * Rules for drop-in games played in the Standard Platform League.
 *
 * @author Michel-Zen
 */
public class SPLDropIn extends SPL
{
    // Override some values from the SPL rules

    @Override
    public int getRobotsPlaying()
    {
        return 5;
    }

    @Override
    public int getTeamSize()
    {
        return 5;
    }

    @Override
    public boolean isCoachAvailable()
    {
        return false;
    }

    @Override
    public boolean isDropInPlayerMode()
    {
        return true;
    }

    @Override
    public int[] getPushesToEjection()
    {
        return new int[0];
    }
}