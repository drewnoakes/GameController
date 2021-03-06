package visualizer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import common.Interval;
import common.Log;
import common.annotations.NotNull;
import controller.Config;
import controller.ui.StringPlotter;
import data.*;

/**
 * The window of the Game Controller Visualizer.
 * <p>
 * Displays a summary of the game state in a large screen format, targeted at audiences and field referees.
 * <p>
 * Shows:
 *
 * <ul>
 *     <li>Team logos</li>
 *     <li>Score</li>
 *     <li>Time remaining</li>
 *     <li>The current game period</li>
 *     <li>The current play mode</li>
 * </ul>
 *
 * @author Michel Bartsch
 * @author Drew Noakes https://drewnoakes.com
 */
public class VisualizerUI
{
    private static final boolean IS_OSX = System.getProperty("os.name").contains("OS X");
    private static final boolean IS_APPLE_JAVA = IS_OSX && System.getProperty("java.version").compareTo("1.7") < 0;
    private static final int DISPLAY_UPDATE_DELAY = 500;
    /** Available screens on the current computer. */
    private static final GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

    private final BufferStrategy bufferStrategy;
    private final VisualizerOptions options;
    /** If testmode is on to just display whole GameState. */
    private boolean testmode = false;
    /** The last state received to show. */
    private GameStateSnapshot state = null;
    private BufferedImage unscaledBackground;
    private BufferedImage background;

    private final JFrame frame;
    
    // The fonts used

    private Font testFont;
    private Font standardFont;
    private Font standardSmallFont;
    private Font scoreFont;
    private Font coachMessageFont;

    private boolean mirrorTeams = false;

    /**
     * Constructs all elements of the UI and shows it on screen.
     */
    public VisualizerUI(@NotNull VisualizerOptions options)
    {
        this.options = options;

        GraphicsDevice device = devices[IS_OSX && !IS_APPLE_JAVA ? 0 : devices.length - 1];

        frame = new JFrame("Visualizer", device.getDefaultConfiguration());
        frame.setUndecorated(true);

        // Load the background image
        String backgroundImagePrefix = Config.CONFIG_PATH + options.getLeague().getDirectoryName() + "/background";
        for (String ext : Config.IMAGE_EXTENSIONS) {
            String path = null;
            try {
                path = backgroundImagePrefix + "." + ext;
                File file = new File(path);
                if (file.exists())
                    unscaledBackground = ImageIO.read(file);
            } catch (IOException e) {
                Log.error("Error decoding image file: " + path);
            }
        }
        if (unscaledBackground == null) {
            Log.error("Unable to load background image: " + backgroundImagePrefix + ".*");
            System.exit(1);
        }

        // Set initial size as full-screen
        if (IS_APPLE_JAVA && devices.length != 1) {
            frame.setSize(devices[devices.length-1].getDefaultConfiguration().getBounds().getSize());
        } else {
            device.setFullScreenWindow(frame);
        }

        // Handle resize events (ie. projector plugged in, window docked, etc)
        frame.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent evt)
            {
                scaleResources();
            }
        });

        scaleResources();

        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e) {
                Main.exit();
            }
        });
        
        if (IS_OSX) {
            frame.setVisible(false); // without this, keyboard input is missing on OS X
        }

        frame.setVisible(true);
        frame.createBufferStrategy(2);
        bufferStrategy = frame.getBufferStrategy();

        // Start a thread that periodically updates the UI with the latest state
        // TODO do this directly in response to new state arriving rather than via polling
        Thread displayUpdater = new Thread()
        {
            @Override
            public void run() {
                Interval interval = new Interval(DISPLAY_UPDATE_DELAY);
                //noinspection InfiniteLoopStatement
                while (true) {
                    update(state);
                    try {
                        interval.sleep();
                    } catch (InterruptedException ignored) {}
                }
            }
        };
        displayUpdater.setName("Display Updater");
        displayUpdater.start();
    }

    /** Scales fonts and images to match the size of the frame. */
    private void scaleResources()
    {
        float scaleFactor = (float)frame.getWidth()/unscaledBackground.getWidth();
        Image tmp = (new ImageIcon(unscaledBackground).getImage()).getScaledInstance(
                (int)(unscaledBackground.getWidth()*scaleFactor),
                (int)(unscaledBackground.getHeight()*scaleFactor),
                Image.SCALE_SMOOTH);
        background = new BufferedImage(
                (int) (unscaledBackground.getWidth() * scaleFactor),
                (int) (unscaledBackground.getWidth() * scaleFactor),
                BufferedImage.TYPE_INT_ARGB);
        background.getGraphics().drawImage(tmp, 0, 0, null);

        testFont = new Font(Font.MONOSPACED, Font.PLAIN, getSizeToWidth(0.01));
        standardFont = new Font(Font.DIALOG, Font.PLAIN, getSizeToWidth(0.08));
        standardSmallFont = new Font(Font.DIALOG, Font.PLAIN, getSizeToWidth(0.05));
        scoreFont = new Font(Font.DIALOG, Font.PLAIN, getSizeToWidth(0.16));
        coachMessageFont = new Font(Font.DIALOG, Font.PLAIN, getSizeToWidth(0.037));
    }
    
    /**
     * Toggles testmode on and off.
     * Test mode allows the user to make changes to the state without adhering to the game's rules.
     */
    public void toggleTestmode()
    {
        testmode = !testmode;
        update(state);
    }

    /** Toggles the arrangement of the teams in the UI. */
    public void mirrorTeams()
    {
        this.mirrorTeams = !this.mirrorTeams;
    }
    
    /**
     * Updates the visualizer's UI with the provided game state snapshot.
     * 
     * @param state the game state to show.
     */
    public synchronized void update(GameStateSnapshot state)
    {
        this.state = state;

        do {
            do {
                Graphics g = bufferStrategy.getDrawGraphics();
                draw(g);
                g.dispose();
            } while (bufferStrategy.contentsRestored());
            bufferStrategy.show();
        } while (bufferStrategy.contentsLost());
    }
    
    /**
     * This draws the whole visualizer.
     * 
     * @param g  The graphics object to draw on.
     */
    public final void draw(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
        g.drawImage(background, 0, 0, null);
        
        if (state == null) {
            drawNoPacket(g);
        } else if (testmode) {
            drawTestmode(g);
        } else {
            drawTeamLogos(g2);
            drawScores(g);
            drawTime(g);
            drawSecState(g);
            drawPlayMode(g);
            drawSubTime(g);
            drawPenaltyInfo(g);
            drawCoachMessages(g2);
        }
    }
    
    /**
     * This draws something to inform that there is no packet to draw.
     * 
     * @param g the graphics object to draw on.
     */
    private void drawNoPacket(Graphics g)
    {
        g.setColor(Color.BLACK);
        g.setFont(testFont);
        g.drawString("Waiting for data packet...", getSizeToWidth(0.2), getSizeToHeight(0.3));
    }
    
    /**
     * This draws everything in the packet in a simple way, just for testing.
     * 
     * @param g the graphics object to draw on.
     */
    private void drawTestmode(Graphics g)
    {
        double yMargin = 0.3;
        double xMargin = 0.08;
        double xSpacing = (1 - (2*xMargin)) / 3.0;

        StringPlotter plotter = new StringPlotter(g, testFont, Color.BLACK, 1.2);
        plotter.setX(getSizeToWidth(xMargin));
        plotter.setY(getSizeToHeight(yMargin));

        plotter.write("           playMode: " + state.getPlayMode());
        plotter.write("          firstHalf: " + (state.isFirstHalf() ? "true" : "false"));
        plotter.write("   nextKickOffColor: " + state.getNextKickOffColor());
        plotter.write("             period: " + state.getPeriod());
        plotter.write("    lastDropInColor: " + state.getLastDropInColor());
        plotter.write("         dropInTime: " + state.getDropInTime());
        plotter.write("      secsRemaining: " + state.getSecsRemaining());
        plotter.write("      secondaryTime: " + state.getSecondaryTime());

        plotter.setX(getSizeToWidth(xMargin + xSpacing));
        for (TeamStateSnapshot team : state.getTeams()) {
            plotter.setY(getSizeToHeight(yMargin));
            plotter.write("  teamNumber: " + team.getTeamNumber());
            plotter.write("   teamColor: " + team.getTeamColor());
            plotter.write("       score: " + team.getScore());
            plotter.write(" penaltyShot: " + team.getPenaltyShotCount());
            plotter.write(" singleShots: " + Integer.toBinaryString(team.getPenaltyShotFlags()));
            plotter.write("coachMessage: " + (team.getCoachMessage() != null ? new String(team.getCoachMessage()) : null));
            plotter.write(" coachStatus: " + team.getCoach());
            plotter.write("----------------------------");
            for (PlayerStateSnapshot player : team.getPlayers()) {
                plotter.write("      player: " + player.uniformNumber);
                plotter.write("     penalty: " + player.penalty
                        + (player.penalty == Penalty.None || player.penalty == Penalty.Substitute
                            ? ""
                            : " (" + player.secondsTillUnpenalised + ")"));
            }
            plotter.setX(getSizeToWidth(xMargin + 2*xSpacing));
        }
    }

    /**
     * This draws the team logos.
     * 
     * @param g the graphics object to draw on.
     */
    private void drawTeamLogos(Graphics2D g)
    {
        int x = getSizeToWidth(0.01);
        int y = getSizeToHeight(0.35);
        int size = getSizeToWidth(0.28);

        Team team1 = options.getLeague().getTeam(state.getTeam1().getTeamNumber());
        Team team2 = options.getLeague().getTeam(state.getTeam2().getTeamNumber());

        if (team1 == null || team2 == null)
            return;

        BufferedImage[] logos = new BufferedImage[] {
            team1.getLogoImage(),
            team2.getLogoImage()
        };

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        for (int i = 0; i < 2; i++) {
            BufferedImage logo = logos[i];
            if (logo == null)
                continue;
            float scaleFactorX = 1f;
            float scaleFactorY = 1f;
            if (logo.getWidth() * 1.2f > logo.getHeight()) {
                scaleFactorY = logo.getHeight() / (float)logo.getWidth();
            } else {
                scaleFactorX = logo.getWidth() / (float)logo.getHeight();
            }
            int offsetX = (int)((size - size * scaleFactorX) / 2);
            int offsetY = (int)((size - size * scaleFactorY) / 2);
            g.drawImage(logo,
                    (i == 0 ^ mirrorTeams ? x : frame.getWidth() - x - size) + offsetX,
                    y + offsetY,
                    (int)(scaleFactorX * size),
                    (int)(scaleFactorY * size), null);
        }
    }
    
    /**
     * This draws the score.
     * 
     * @param g  The graphics object to draw on.
     */
    private void drawScores(Graphics g)
    {
        g.setFont(scoreFont);

        int x = getSizeToWidth(0.34);
        int y = getSizeToHeight(0.61);
        int yDiv = getSizeToHeight(0.59);
        int size = getSizeToWidth(0.12);

        g.setColor(Color.BLACK);
        drawCenteredString(g, ":", frame.getWidth() / 2 - size, yDiv, 2 * size);

        for (UISide side : UISide.both()) {
            g.setColor(getTeam(side).getTeamColor().getRgb(options.getLeague()));
            drawCenteredString(
                    g,
                    Integer.toString(getTeam(side).getScore()),
                    side == UISide.Left ^ mirrorTeams ? x : frame.getWidth() - x - size,
                    y,
                    size);
        }
    }
    
    /**
     * This draws the main time.
     * 
     * @param g  The graphics object to draw on.
     */
    private void drawTime(Graphics g)
    {
        g.setColor(Color.BLACK);
        g.setFont(standardFont);

        int x = getSizeToWidth(0.4);
        int y = getSizeToHeight(0.37);
        int size = getSizeToWidth(0.2);

        drawCenteredString(g, formatTime(state.getSecsRemaining()), x, y, size);
    }
    
    /**
     * This draws the secondary state, for example "First Half".
     * 
     * @param g  The graphics object to draw on.
     */
    private void drawSecState(Graphics g)
    {
        // Determine the text to use
        String text;
        Period period = this.state.getPeriod();
        if (period == Period.Normal) {
            if (this.state.isFirstHalf()) {
                if (this.state.getPlayMode() == PlayMode.Finished) {
                    text = "Half Time";
                } else {
                    text = "First Half";
                }
            } else {
                if (this.state.getPlayMode() == PlayMode.Initial) {
                    text = "Half Time";
                } else {
                    text = "Second Half";
                }
            }
        } else if (period == Period.Overtime) {
            text = "Overtime";
        } else if (period == Period.PenaltyShootout) {
            text = "Penalty Shootout";
        } else if (period == Period.Timeout) {
            text = "Time Out";
        } else {
            // Should never reach here
            Log.error("Invalid period: " + period);
            text = "";
        }

        // Draw the text on the UI
        g.setColor(Color.BLACK);
        g.setFont(standardSmallFont);
        int x = getSizeToWidth(0.4);
        int y = getSizeToHeight(0.72);
        int size = getSizeToWidth(0.2);
        drawCenteredString(g, text, x, y, size);
    }
    
    /**
     * This draws the play mode, for example "Initial".
     * 
     * @param g  The graphics object to draw on.
     */
    private void drawPlayMode(Graphics g)
    {
        g.setColor(Color.BLACK);
        g.setFont(standardSmallFont);
        int x = getSizeToWidth(0.4);
        int y = getSizeToHeight(0.81);
        int size = getSizeToWidth(0.2);
        drawCenteredString(g, state.getPlayMode().toString(), x, y, size);
    }
    
    /**
     * This draws the sub time, for example the ready time.
     * 
     * @param g  The graphics object to draw on.
     */
    private void drawSubTime(Graphics g)
    {
        if (state.getSecondaryTime() == 0) {
            return;
        }
        g.setColor(Color.BLACK);
        g.setFont(standardSmallFont);
        int x = getSizeToWidth(0.4);
        int y = getSizeToHeight(0.9);
        int size = getSizeToWidth(0.2);
        drawCenteredString(g, formatTime(state.getSecondaryTime()), x, y, size);
    }
    
    /**
     * This draws the penalty tries and if they scored.
     * 
     * @param g  The graphics object to draw on.
     */
    private void drawPenaltyInfo(Graphics g)
    {
        g.setColor(Color.RED);

        int x = getSizeToWidth(0.05);
        int y = getSizeToHeight(0.86);
        int size = getSizeToWidth(0.02);

        for (int i = 0; i < 2; i++) {
            TeamStateSnapshot team = i == 0 ? state.getTeam1() : state.getTeam2();
            g.setColor(team.getTeamColor().getRgb(options.getLeague()));
            for (int j = 0; j < team.getPenaltyShotCount(); j++) {
                int circleX = i == 0 ? x + j * 2 * size : frame.getWidth() - x - (5 - j) * 2 * size - size;
                if ((team.getPenaltyShotFlags() & (1 << j)) != 0) {
                    g.fillOval(circleX, y, size, size);
                } else {
                    g.drawOval(circleX, y, size, size);
                }
            }
        }
    }
    
    /**
     * This simply draws a string horizontal centered on a given position.
     * 
     * @param g     The graphics object to draw on.
     * @param s     The string to draw.
     * @param x     Left position of the area to draw the string in.
     * @param y     Upper position of the area to draw the string in.
     * @param width The width of the area to draw the string centered in.
     */
    private void drawCenteredString(Graphics g, String s, int x, int y, int width)
    {
        int offset = (width - g.getFontMetrics().stringWidth(s)) / 2;
        g.drawString(s, x+offset, y);
    }

    private void drawCoachMessages(Graphics2D g)
    {
        for (int i = 0; i < 2; i++) {
            TeamStateSnapshot team = i == 0 ? state.getTeam1() : state.getTeam2();

            if (team.getCoachMessage() == null)
                continue;

            String coachMessage;
            try {
                coachMessage = new String(team.getCoachMessage(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                coachMessage = new String(team.getCoachMessage());
            }
            int p = coachMessage.indexOf(0);
            if (p != -1) {
                coachMessage = coachMessage.substring(0, p);
            }

            g.setFont(standardSmallFont);
            int maxWidth = (getSizeToWidth(0.99) - getSizeToWidth(0.01) - g.getFontMetrics().stringWidth("00::00")) / 2;

            g.setFont(coachMessageFont);
            int split = -1;
            int j;
            for (j = 0; j < coachMessage.length() &&
                  g.getFontMetrics().stringWidth(coachMessage.substring(0, j + 1)) <= maxWidth; ++j) {
                if (!Character.isLetter(coachMessage.charAt(j))
                        || j < coachMessage.length() - 1
                        && Character.isLowerCase(coachMessage.charAt(j))
                        && Character.isUpperCase(coachMessage.charAt(j + 1))) {
                    split = j;
                }
            }

            String row1;
            String row2;
            if (j == coachMessage.length()) {
                row1 = "";
                row2 = coachMessage;
            } else {
                row1 = coachMessage.substring(0, split + 1).trim();
                row2 = coachMessage.substring(split + 1).trim();
            }

            // Draw the coach label and coach message box
            g.setColor(team.getTeamColor().getRgb(options.getLeague()));
            if (i == 0 ^ mirrorTeams) {
                g.drawString(row1, getSizeToWidth(0.01), getSizeToHeight(0.92));
                g.drawString(row2, getSizeToWidth(0.01), getSizeToHeight(0.98));
            } else {
                g.drawString(row1, getSizeToWidth(0.99) - g.getFontMetrics().stringWidth(row1), getSizeToHeight(0.92));
                g.drawString(row2, getSizeToWidth(0.99) - g.getFontMetrics().stringWidth(row2), getSizeToHeight(0.98));
            }
        }
    }

    /**
     * This is used to scale sizes depending on the visualizer's width.
     *
     * @param size  A size between 0.0 (nothing) and 1.0 (full visualizer's width).
     *
     * @return A size in pixel.
     */
    private int getSizeToWidth(double size)
    {
        return (int)(size*frame.getWidth());
    }

    /**
     * This is used to scale sizes depending on the visualizer's height.
     *
     * @param size  A size between 0.0 (nothing) and 1.0 (full visualizer's height).
     *
     * @return A size in pixel.
     */
    private int getSizeToHeight(double size)
    {
        return (int)(size*frame.getHeight());
    }

    /**
     * Formats a time in seconds to a usual looking minutes and seconds time as string.
     *
     * @param seconds   Time to format in seconds.
     *
     * @return Time formatted.
     */
    private String formatTime(int seconds)
    {
        int displaySeconds = Math.abs(seconds) % 60;
        int displayMinutes = Math.abs(seconds) / 60;
        return (seconds < 0 ? "-" : "") + String.format("%02d:%02d", displayMinutes, displaySeconds);
    }

    private TeamStateSnapshot getTeam(@NotNull UISide side)
    {
        return side == UISide.Left ? state.getTeam1() : state.getTeam2();
    }
}
