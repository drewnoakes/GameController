package data;

import common.annotations.NotNull;

public enum League
{
    SPL("SPL", "spl"),

    SPLDropIn("SPL Drop-in", "spl_dropin"),

    HLKid("HL Kid", "hl_kid"),

    HLTeen("HL Teen", "hl_teen"),

    HLAdult("HL Adult", "hl_adult");

    private final String name;
    private final String directoryName;

    League(@NotNull String name, @NotNull String directoryName)
    {
        this.name = name;
        this.directoryName = directoryName;
    }

    @NotNull
    public String getName()
    {
        return name;
    }

    @NotNull
    public String getDirectoryName()
    {
        return directoryName;
    }
}
