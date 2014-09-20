package leagues;

/**
 * Base class for rules specific to the Humanoid Leagues.
 *
 * @author Michel-Zen
 * @author Drew Noakes https://drewnoakes.com
 */
public abstract class HL implements LeagueRules
{
    //
    // Values that differ across all humanoid leagues.
    //

    @Override
    public abstract int getTeamSize();

    @Override
    public abstract int getRobotsPlaying();

    //
    // Sensible defaults for all Humanoid Leagues (Kid Size, Teen Size, Adult Size).
    // These may be overridden in subclasses however.
    //

    @Override
    public boolean isPlayOffTimeStop()
    {
        return false;
    }

    @Override
    public int getHalfTime()
    {
        return 10*60;
    }

    @Override
    public int getReadyTime()
    {
        return 30;
    }

    @Override
    public int getPauseTime()
    {
        return 5*60;
    }

    @Override
    public boolean isKickoffChoice()
    {
        return true;
    }

    @Override
    public int getKickoffTime()
    {
        return 10;
    }

    @Override
    public int getMinDurationBeforeStuck()
    {
        return 30;
    }

    @Override
    public boolean isOvertime()
    {
        return true;
    }

    @Override
    public int getOvertimeTime()
    {
        return 5*60;
    }

    @Override
    public boolean isStartWithPenalty()
    {
        return false;
    }

    @Override
    public int getPausePenaltyShootOutTime()
    {
        return 0;
    }

    @Override
    public int getPenaltyShotTime()
    {
        return 60;
    }

    @Override
    public boolean isPenaltyShotRetries()
    {
        return true;
    }

    @Override
    public int getPenaltyShotTimeSuddenDeath()
    {
        return 2*60; // does not matter
    }

    @Override
    public int getNumberOfPenaltyShotsShort()
    {
        return 5;
    }

    @Override
    public int getNumberOfPenaltyShotsLong()
    {
        return 5;
    }

    @Override
    public boolean isReturnRobotsInGameStoppages()
    {
        return false;
    }

    @Override
    public int getTimeOutTime()
    {
        return 2*60;
    }

    @Override
    public boolean isGiveOpponentKickOffOnTimeOut()
    {
        return false;
    }

    @Override
    public int getRefereeTimeout()
    {
        return 0;
    }

    @Override
    public boolean isRefereeTimeoutAvailable()
    {
        return false;
    }

    @Override
    public boolean isTimeOutPerHalf()
    {
        return true;
    }

    @Override
    public int[] getPushesToEjection()
    {
        return new int[0];
    }

    @Override
    public boolean isCoachAvailable()
    {
        return false;
    }

    @Override
    public boolean isLostTime()
    {
        return false;
    }

    @Override
    public boolean isSupportGameStateVersion7()
    {
        return true;
    }

    @Override
    public boolean isSupportGameStateVersion8()
    {
        return true;
    }

    @Override
    public boolean isDropInPlayerMode()
    {
        return false;
    }
}