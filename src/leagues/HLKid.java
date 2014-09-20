package leagues;

/**
 * Models rules specific to the Kid-Size Humanoid League.
 */
public class HLKid extends HL
{
    @Override
    public int getTeamSize()
    {
        return 6; // 4 players, 2 subs
    }

    @Override
    public int getRobotsPlaying()
    {
        return 4;
    }
}
