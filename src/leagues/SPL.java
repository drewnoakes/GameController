package leagues;

/**
 * Models rules specific to the Standard Platform League.
 *
 * @author Michel-Zen
 */
public class SPL implements LeagueRules
{
    @Override
    public int getTeamSize()
    {
        return 6; // 5 players, 1 sub
    }

    @Override
    public int getRobotsPlaying()
    {
        return 5;
    }

    @Override
    public boolean isChangeColoursEachPeriod()
    {
        return false;
    }

    @Override
    public boolean isPlayOffTimeStop()
    {
        return true;
    }

    @Override
    public int getHalfTime()
    {
        return 10*60;
    }

    @Override
    public int getReadyTime()
    {
        return 45;
    }

    @Override
    public int getPauseTime()
    {
        return 10*60;
    }

    @Override
    public boolean isKickoffChoice()
    {
        return false;
    }

    @Override
    public int getKickoffTime()
    {
        return 10;
    }

    @Override
    public int getMinDurationBeforeStuck()
    {
        return 15;
    }

    @Override
    public boolean isOvertime()
    {
        return false;
    }

    @Override
    public int getOvertimeTime()
    {
        return 0;
    }

    @Override
    public boolean isStartWithPenalty()
    {
        return false;
    }

    @Override
    public int getPausePenaltyShootOutTime()
    {
        return 5*60;
    }

    @Override
    public int getPenaltyShotTime()
    {
        return 60;
    }

    @Override
    public boolean isPenaltyShotRetries()
    {
        return false;
    }

    @Override
    public boolean isSuddenDeath()
    {
        return true;
    }

    @Override
    public int getPenaltyShotTimeSuddenDeath()
    {
        return 2*60;
    }

    @Override
    public int getNumberOfPenaltyShotsShort()
    {
        return 3;
    }

    @Override
    public int getNumberOfPenaltyShotsLong()
    {
        return 5;
    }

    @Override
    public boolean isReturnRobotsInGameStoppages()
    {
        return true;
    }

    @Override
    public int getTimeOutTime()
    {
        return 5*60;
    }

    @Override
    public boolean isGiveOpponentKickOffOnTimeOut()
    {
        return true;
    }

    @Override
    public int getRefereeTimeout()
    {
        return 10*60;
    }

    @Override
    public boolean isRefereeTimeoutAvailable()
    {
        return true;
    }

    @Override
    public boolean isTimeOutPerHalf()
    {
        return false;
    }

    @Override
    public int[] getPushesToEjection()
    {
        return new int[] {4, 6, 8, 10, 12};
    }

    @Override
    public boolean isCoachAvailable()
    {
        return true;
    }

    @Override
    public boolean isLostTime()
    {
        return true;
    }

    @Override
    public boolean isSupportGameStateVersion7()
    {
        return false;
    }

    @Override
    public boolean isSupportGameStateVersion8()
    {
        return false;
    }

    @Override
    public boolean isDropInPlayerMode()
    {
        return false;
    }
}