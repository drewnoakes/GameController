package data;

import common.Log;
import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.Config;
import leagues.*;

import java.io.*;
import java.util.*;

public enum League
{
    SPL((byte)0x01, "SPL", "spl", new SPL()),

    SPLDropIn((byte)0x02, "SPL Drop-in", "spl_dropin", new SPLDropIn()),

    HLKid((byte)0x11, "HL Kid", "hl_kid", new HLKid()),

    HLTeen((byte)0x12, "HL Teen", "hl_teen", new HLTeen()),

    HLAdult((byte)0x13, "HL Adult", "hl_adult", new HLAdult());

    private final static Collection<League> allLeagues;

    static
    {
        allLeagues = Collections.unmodifiableCollection(Arrays.asList(SPL, SPLDropIn, HLKid, HLTeen, HLAdult));
    }

    @NotNull
    public static Collection<League> getAllLeagues()
    {
        return allLeagues;
    }

    @Nullable
    public static League findByDirectoryName(@NotNull String directoryName)
    {
        for (League l : getAllLeagues())
            if (l.getDirectoryName().equals(directoryName))
                return l;
        return null;
    }

    @Nullable
    public static League findByName(@NotNull String name)
    {
        for (League l : getAllLeagues())
            if (l.getName().equals(name))
                return l;
        return null;
    }

    @Nullable
    public static League findByNumber(byte number)
    {
        for (League l : getAllLeagues())
            if (l.number() == number)
                return l;
        return null;
    }

    private final byte number;
    private final String name;
    private final String directoryName;
    private final LeagueSettings settings;
    private List<Team> teams;

    private League(byte number, @NotNull String name, @NotNull String directoryName, @NotNull LeagueSettings settings)
    {
        this.number = number;
        this.name = name;
        this.directoryName = directoryName;
        this.settings = settings;
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

    @NotNull
    public LeagueSettings settings()
    {
        return settings;
    }

    public byte number() { return number; }

    @NotNull
    public List<Team> teams()
    {
        if (teams != null)
            return teams;

        List<Team> teams = new ArrayList<Team>();

        String fileName = Config.CONFIG_PATH + getDirectoryName() + "/" + Config.TEAM_CONFIG_FILE_NAME;

        BufferedReader reader = null;

        try {
            // Open the file
            InputStream stream = new FileInputStream(fileName);
            reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

            // Process each line
            String line;
            while ((line = reader.readLine()) != null) {
                // Parse the simple structure of form: 1=my team
                String[] parts = line.split("=");

                if (parts.length != 2) {
                    Log.error("Error reading line in file \"" + fileName + "\": " + line);
                    continue;
                }

                int number = Integer.valueOf(parts[0]);
                String name = parts[1];

                // Add the team
                teams.add(new Team(number, name, this));
            }
        } catch (IOException e) {
            Log.error("Error processing team config file: " + fileName);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignored) {}
            }
        }

        // Sort teams by their number
        Collections.sort(teams, new Comparator<Team>()
        {
            @Override
            public int compare(Team o1, Team o2)
            {
                return o1.getNumber() - o2.getNumber();
            }
        });

        // Freeze the collection to prevent future changes
        this.teams = Collections.unmodifiableList(teams);

        return this.teams;
    }

    @Nullable
    public Team getTeam(int teamNumber)
    {
        for (Team t : teams())
            if (t.getNumber() == teamNumber)
                return t;
        return null;
    }

    public boolean hasTeamNumber(int teamNumber)
    {
        return getTeam(teamNumber) != null;
    }

    public boolean isSPLFamily()
    {
        return this == SPL || this == SPLDropIn;
    }

    public boolean isHLFamily()
    {
        return !isSPLFamily();
    }
}
