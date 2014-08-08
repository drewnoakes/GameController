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
import data.GameStateSnapshot;
import data.Period;
import data.PlayMode;
import rules.Rules;
import data.Teams;

/**
 * This class displays the game-state
 *
 * @author Michel Bartsch
 */
public class GUI extends JFrame
{
    /**
     * Some constants defining this GUI`s appearance as their names say.
     * Feel free to change them and see what happens.
     */
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
    private static final String CONFIG_PATH = "config/";
    private static final String BACKGROUND = "background";
    private static final String WAITING_FOR_PACKAGE = "waiting for package...";

    /** Available screens. */
    private static final GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

    private BufferStrategy bufferStrategy;
    /** If testmode is on to just display whole GameState. */
    private boolean testmode = false;
    /** The last state received to show. */
    private GameStateSnapshot state = null;
    /** The background. */
    private BufferedImage background;
    
    /** The fonts used. */
    private Font testFont;
    private Font standardFont;
    private Font standardSmallFont;
    private Font scoreFont;
    private Font coachMessageFont;


    /**
     * Creates a new GUI.
     */
    GUI()
    {
        super(WINDOW_TITLE, devices[IS_OSX && !IS_APPLE_JAVA ? 0 : devices.length - 1].getDefaultConfiguration());
        
        setUndecorated(true);
        if (IS_APPLE_JAVA && devices.length != 1) {
            setSize(devices[devices.length-1].getDefaultConfiguration().getBounds().getSize());
        } else {
            devices[IS_OSX && !IS_APPLE_JAVA ? 0 : devices.length-1].setFullScreenWindow(this);
        }

        for (String format : new String [] {".png", ".jpeg", ".jpg"}) {
            try {
                background = ImageIO.read(new File(CONFIG_PATH+Rules.league.leagueDirectory+"/"+BACKGROUND+format));
            } catch (IOException e) {
            }
        }
        if (background == null) {
            Log.error("Unable to load background image");
        }
        float scaleFactor = (float)getWidth()/background.getWidth();
        Image tmp = (new ImageIcon(background).getImage()).getScaledInstance(
                (int)(background.getWidth()*scaleFactor),
                (int)(background.getHeight()*scaleFactor),
                Image.SCALE_SMOOTH);
        background = new BufferedImage((int) (background.getWidth() * scaleFactor), (int) (background.getWidth() * scaleFactor), BufferedImage.TYPE_INT_ARGB);
        background.getGraphics().drawImage(tmp, 0, 0, null);
        
        testFont = new Font(TEST_FONT, Font.PLAIN, (int)(TEST_FONT_SIZE*getWidth()));
        standardFont = new Font(STANDARD_FONT, Font.PLAIN, (int)(STANDARD_FONT_SIZE*getWidth()));
        standardSmallFont = new Font(STANDARD_FONT, Font.PLAIN, (int)(STANDARD_FONT_S_SIZE*getWidth()));
        scoreFont = new Font(STANDARD_FONT, Font.PLAIN, (int)(STANDARD_FONT_XXL_SIZE*getWidth()));
        coachMessageFont = new Font(Font.DIALOG, Font.PLAIN, (int)(0.037*getWidth()));
        
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e) {
                Main.exit();
            }
        });
        
        if (IS_OSX) {
            setVisible(false); // without this, keyboard input is missing on OS X
        }
        
        setVisible(true);
        createBufferStrategy(2);
        bufferStrategy = getBufferStrategy();
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
     * This toggles the visualizer´s testmode on and off.
     */
    public void toggleTestmode()
    {
        testmode = !testmode;
        update(state);
    }
    
    /**
     * This is called by the GameStateListener after receiving GameState to show
     * them on the gui.
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
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(background, 0, 0, null);
        
        if (state == null) {
            drawNoPackage(g);
        } else if (testmode) {
            drawTestmode(g);
        } else {
            drawTeams(g);
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
     * This draws something to inform that there is no package to draw.
     * 
     * @param g  The graphics object to draw on.
     */
    private void drawNoPackage(Graphics g)
    {
        g.setColor(Color.BLACK);
        g.setFont(testFont);
        g.drawString(WAITING_FOR_PACKAGE, (int)(0.2*getWidth()), (int)(0.3*getHeight()));
    }
    
    /**
     * This draws everything in the package in a simple way, just for testing.
     * 
     * @param g  The graphics object to draw on.
     */
    private void drawTestmode(Graphics g)
    {
        g.setColor(Color.BLACK);
        g.setFont(testFont);
        int x = getSizeToWidth(0.08);
        int y = getSizeToHeight(0.3);
        String[] out = state.toString().split("\n");
        for (String o : out) {
            g.drawString(o, x, y);
            y += testFont.getSize() * 1.2;
        }
        for (int j=0; j<2; j++) {
            out = state.team[j].toString().split("\n");
            for (String o : out) {
                g.drawString(o, x, y);
                y += testFont.getSize() * 1.2;
            }
        }
        
        x = getSizeToWidth(0.35);
        for (int i=0; i<2; i++) {
            y = getSizeToHeight(0.2);
            for (int j=0; j< state.team[i].player.length; j++) {
                out = state.team[i].player[j].toString().split("\n");
                for (String o : out) {
                    g.drawString(o, x, y);
                    y += testFont.getSize() * 1.2;
                }
            }
            x = getSizeToWidth(0.64);
        }
    }

    /**
     * This draws the teams´s icons.
     * 
     * @param g  The graphics object to draw on.
     */
    private void drawTeams(Graphics g)
    {
        int x = getSizeToWidth(0.01);
        int y = getSizeToHeight(0.35);
        int size = getSizeToWidth(0.28);
        BufferedImage[] icons = new BufferedImage[] {
            Teams.getIcon(state.team[0].teamNumber),
            Teams.getIcon(state.team[1].teamNumber)};
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        for (int i=0; i<2; i++) {
            g.setColor(state.team[i].teamColor.getColor());
            float scaleFactorX = 1f;
            float scaleFactorY = 1f;
            if (icons[i].getWidth() * 1.2f > icons[i].getHeight()) {
                scaleFactorY = icons[i].getHeight()/(float)icons[i].getWidth();
            } else {
                scaleFactorX = icons[i].getWidth()/(float)icons[i].getHeight();
            }
            int offsetX = (int)((size - size*scaleFactorX)/2);
            int offsetY = (int)((size - size*scaleFactorY)/2);
            g.drawImage(icons[i],
                    (i==1 ? x : getWidth()-x-size) + offsetX,
                    y+offsetY,
                    (int)(scaleFactorX*size),
                    (int)(scaleFactorY*size), null);
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
        drawCenteredString(g, ":", getWidth()/2-size, yDiv, 2*size);
        for (int i=0; i<2; i++) {
            g.setColor(state.team[i].teamColor.getColor());
            drawCenteredString(
                    g,
                    state.team[i].score+"",
                    i==1 ? x : getWidth()-x-size,
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
        drawCenteredString(g, formatTime(state.secsRemaining), x, y, size);
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
        if (this.state.period == Period.Normal) {
            if (this.state.firstHalf) {
                if (this.state.playMode == PlayMode.Finished) {
                    state = "Half Time";
                } else {
                    state = "First Half";
                }
            } else {
                if (this.state.playMode == PlayMode.Initial) {
                    state = "Half Time";
                } else {
                    state = "Second Half";
                }
            }
        } else if (this.state.period == Period.Overtime) {
            state = "Overtime";
        } else if (this.state.period == Period.PenaltyShootout) {
            state = "Penalty Shootout";
        } else if (this.state.period == Period.Timeout) {
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
        drawCenteredString(g, state.playMode.toString(), x, y, size);
    }
    
    /**
     * This draws the sub time, for example the ready time.
     * 
     * @param g  The graphics object to draw on.
     */
    private void drawSubTime(Graphics g)
    {
        if (state.secondaryTime == 0) {
            return;
        }
        g.setColor(Color.BLACK);
        g.setFont(standardSmallFont);
        int x = getSizeToWidth(0.4);
        int y = getSizeToHeight(0.9);
        int size = getSizeToWidth(0.2);
        drawCenteredString(g, formatTime(state.secondaryTime), x, y, size);
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
        for (int i=0; i<2; i++) {
            g.setColor(state.team[i].teamColor.getColor());
            for (int j=0; j< state.team[i].penaltyShot; j++) {
                if ((state.team[i].singleShots & (1<<j)) != 0) {
                    g.fillOval(i==1 ? x+j*2*size : getWidth()-x-(5-j)*2*size-size, y, size, size);
                } else {
                    g.drawOval(i==1 ? x+j*2*size : getWidth()-x-(5-j)*2*size-size, y, size, size);
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
        return (int)(size*getWidth());
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
        return (int)(size*getHeight());
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
        Graphics2D g2 = (Graphics2D) g; //need for setting the thickness of the line of the rectangles

        for (int i = 0; i < 2; i++) {
            String coachMessage;
            try {
                coachMessage = new String(state.team[i].coachMessage, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                coachMessage = new String(state.team[i].coachMessage);
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

            //Draw the coach label and coach message box
            g2.setColor(state.team[i].teamColor.getColor());
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
}
