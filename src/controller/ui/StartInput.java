package controller.ui;

import common.annotations.NotNull;
import controller.Config;
import controller.StartOptions;
import controller.ui.controls.ImagePanel;
import controller.ui.controls.LeagueListCellRenderer;
import controller.ui.controls.TeamListCellRenderer;
import data.League;
import data.Team;
import data.TeamColor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import javax.swing.*;

/**
 * This is only to be on starting the program to get starting input.
 *
 * @author Michel Bartsch
 * @author Drew Noakes https://drewnoakes.com
 */
public class StartInput
{
    // Layout constants
    private static final String WINDOW_TITLE = "RoboCup Game Controller";
    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 482;
    private static final int STANDARD_SPACE = 10;
    private static final int TEAMS_HEIGHT = 300;
    private static final int IMAGE_SIZE = 250;
    private static final int OPTIONS_CONTAINER_HEIGHT = 80;
    private static final int OPTIONS_HEIGHT = 22;
    private static final int START_HEIGHT = 30;
    /** This is not what the name says ;) */
    private static final int FULLSCREEN_WIDTH = 160;

    private static final String[] BACKGROUND_SIDE = {"robot_left_blue.png", "robot_right_red.png"};
    private static final String FULLTIME_LABEL_NO = "Preliminaries Game";
    private static final String FULLTIME_LABEL_YES = "Play-off Game";
    private static final String FULLTIME_LABEL_HL_NO = "Normal Game";
    private static final String FULLTIME_LABEL_HL_YES = "Knock-Out Game";
    private static final String FULLSCREEN_LABEL = "Fullscreen";
    private static final String COLOR_CHANGE_LABEL = "Auto color change";
    private static final String START_LABEL = "Start";

    /** A countdown latch which fires when the UI has been closed and the game should start. */
    private final CountDownLatch latch = new CountDownLatch(1);

    private final StartOptions options;

    private JFrame frame;
    private JLabel[] teamLogoLabel = new JLabel[2];
    private ImagePanel[] teamBackgroundPanel = new ImagePanel[2];
    private JComboBox[] teamCombo = new JComboBox[2];
    private JRadioButton kickOffBlue;
    private JRadioButton kickOffRed;
    private JComboBox<League> leagueCombo;
    private JRadioButton nofulltime;
    private JRadioButton fulltime;
    private Checkbox fullscreen;
    private Checkbox autoColorChange;
    private JButton startButton;

    /**
     * Shows the StartInput dialog and blocks until the user clicks 'start' or closes the window.
     *
     * @param options the set of options to bind this UI to
     */
    public static void showDialog(@NotNull StartOptions options)
    {
        StartInput input = new StartInput(options);
        try {
            // Block until the UI is done with
            input.latch.await();
        } catch (InterruptedException e) {
            System.exit(1);
        }
        input.close();
    }

    private StartInput(@NotNull final StartOptions options)
    {
        this.options = options;

        buildUI();

        // Set the league to the one specified in starting
        // options, which in turn sets the selected teams
        setLeague(options.league);

        frame.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    private void buildUI()
    {
        frame = new JFrame(WINDOW_TITLE);
        frame.setIconImage(new ImageIcon(Config.ICONS_PATH + "window_icon.png").getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 0, STANDARD_SPACE));
        // Centre window on user's screen
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        frame.setLocation((width-WINDOW_WIDTH)/2, (height-WINDOW_HEIGHT)/2);

        // Create the team selection panels
        List<Team> teams = options.league.teams();
        for (int i = 0; i < 2; i++) {
            final int side = i; // copy for closure
            ImagePanel container = new ImagePanel(ImagePanel.Mode.TopCentre);
            container.setPreferredSize(new Dimension(WINDOW_WIDTH / 2 - STANDARD_SPACE, TEAMS_HEIGHT));
            container.setOpaque(true);
            container.setLayout(new BorderLayout());
            teamLogoLabel[side] = new JLabel();
            teamLogoLabel[side].setHorizontalAlignment(SwingConstants.CENTER);
            teamLogoLabel[side].setVerticalAlignment(SwingConstants.CENTER);
            container.add(teamLogoLabel[side], BorderLayout.CENTER);
            teamCombo[side] = new JComboBox(teams.toArray());
            teamCombo[side].setRenderer(new TeamListCellRenderer());
            container.add(teamCombo[side], BorderLayout.SOUTH);
            teamCombo[side].addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    doWithWait(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            setTeam(side, (Team)teamCombo[side].getSelectedItem());
                        }
                    });
                }
            });
            frame.add(container);

            teamBackgroundPanel[side] = container;
        }

        // Create kick off selection controls
        JPanel optionsKickOff = new JPanel();
        kickOffBlue = new JRadioButton();
        kickOffBlue.setText("Kick off blue");
        kickOffRed = new JRadioButton();
        kickOffRed.setText("Kick off red");
        kickOffBlue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStateButton();
            }});
        kickOffRed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStateButton();
            }});
        ButtonGroup kickOffGroup = new ButtonGroup();
        kickOffGroup.add(kickOffBlue);
        kickOffGroup.add(kickOffRed);
        optionsKickOff.add(kickOffBlue);
        optionsKickOff.add(kickOffRed);
        if (options.initialKickOffTeam == TeamColor.Blue)
            kickOffBlue.setSelected(true);
        else if (options.initialKickOffTeam == TeamColor.Red)
            kickOffRed.setSelected(true);
        optionsKickOff.setPreferredSize(new Dimension(WINDOW_WIDTH-2*STANDARD_SPACE, OPTIONS_HEIGHT));
        optionsKickOff.setLayout(new FlowLayout(FlowLayout.CENTER));
        frame.add(optionsKickOff);

        // Create the left-hand control panel, containing 'full screen' and 'auto colour change' options
        JPanel optionsLeft = new JPanel();
        optionsLeft.setPreferredSize(new Dimension(WINDOW_WIDTH/2-2*STANDARD_SPACE, OPTIONS_CONTAINER_HEIGHT));
        optionsLeft.setLayout(new FlowLayout(FlowLayout.CENTER));
        frame.add(optionsLeft);

        JPanel fullscreenPanel = new JPanel();
        fullscreenPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        optionsLeft.add(fullscreenPanel);
        JPanel autoColorChangePanel = new JPanel();
        autoColorChangePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        optionsLeft.add(autoColorChangePanel);

        fullscreen = new Checkbox(FULLSCREEN_LABEL);
        fullscreen.setPreferredSize(new Dimension(FULLSCREEN_WIDTH, OPTIONS_HEIGHT));
        fullscreen.setState(options.fullScreenMode);
        fullscreenPanel.add(fullscreen);

        autoColorChange = new Checkbox(COLOR_CHANGE_LABEL);
        autoColorChange.setPreferredSize(new Dimension(FULLSCREEN_WIDTH, OPTIONS_HEIGHT));
        autoColorChange.setState(options.colorChangeAuto);
        autoColorChangePanel.add(autoColorChange);

        // Create the right-hand control panel, containing 'league' combo and 'normal vs knockout/playoff' radios
        JPanel optionsRight = new JPanel();
        optionsRight.setPreferredSize(new Dimension(WINDOW_WIDTH/2-2*STANDARD_SPACE, OPTIONS_CONTAINER_HEIGHT));
        frame.add(optionsRight);
        Dimension optionsDim = new Dimension(WINDOW_WIDTH/3-2*STANDARD_SPACE, OPTIONS_HEIGHT);
        leagueCombo = new JComboBox<League>();
        leagueCombo.setRenderer(new LeagueListCellRenderer());
        for (League l : League.getAllLeagues())
        {
            leagueCombo.addItem(l);
            if (l == options.league) {
                leagueCombo.setSelectedItem(l);
            }
        }
        leagueCombo.setPreferredSize(optionsDim);
        leagueCombo.addActionListener(new ActionListener()
             {
                 @Override
                 public void actionPerformed(ActionEvent e)
                 {
                     doWithWait(new Runnable()
                     {
                         @Override
                         public void run()
                         {
                             setLeague((League)StartInput.this.leagueCombo.getSelectedItem());
                         }
                     });
                 }
             });
        optionsRight.add(leagueCombo);
        nofulltime = new JRadioButton();
        nofulltime.setPreferredSize(optionsDim);
        fulltime = new JRadioButton();
        fulltime.setPreferredSize(optionsDim);
        ButtonGroup fullTimeGroup = new ButtonGroup();
        fullTimeGroup.add(nofulltime);
        fullTimeGroup.add(fulltime);
        optionsRight.add(nofulltime);
        optionsRight.add(fulltime);
        nofulltime.addActionListener(new ActionListener() {
            @Override
                public void actionPerformed(ActionEvent e) {
                    updateStateButton();
                }});
        fulltime.addActionListener(new ActionListener() {
            @Override
                public void actionPerformed(ActionEvent e) {
                    updateStateButton();
                }});
        if (options.playOff != null) {
            if (options.playOff)
                fulltime.setSelected(true);
            else
                nofulltime.setSelected(true);
        }

        // Create the start button
        startButton = new JButton(START_LABEL);
        startButton.setPreferredSize(new Dimension(WINDOW_WIDTH / 3 - 2 * STANDARD_SPACE, START_HEIGHT));
        startButton.setEnabled(false);
        frame.add(startButton);
        startButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // The 'start' button was clicked
                options.playOff = fulltime.isSelected() && fulltime.isVisible();
                options.fullScreenMode = fullscreen.getState();
                options.colorChangeAuto = autoColorChange.getState();
                if (kickOffBlue.isSelected())
                    options.initialKickOffTeam = TeamColor.Blue;
                else if (kickOffRed.isSelected())
                    options.initialKickOffTeam = TeamColor.Red;
                else
                    throw new AssertionError("Start button should not be enabled if no kick off team selected.");
                latch.countDown();
            }
        });

        // Set window size, execute layout, then show on screen
        frame.getContentPane().setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        frame.pack();
    }

    @SuppressWarnings("unchecked")
    private void setLeague(@NotNull League league)
    {
        options.league = league;

        // Show/hide checkboxes
        if (league == League.SPLDropIn) {
            nofulltime.setVisible(false);
            fulltime.setVisible(false);
            autoColorChange.setVisible(false);
        } else if (league == League.SPL) {
            nofulltime.setVisible(true);
            fulltime.setVisible(true);
            autoColorChange.setVisible(false);
            nofulltime.setText(FULLTIME_LABEL_NO);
            fulltime.setText(FULLTIME_LABEL_YES);
        } else {
            nofulltime.setVisible(true);
            fulltime.setVisible(true);
            autoColorChange.setVisible(true);
            nofulltime.setText(FULLTIME_LABEL_HL_NO);
            fulltime.setText(FULLTIME_LABEL_HL_YES);
            autoColorChange.setState(options.colorChangeAuto);
        }

        for (int side = 0; side < 2; side++) {
            // Update background image
            String backgroundImagePath = Config.ICONS_PATH + league.getDirectoryName() + "/" + BACKGROUND_SIDE[side];
            teamBackgroundPanel[side].setImage(new ImageIcon(backgroundImagePath).getImage());

            // Determine the team number, or use zero if unspecified at this point
            int teamNumber = this.options.teamNumberByIndex(side);
            if (teamNumber == -1)
                teamNumber = 0;

            // Suspend change notification on combo during population
            assert(teamCombo[side].getActionListeners().length == 1);
            ActionListener actionListener = teamCombo[side].getActionListeners()[0];
            teamCombo[side].removeActionListener(actionListener);

            // Populate team combo box
            List<Team> teams1 = this.options.league.teams();
            teamCombo[side].removeAllItems();
            if (this.options.league.settings().dropInPlayerMode) {
                // In SPL drop in games, there are only two teams (red and blue), plus we add team 0 (invisibles).
                assert(teams1.size() == 3);
                teamCombo[side].addItem(teams1.get(0));
                teamCombo[side].addItem(teams1.get(side == 0 ?  1 : 2));
                // TODO support setting of drop-in team numbers from StartOptions (for now just set to zero)
                this.options.setTeamNumberByIndex(side, (byte)0);
                teamNumber = 0;
            } else {
                boolean found = false;
                for (int j = 0; j < teams1.size(); j++) {
                    teamCombo[side].addItem(teams1.get(j));
                    if (teams1.get(j).getNumber() == teamNumber) {
                        teamCombo[side].setSelectedIndex(j);
                        found = true;
                    }
                }
                if (!found) {
                    this.options.setTeamNumberByIndex(side, (byte)0);
                    teamNumber = 0;
                }
            }

            // Reinstate change notification
            teamCombo[side].addActionListener(actionListener);

            setTeam(side, this.options.league.getTeam(teamNumber));
        }

        updateStateButton();
    }

    /**
     * Assigns a team to a side of the UI.
     * 
     * @param side the side (0=left, 1=right)
     * @param team the team to assign to the specified side
     */ 
    private void setTeam(int side, @NotNull Team team)
    {
        // Update the start options
        if (side == 0)
            options.teamNumberBlue = (byte)team.getNumber();
        else
            options.teamNumberRed = (byte)team.getNumber();

        // Update the team image
        BufferedImage logoImage = team.getLogoImage();
        if (logoImage != null) {
            float scaleFactor = logoImage.getWidth(null) > logoImage.getHeight(null)
                    ? (float)IMAGE_SIZE / logoImage.getWidth(null)
                    : (float)IMAGE_SIZE / logoImage.getHeight(null);

            // Don't zoom the image in, only allow reduction to fit
            if (scaleFactor < 1.0f) {
                Image scaledImage = logoImage;

                // getScaledInstance/SCALE_SMOOTH does not work with all color models, so we need to convert image
                if (logoImage.getType() != BufferedImage.TYPE_INT_ARGB &&
                    logoImage.getType() != BufferedImage.TYPE_3BYTE_BGR &&
                    logoImage.getType() != BufferedImage.TYPE_4BYTE_ABGR) {
                    scaledImage = new BufferedImage(logoImage.getWidth(), logoImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics g = ((BufferedImage)scaledImage).createGraphics();
                    g.drawImage(logoImage, 0, 0, null);
                    g.dispose();
                }

                scaledImage = scaledImage.getScaledInstance(
                        (int)(logoImage.getWidth(null) * scaleFactor),
                        (int)(logoImage.getHeight(null) * scaleFactor),
                        Image.SCALE_SMOOTH);

                teamLogoLabel[side].setIcon(new ImageIcon(scaledImage));
            } else {
                teamLogoLabel[side].setIcon(new ImageIcon(logoImage));
            }
        } else {
            // Team doesn't have a logo
            teamLogoLabel[side].setIcon(null);
        }
        teamLogoLabel[side].repaint();

        // Update whether the start button is enabled
        updateStateButton();
    }
    
    /**
     * Enables the start button, if the conditions are ok, otherwise disables it.
     */
    private void updateStateButton()
    {
        boolean isEnabled = options.teamNumberBlue != options.teamNumberRed;
        isEnabled &= fulltime.isSelected() || nofulltime.isSelected() || !fulltime.isVisible();
        isEnabled &= kickOffBlue.isSelected() || kickOffRed.isSelected();
        startButton.setEnabled(isEnabled);
    }

    private void close()
    {
        frame.dispose();
    }

    private void doWithWait(Runnable action)
    {
        Cursor cursor = frame.getCursor();
        try {
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            action.run();
        } finally {
            frame.setCursor(cursor);
        }
    }
}
