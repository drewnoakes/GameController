package leagues;

/**
 * Models rules specific to the Adult-Size Humanoid league.
 *
 * @author Michel-Zen
 */
public class HLAdult extends HL
{
    @Override
    public int getTeamSize()
    {
        return 2; // 1 player, 1 sub
    }

    @Override
    public int getRobotsPlaying()
    {
        return 1;
    }

    @Override
    public boolean isGameStartedWithPenaltyShots()
    {
        return true;
    }

    @Override
    public int getPenaltyShotDurationSeconds()
    {
        return (int)(2.5 * 60);
    }
}