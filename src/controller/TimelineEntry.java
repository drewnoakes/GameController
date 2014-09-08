package controller;

import common.annotations.NotNull;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class TimelineEntry
{
    @NotNull
    private final GameState state;
    @NotNull private final String title;

    TimelineEntry(@NotNull GameState state, @NotNull String title)
    {
        this.title = title;
        this.state = state;
    }

    @NotNull
    public GameState getState()
    {
        return state;
    }

    @NotNull
    public String getTitle()
    {
        return title;
    }
}
