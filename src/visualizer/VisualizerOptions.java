package visualizer;

import common.annotations.NotNull;
import data.League;

/**
 * Immutable options used when running the visualiser.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class VisualizerOptions
{
    private final League league;

    public VisualizerOptions(@NotNull League league)
    {
        this.league = league;
    }

    @NotNull
    public League getLeague()
    {
        return league;
    }
}
