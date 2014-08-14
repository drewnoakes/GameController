package data;

import common.Log;
import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.Config;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Immutable data about a team, such as its name, number and logo.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class Team
{
    private final int number;
    @NotNull private final String name;
    @NotNull private final League league;
    @Nullable private BufferedImage logo;
    private boolean logoLoaded = false;

    public Team(int number, @NotNull String name, @NotNull League league)
    {
        this.number = number;
        this.name = name;
        this.league = league;
    }

    @NotNull
    public String getName()
    {
        return name;
    }

    public int getNumber()
    {
        return number;
    }

    @Nullable
    public BufferedImage getLogoImage()
    {
        if (logoLoaded)
            return logo;

        BufferedImage logo = null;

        // Support several image file extensions
        for (String ext : Config.IMAGE_EXTENSIONS) {
            // If a file exists for this team with said extension
            File file = new File(Config.CONFIG_PATH + league.getDirectoryName() + "/" + number + "." + ext);
            if (file.exists()) {
                // Try to load and decode it
                try {
                    logo = ImageIO.read(file);
                    break;
                } catch (IOException e) {
                    Log.error("Error loading team logo image: " + file.getPath());
                }
            }
        }

        // Cache for future calls
        this.logo = logo;
        logoLoaded = true;

        return logo;
    }
}
