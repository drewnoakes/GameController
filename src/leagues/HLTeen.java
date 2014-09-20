package leagues;

/**
 * Models rules specific to Teen-Size Humanoid League.
 *
 * @author Michel-Zen
 */
public class HLTeen extends HL
{
    @Override
    public int getTeamSize()
    {
        return 4; // 2 players, 2 subs
    }

    @Override
    public int getRobotsPlaying()
    {
        return 2;
    }
}