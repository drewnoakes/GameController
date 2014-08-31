package controller.ui;

import common.annotations.NotNull;
import controller.GameOptions;
import controller.Config;
import controller.ui.controls.ImagePanel;
import controller.ui.controls.LeagueListCellRenderer;
import controller.ui.controls.TeamListCellRenderer;
import data.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import javax.swing.*;

/**
 * Allows specification of {@link GameOptions} to apply during a game.
 *
 * @author Michel Bartsch
 * @author Drew Noakes https://drewnoakes.com
 */
public class GameOptionsUI
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
    private static final int LEFT_OPTIONS_WIDTH = 160;

    private static final String FULLTIME_LABEL_NO = "Preliminaries Game";
    private static final String FULLTIME_LABEL_YES = "Play-off Game";
    private static final String FULLTIME_LABEL_HL_NO = "Normal Game";
    private static final String FULLTIME_LABEL_HL_YES = "Knock-Out Game";
    private static final String START_LABEL = "Start";

    /** A countdown latch which fires when the UI has been closed and the game should start. */
    private final CountDownLatch latch = new CountDownLatch(1);

    private final Pair<Team> teams;
    private final UIOrientation orientation;
    private final String broadcastAddress;
    private JFrame frame;
    private Pair<String> backgroundImages;
    private Pair<JLabel> teamLogoLabels;
    private Pair<ImagePanel> teamBackgroundPanels;
    private Pair<JComboBox<Team>> teamCombos;
    private Pair<JRadioButton> kickOffRadios;
    private JComboBox<League> leagueCombo;
    private JRadioButton nofulltime;
    private JRadioButton fulltime;
    private JCheckBox fullscreen;
    private JCheckBox changeColoursEachPeriodCheckbox;
    private JButton startButton;

    /**
     * Shows the StartInput dialog and blocks until the user clicks 'start' or closes the window.
     *
     * @param options the set of options to bind this UI to
     */
    public static GameOptions configure(@NotNull GameOptions options)
    {
        GameOptionsUI ui = new GameOptionsUI(options);
        try {
            // Block until the UI is done with
            ui.latch.await();
        } catch (InterruptedException e) {
            System.exit(1);
        }
        ui.close();

        return ui.createGameOptions();
    }

    @NotNull
    private GameOptions createGameOptions()
    {
        // The 'start' button was clicked
        boolean isPlayOff = fulltime.isSelected() && fulltime.isVisible();
        boolean isFullScreen = fullscreen.isSelected();
        boolean changeColoursEachPeriod = changeColoursEachPeriodCheckbox.isSelected();

        TeamColor initialKickOffColor = null;
        for (TeamColor color : TeamColor.both()) {
            if (kickOffRadios.get(color).isSelected())
                initialKickOffColor = color;
        }

        assert(initialKickOffColor != null);

        League league = (League)leagueCombo.getSelectedItem();

        return new GameOptions(broadcastAddress, isFullScreen, league, isPlayOff, orientation, teams, initialKickOffColor, changeColoursEachPeriod);
    }

    private GameOptionsUI(@NotNull final GameOptions options)
    {
        orientation = options.orientation;
        teams = options.teams;
        broadcastAddress = options.broadcastAddress;

        buildUI(options.initialKickOffColor, options.isFullScreen, options.changeColoursEachPeriod, options.isPlayOff);

        // Set the league to the one specified in starting
        // options, which in turn sets the selected teams
        setLeague(options.league);

        frame.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    private void buildUI(TeamColor initialKickOffColor, boolean isFullScreen, boolean changeColoursEachPeriod, Boolean isPlayOff)
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

        // Per-team stuff

        backgroundImages = new Pair<String>(orientation, "robot_left_blue.png", "robot_right_red.png");
        teamLogoLabels = new Pair<JLabel>(orientation, new JLabel(), new JLabel());
        teamBackgroundPanels = new Pair<ImagePanel>(orientation, new ImagePanel(ImagePanel.Mode.TopCentre), new ImagePanel(ImagePanel.Mode.TopCentre));
        teamCombos = new Pair<JComboBox<Team>>(orientation, new JComboBox<Team>(), new JComboBox<Team>());

        // Create the team selection panels
        for (final UISide side : UISide.both()) {
            ImagePanel container = teamBackgroundPanels.get(side);
            container.setPreferredSize(new Dimension(WINDOW_WIDTH / 2 - STANDARD_SPACE, TEAMS_HEIGHT));
            container.setOpaque(true);
            container.setLayout(new BorderLayout());

            JLabel teamLogoLabel = teamLogoLabels.get(side);
            teamLogoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            teamLogoLabel.setVerticalAlignment(SwingConstants.CENTER);
            container.add(teamLogoLabel, BorderLayout.CENTER);
            final JComboBox<Team> teamCombo = teamCombos.get(side);
            teamCombo.setRenderer(new TeamListCellRenderer());
            container.add(teamCombo, BorderLayout.SOUTH);
            teamCombo.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    doWithWait(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            setTeam(side, (Team)teamCombo.getSelectedItem());
                        }
                    });
                }
            });
            frame.add(container);
        }

        // Create kick off selection controls
        JPanel optionsKickOff = new JPanel();
        kickOffRadios = new Pair<JRadioButton>(orientation, new JRadioButton(), new JRadioButton());
        kickOffRadios.get(TeamColor.Blue).setText("Kick off blue");
        kickOffRadios.get(TeamColor.Red).setText("Kick off red");
        ButtonGroup kickOffGroup = new ButtonGroup();
        for (UISide side : UISide.both()) {
            JRadioButton radio = kickOffRadios.get(side);
            radio.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    updateStateButton();
                }
            });
            kickOffGroup.add(radio);
            optionsKickOff.add(radio);
            if (orientation.getSide(initialKickOffColor) == side)
                radio.setSelected(true);
        }

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

        fullscreen = new JCheckBox("Fullscreen");
        fullscreen.setPreferredSize(new Dimension(LEFT_OPTIONS_WIDTH, OPTIONS_HEIGHT));
        fullscreen.setHorizontalAlignment(SwingConstants.LEFT);
        fullscreen.setSelected(isFullScreen);
        fullscreenPanel.add(fullscreen);

        changeColoursEachPeriodCheckbox = new JCheckBox("Auto color change");
        changeColoursEachPeriodCheckbox.setPreferredSize(new Dimension(LEFT_OPTIONS_WIDTH, OPTIONS_HEIGHT));
        changeColoursEachPeriodCheckbox.setSelected(changeColoursEachPeriod);
        autoColorChangePanel.add(changeColoursEachPeriodCheckbox);

        // Create the right-hand control panel, containing 'league' combo and 'normal vs knockout/playoff' radios
        JPanel optionsRight = new JPanel();
        optionsRight.setPreferredSize(new Dimension(WINDOW_WIDTH/2-2*STANDARD_SPACE, OPTIONS_CONTAINER_HEIGHT));
        frame.add(optionsRight);
        Dimension optionsDim = new Dimension(WINDOW_WIDTH/3-2*STANDARD_SPACE, OPTIONS_HEIGHT);
        leagueCombo = new JComboBox<League>();
        leagueCombo.setRenderer(new LeagueListCellRenderer());
        for (League l : League.getAllLeagues()) {
            leagueCombo.addItem(l);
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
                             setLeague((League)GameOptionsUI.this.leagueCombo.getSelectedItem());
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
                }
            });
        fulltime.addActionListener(new ActionListener() {
            @Override
                public void actionPerformed(ActionEvent e) {
                    updateStateButton();
                }
            });
        if (isPlayOff != null) {
            if (isPlayOff)
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
        leagueCombo.setSelectedItem(league);

        // Show/hide checkboxes
        if (league == League.SPLDropIn) {
            nofulltime.setVisible(false);
            fulltime.setVisible(false);
            changeColoursEachPeriodCheckbox.setVisible(false);
        } else if (league == League.SPL) {
            nofulltime.setVisible(true);
            fulltime.setVisible(true);
            changeColoursEachPeriodCheckbox.setVisible(false);
            nofulltime.setText(FULLTIME_LABEL_NO);
            fulltime.setText(FULLTIME_LABEL_YES);
        } else {
            nofulltime.setVisible(true);
            fulltime.setVisible(true);
            changeColoursEachPeriodCheckbox.setVisible(true);
            nofulltime.setText(FULLTIME_LABEL_HL_NO);
            fulltime.setText(FULLTIME_LABEL_HL_YES);
        }

        for (UISide side : UISide.both()) {
            // Update background image
            String backgroundImagePath = Config.ICONS_PATH + league.getDirectoryName() + "/" + backgroundImages.get(side);
            teamBackgroundPanels.get(side).setImage(new ImageIcon(backgroundImagePath).getImage());

            // Determine the team number, or use zero if unspecified at this point
            int teamNumber = this.teams.get(side).getNumber();
            if (teamNumber == -1)
                teamNumber = 0;

            // Suspend change notification on combo during population
            assert(teamCombos.get(side).getActionListeners().length == 1);
            ActionListener actionListener = teamCombos.get(side).getActionListeners()[0];
            teamCombos.get(side).removeActionListener(actionListener);

            // Populate team combo box
            List<Team> teams = league.teams();
            teamCombos.get(side).removeAllItems();
            if (league.settings().dropInPlayerMode) {
                // In SPL drop in games, there are only two teams (red and blue), plus we add team 0 (invisibles).
                assert(teams.size() == 3);
                teamCombos.get(side).addItem(teams.get(0));
                teamCombos.get(side).addItem(teams.get(side == UISide.Left ?  1 : 2));
                // TODO support setting of drop-in team numbers from StartOptions (for now just set to zero)
                teamNumber = 0;
            } else {
                for (Team team : teams) {
                    teamCombos.get(side).addItem(team);
                }
                if (!league.hasTeamNumber(teamNumber)) {
                    // Just set to the first team in the league
                    teamNumber = 0;
                }
            }

            // Reinstate change notification
            teamCombos.get(side).addActionListener(actionListener);

            final Team team = league.getTeam(teamNumber);
            assert(team != null);
            setTeam(side, team);
        }

        updateStateButton();
    }

    /**
     * Assigns a team to a side of the UI.
     * 
     * @param side the side (0=left, 1=right)
     * @param team the team to assign to the specified side
     */ 
    private void setTeam(UISide side, @NotNull Team team)
    {
        // Update the start options
        teams.set(side, team);

        teamCombos.get(side).setSelectedItem(team);

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

                teamLogoLabels.get(side).setIcon(new ImageIcon(scaledImage));
            } else {
                teamLogoLabels.get(side).setIcon(new ImageIcon(logoImage));
            }
        } else {
            // Team doesn't have a logo
            teamLogoLabels.get(side).setIcon(null);
        }
        teamLogoLabels.get(side).repaint();

        // Update whether the start button is enabled
        updateStateButton();
    }
    
    /**
     * Enables the start button, if the conditions are ok, otherwise disables it.
     */
    private void updateStateButton()
    {
        // Must select different teams
        boolean isEnabled = teams.get(UISide.Left) != teams.get(UISide.Right);

        // Must select a full-time option, if available
        isEnabled &= fulltime.isSelected() || nofulltime.isSelected() || !fulltime.isVisible();

        // Must select a kick off team
        isEnabled &= kickOffRadios.get(UISide.Left).isSelected() || kickOffRadios.get(UISide.Right).isSelected();

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
