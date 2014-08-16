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
    SPL("SPL", "spl", new SPL()),

    SPLDropIn("SPL Drop-in", "spl_dropin", new SPLDropIn()),

    HLKid("HL Kid", "hl_kid", new HLKid()),

    HLTeen("HL Teen", "hl_teen", new HLTeen()),

    HLAdult("HL Adult", "hl_adult", new HLAdult());

    public static League[] getAllLeagues()
    {
        return new League[] { SPL, SPLDropIn, HLKid, HLTeen, HLAdult };
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

    private final String name;
    private final String directoryName;
    private final LeagueSettings settings;
    private List<Team> teams;

    private League(@NotNull String name, @NotNull String directoryName, @NotNull LeagueSettings settings)
    {
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

    public LeagueSettings settings()
    {
        return settings;
    }

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
                } catch (Exception e) {}
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
