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
    public int getNormalHalfDurationSeconds()
    {
        // Each half is ten minutes
        return 10*60;
    }

    @Override
    public int getReadyPlayModeDurationSeconds()
    {
        return 30;
    }

    @Override
    public int getHalfTimeDurationSeconds()
    {
        // Half time is five minutes
        return 5*60;
    }

    @Override
    public boolean isKickOffTeamChoosableAtStartOfGame()
    {
        return true;
    }

    @Override
    public int getKickOffDurationSeconds()
    {
        return 10;
    }

    @Override
    public int getMinDurationBeforeStuckGameAllowed()
    {
        return 30;
    }

    @Override
    public boolean isOvertimeAllowed()
    {
        return true;
    }

    @Override
    public int getOvertimeHalfDurationSeconds()
    {
        return 5*60;
    }

    @Override
    public boolean isGameStartedWithPenaltyShots()
    {
        return false;
    }

    @Override
    public int getDurationBeforePenaltyShootoutsStart()
    {
        return 0;
    }

    @Override
    public int getPenaltyShotDurationSeconds()
    {
        return 60;
    }

    @Override
    public boolean arePenaltyShotRetriesAllowed()
    {
        return true;
    }

    @Override
    public int getPenaltyShotDurationSecondsInSuddenDeath()
    {
        return 2*60; // does not matter
    }

    @Override
    public int getNumberOfPenaltyShotsInNormalGame()
    {
        return 5;
    }

    @Override
    public int getNumberOfPenaltyShotsInPlayOffGame()
    {
        return 5;
    }

    @Override
    public boolean arePenaltiesClearedDuringStoppages()
    {
        return false;
    }

    @Override
    public int getTimeoutDurationSeconds()
    {
        return 2*60;
    }

    @Override
    public boolean isKickOffGivenToOpponentAfterTimeout()
    {
        return false;
    }

    @Override
    public int getRefereeTimeoutDurationSeconds()
    {
        throw new RuntimeException("Referee timeouts are not used in the HL.");
    }

    @Override
    public boolean isRefereeTimeoutAvailable()
    {
        return false;
    }

    @Override
    public boolean isTeamAllowedOnlyOneTimeoutPerHalf()
    {
        return true;
    }

    @Override
    public int[] getPushesToEjection()
    {
        throw new RuntimeException("Pushes are not used in the HL.");
    }

    @Override
    public boolean isCoachAvailable()
    {
        return false;
    }

    @Override
    public boolean isStoppageTimeAllowed()
    {
        return false;
    }

    @Override
    public boolean isLegacyGameStateVersion7Broadcast()
    {
        return true;
    }

    @Override
    public boolean isLegacyGameStateVersion8Broadcast()
    {
        return true;
    }

    @Override
    public boolean isDropInPlayerMode()
    {
        return false;
    }
}