package visualizer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import data.*;

/**
 * The window of the Game Controller Visualizer.
 *
 * Displays a summary of the game state in a large screen format, targeted at audiences and field referees.
 *
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
 */
public class VisualizerUI
{
    // Some constants defining this GUI`s appearance as their names say.
    // Feel free to change them and see what happens.

    private static final boolean IS_OSX = System.getProperty("os.name").contains("OS X");
    private static final boolean IS_APPLE_JAVA = IS_OSX && System.getProperty("java.version").compareTo("1.7") < 0;
    private static final String WINDOW_TITLE = "Visualizer";
    private static final int DISPLAY_UPDATE_DELAY = 500;
    private static final String STANDARD_FONT = Font.DIALOG;
    private static final double STANDARD_FONT_SIZE = 0.08;
    private static final double STANDARD_FONT_XXL_SIZE = 0.16;
    private static final double STANDARD_FONT_S_SIZE = 0.05;
    private static final String TEST_FONT = "Lucida Console";
    private static final double TEST_FONT_SIZE = 0.01;
    private static final String BACKGROUND = "background";
    private static final String WAITING_FOR_PACKET = "waiting for data packet...";

    /** Available screens on the current computer. */
    private static final GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

    private final BufferStrategy bufferStrategy;
    private final VisualizerOptions options;
    /** If testmode is on to just display whole GameState. */
    private boolean testmode = false;
    /** The last state received to show. */
    private GameStateSnapshot state = null;
    /** The background. */
    private BufferedImage background;

    private final JFrame frame;
    
    // The fonts used

    private final Font testFont;
    private final Font standardFont;
    private final Font standardSmallFont;
    private final Font scoreFont;
    private final Font coachMessageFont;

    /**
     * Constructs all elements of the UI and shows it on screen.
     */
    VisualizerUI(@NotNull VisualizerOptions options)
    {
        this.options = options;

        frame = new JFrame(WINDOW_TITLE, devices[IS_OSX && !IS_APPLE_JAVA ? 0 : devices.length - 1].getDefaultConfiguration());

        frame.setUndecorated(true);
        if (IS_APPLE_JAVA && devices.length != 1) {
            frame.setSize(devices[devices.length-1].getDefaultConfiguration().getBounds().getSize());
        } else {
            devices[IS_OSX && !IS_APPLE_JAVA ? 0 : devices.length-1].setFullScreenWindow(frame);
        }

        for (String ext : Config.IMAGE_EXTENSIONS) {
            String path = null;
            try {
                path = Config.CONFIG_PATH + options.getLeague().getDirectoryName() + "/" + BACKGROUND + "." + ext;
                File file = new File(path);
                if (file.exists())
                    background = ImageIO.read(file);
            } catch (IOException e) {
                Log.error("Error decoding image file: " + path);
            }
        }
        if (background == null) {
            Log.error("Unable to load background image: " + Config.CONFIG_PATH + options.getLeague().getDirectoryName() + "/" + BACKGROUND + ".*");
            System.exit(1);
        }
        float scaleFactor = (float)frame.getWidth()/background.getWidth();
        Image tmp = (new ImageIcon(background).getImage()).getScaledInstance(
                (int)(background.getWidth()*scaleFactor),
                (int)(background.getHeight()*scaleFactor),
                Image.SCALE_SMOOTH);
        background = new BufferedImage((int) (background.getWidth() * scaleFactor), (int) (background.getWidth() * scaleFactor), BufferedImage.TYPE_INT_ARGB);
        background.getGraphics().drawImage(tmp, 0, 0, null);
        
        testFont = new Font(TEST_FONT, Font.PLAIN, (int)(TEST_FONT_SIZE*frame.getWidth()));
        standardFont = new Font(STANDARD_FONT, Font.PLAIN, (int)(STANDARD_FONT_SIZE*frame.getWidth()));
        standardSmallFont = new Font(STANDARD_FONT, Font.PLAIN, (int)(STANDARD_FONT_S_SIZE*frame.getWidth()));
        scoreFont = new Font(STANDARD_FONT, Font.PLAIN, (int)(STANDARD_FONT_XXL_SIZE*frame.getWidth()));
        coachMessageFont = new Font(Font.DIALOG, Font.PLAIN, (int)(0.037*frame.getWidth()));

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
                while (true) {
                    update(state);
                    try {
                        interval.sleep();
                    } catch (InterruptedException e) {}
                }
            }
        };
        displayUpdater.start();
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
            drawTeamLogos(g);
            drawScores(g);
            drawTime(g);
            drawSecState(g);
            drawPlayMode(g);
            drawSubTime(g);
            drawPenaltyInfo(g);
            drawCoachMessages(g);
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
        g.drawString(WAITING_FOR_PACKET, (int)(0.2*frame.getWidth()), (int)(0.3*frame.getHeight()));
    }
    
    /**
     * This draws everything in the packet in a simple way, just for testing.
     * 
     * @param g the graphics object to draw on.
     */
    private void drawTestmode(Graphics g)
    {
        g.setColor(Color.BLACK);
        g.setFont(testFont);
        int x = getSizeToWidth(0.08);
        int y = getSizeToHeight(0.3);
        String[] out = {
            "           playMode: " + state.getPlayMode(),
            "          firstHalf: " + (state.isFirstHalf() ? "true" : "false"),
            "   nextKickOffColor: " + state.getNextKickOffColor(),
            "             period: " + state.getPeriod(),
            "    lastDropInColor: " + state.getLastDropInColor(),
            "         dropInTime: " + state.getDropInTime(),
            "      secsRemaining: " + state.getSecsRemaining(),
            "      secondaryTime: " + state.getSecondaryTime()
        };
        for (String o : out) {
            g.drawString(o, x, y);
            y += testFont.getSize() * 1.2;
        }

        for (TeamStateSnapshot team : state.getTeams()) {
            String[] teamLines = {
                "--------------------------------------",
                "         teamNumber: " + team.getTeamNumber(),
                "          teamColor: " + team.getTeamColor(),
                "              score: " + team.getScore(),
                "        penaltyShot: " + team.getPenaltyShotCount(),
                "        singleShots: " + Integer.toBinaryString(team.getPenaltyShotFlags()),
                "       coachMessage: " + (team.getCoachMessage() != null ? new String(team.getCoachMessage()) : null),
                "        coachStatus: " + team.getCoach()
            };
            for (String o : teamLines) {
                g.drawString(o, x, y);
                y += testFont.getSize() * 1.2;
            }
        }
        
        x = getSizeToWidth(0.35);
        for (TeamStateSnapshot team : state.getTeams()) {
            y = getSizeToHeight(0.2);
            for (PlayerStateSnapshot player : team.getPlayers()) {
                String[] playerLines = {
                    "            penalty: " + player.penalty,
                    "secsTillUnpenalised: " + player.secondsTillUnpenalised
                };
                for (String o : playerLines) {
                    g.drawString(o, x, y);
                    y += testFont.getSize() * 1.2;
                }
            }
            x = getSizeToWidth(0.64);
        }
    }

    /**
     * This draws the team logos.
     * 
     * @param g the graphics object to draw on.
     */
    private void drawTeamLogos(Graphics g)
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

        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        for (int i = 0; i < 2; i++) {
            TeamStateSnapshot team = i == 0 ? state.getTeam1() : state.getTeam2();
            g.setColor(team.getTeamColor().getRgb(options.getLeague()));
            float scaleFactorX = 1f;
            float scaleFactorY = 1f;
            if (logos[i].getWidth() * 1.2f > logos[i].getHeight()) {
                scaleFactorY = logos[i].getHeight() / (float)logos[i].getWidth();
            } else {
                scaleFactorX = logos[i].getWidth() / (float)logos[i].getHeight();
            }
            int offsetX = (int)((size - size * scaleFactorX) / 2);
            int offsetY = (int)((size - size * scaleFactorY) / 2);
            g.drawImage(logos[i],
                    (i == 1 ? x : frame.getWidth() - x - size) + offsetX,
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
                    side == UISide.Left ? x : frame.getWidth() - x - size,
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
        g.setColor(Color.BLACK);
        g.setFont(standardSmallFont);
        int x = getSizeToWidth(0.4);
        int y = getSizeToHeight(0.72);
        int size = getSizeToWidth(0.2);
        String state;
        if (this.state.getPeriod() == Period.Normal) {
            if (this.state.isFirstHalf()) {
                if (this.state.getPlayMode() == PlayMode.Finished) {
                    state = "Half Time";
                } else {
                    state = "First Half";
                }
            } else {
                if (this.state.getPlayMode() == PlayMode.Initial) {
                    state = "Half Time";
                } else {
                    state = "Second Half";
                }
            }
        } else if (this.state.getPeriod() == Period.Overtime) {
            state = "Overtime";
        } else if (this.state.getPeriod() == Period.PenaltyShootout) {
            state = "Penalty Shootout";
        } else if (this.state.getPeriod() == Period.Timeout) {
            state = "Time Out";
        } else {
            state = "";
        }
        drawCenteredString(g, state, x, y, size);
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
                int circleX = i == 1 ? x + j * 2 * size : frame.getWidth() - x - (5 - j) * 2 * size - size;
                if ((team.getPenaltyShotFlags() & (1 << j)) != 0) {
                    g.fillOval(circleX, y, size, size);
                } else {
                    g.drawOval(circleX, y, size, size);
                }
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

    private void drawCoachMessages(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g; // needed for setting the thickness of the line of the rectangles

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

            g2.setFont(standardSmallFont);
            int maxWidth = (getSizeToWidth(0.99) - getSizeToWidth(0.01) - g2.getFontMetrics().stringWidth("00::00")) / 2;

            g2.setFont(coachMessageFont);
            int split = -1;
            int j;
            for (j = 0; j < coachMessage.length() &&
                  g2.getFontMetrics().stringWidth(coachMessage.substring(0, j + 1)) <= maxWidth; ++j) {
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
            g2.setColor(team.getTeamColor().getRgb(options.getLeague()));
            if (i == 1) {
                g2.drawString(row1, getSizeToWidth(0.01), getSizeToHeight(0.92));
                g2.drawString(row2, getSizeToWidth(0.01), getSizeToHeight(0.98));
            } else {
                g2.drawString(row1, getSizeToWidth(0.99) - g2.getFontMetrics().stringWidth(row1), getSizeToHeight(0.92));
                g2.drawString(row2, getSizeToWidth(0.99) - g2.getFontMetrics().stringWidth(row2), getSizeToHeight(0.98));
            }
        }
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

    private TeamStateSnapshot getTeam(UISide side)
    {
        return side == UISide.Left ? state.getTeam1() : state.getTeam2();
    }
}
