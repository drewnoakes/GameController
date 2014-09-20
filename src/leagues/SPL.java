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
        return 6; // 5 players, 1 sub (coach counted separately)
    }

    @Override
    public int getRobotsPlaying()
    {
        return 5;
    }

    @Override
    public boolean isPlayOffTimeStop()
    {
        return true;
    }

    @Override
    public int getNormalHalfDurationSeconds()
    {
        // Each half is ten minutes
        return 10*60;
    }

    @Override
    public int getReadyPlayModeDurationSeconds()
    {
        return 45;
    }

    @Override
    public int getHalfTimeDurationSeconds()
    {
        // Half time is ten minutes
        return 10*60;
    }

    @Override
    public boolean isKickOffTeamChoosableAtStartOfGame()
    {
        return false;
    }

    @Override
    public int getKickOffDurationSeconds()
    {
        return 10;
    }

    @Override
    public int getMinDurationBeforeStuckGameAllowed()
    {
        return 15;
    }

    @Override
    public boolean isOvertimeAllowed()
    {
        return false;
    }

    @Override
    public int getOvertimeHalfDurationSeconds()
    {
        throw new RuntimeException("Overtime is not used in the SPL.");
    }

    @Override
    public boolean isGameStartedWithPenaltyShots()
    {
        return false;
    }

    @Override
    public int getDurationBeforePenaltyShootoutsStart()
    {
        return 5*60;
    }

    @Override
    public int getPenaltyShotDurationSeconds()
    {
        return 60;
    }

    @Override
    public boolean arePenaltyShotRetriesAllowed()
    {
        return false;
    }

    @Override
    public int getPenaltyShotDurationSecondsInSuddenDeath()
    {
        return 2*60;
    }

    @Override
    public int getNumberOfPenaltyShotsInNormalGame()
    {
        return 3;
    }

    @Override
    public int getNumberOfPenaltyShotsInPlayOffGame()
    {
        return 5;
    }

    @Override
    public boolean arePenaltiesClearedDuringStoppages()
    {
        return true;
    }

    @Override
    public int getTimeoutDurationSeconds()
    {
        return 5*60;
    }

    @Override
    public boolean isKickOffGivenToOpponentAfterTimeout()
    {
        return true;
    }

    @Override
    public int getRefereeTimeoutDurationSeconds()
    {
        return 10*60;
    }

    @Override
    public boolean isRefereeTimeoutAvailable()
    {
        return true;
    }

    @Override
    public boolean isTeamAllowedOnlyOneTimeoutPerHalf()
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
    public boolean isStoppageTimeAllowed()
    {
        return true;
    }

    @Override
    public boolean isLegacyGameStateVersion7Broadcast()
    {
        return false;
    }

    @Override
    public boolean isLegacyGameStateVersion8Broadcast()
    {
        return false;
    }

    @Override
    public boolean isDropInPlayerMode()
    {
        return false;
    }
}