package controller.ui;

import controller.StartOptions;
import data.Rules;
import data.SPL;
import data.SPLDropIn;
import data.Teams;
import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import javax.swing.*;


/**
 * @author Michel Bartsch
 * 
 * This is only to be on starting the program to get starting input.
 */
public class StartInput extends JFrame
{
    /**
     * Some constants defining this GUI`s appearance as their names say.
     * Feel free to change them and see what happens.
     */
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
    private static final String ICONS_PATH = "config/icons/";
    private static final String[] BACKGROUND_SIDE = {"robot_left_blue.png",
                                                        "robot_right_red.png"};
    private static final String FULLTIME_LABEL_NO = "Preliminaries Game";
    private static final String FULLTIME_LABEL_YES = "Play-off Game";
    private static final String FULLTIME_LABEL_HL_NO = "Normal Game";
    private static final String FULLTIME_LABEL_HL_YES = "Knock-Out Game";
    private static final String FULLSCREEN_LABEL = "Fullscreen";
    private static final String COLOR_CHANGE_LABEL = "Auto color change";
    private static final String START_LABEL = "Start";
    /** A countdown latch which fires when the UI has been closed and the game should start. */
    private CountDownLatch latch = new CountDownLatch(1);

    private StartOptions options;

    /** All the components of this GUI. */
    private ImagePanel[] teamContainer = new ImagePanel[2];
    private ImageIcon[] teamIcon = new ImageIcon[2];
    private JLabel[] teamIconLabel = new JLabel[2];
    private JComboBox[] team = new JComboBox[2];
    private JRadioButton kickOffBlue;
    private JRadioButton kickOffRed;
    private JComboBox league;
    private JRadioButton nofulltime;
    private JRadioButton fulltime;
    private Checkbox fullscreen;
    private Checkbox autoColorChange;
    private JButton start;

    /**
     * Shows the StartInput dialog and blocks until the user clicks 'start' or closes the window.
     *
     * @param options the set of options to bind this UI to
     */
    public static void showDialog(StartOptions options)
    {
        StartInput input = new StartInput(options);
        try {
            // Block until the UI is done with
            input.latch.await();
        } catch (InterruptedException e) {
            System.exit(1);
        }
        input.dispose();
    }

    /**
     * Creates a new StartInput.
     *
     * @param options the set of options to bind this UI to
     */
    @SuppressWarnings("unchecked")
    private StartInput(final StartOptions options)
    {
        super(WINDOW_TITLE);

        this.options = options;

        // Centre window on user's screen
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        setLocation((width-WINDOW_WIDTH)/2, (height-WINDOW_HEIGHT)/2);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, STANDARD_SPACE));

        // Create the team selection panels
        String[] teams = getShortTeams();
        for (int i=0; i<2; i++) {
            String backgroundImagePath = ICONS_PATH + Rules.league.leagueDirectory + "/" + BACKGROUND_SIDE[i];
            teamContainer[i] = new ImagePanel(new ImageIcon(backgroundImagePath).getImage());
            teamContainer[i].setPreferredSize(new Dimension(WINDOW_WIDTH/2-STANDARD_SPACE, TEAMS_HEIGHT));
            teamContainer[i].setOpaque(true);
            teamContainer[i].setLayout(new BorderLayout());
            add(teamContainer[i]);
            setTeamIcon(i, 0);
            teamIconLabel[i] = new JLabel(teamIcon[i]);
            teamContainer[i].add(teamIconLabel[i], BorderLayout.CENTER);
            team[i] = new JComboBox(teams);
            teamContainer[i].add(team[i], BorderLayout.SOUTH);
        }
        team[0].addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    Object selected = team[0].getSelectedItem();
                    if (selected == null) {
                        return;
                    }
                    options.teamNumberBlue = Byte.valueOf(((String)selected).split(" \\(")[1].split("\\)")[0]);
                    setTeamIcon(0, options.teamNumberBlue);
                    teamIconLabel[0].setIcon(teamIcon[0]);
                    teamIconLabel[0].repaint();
                    startEnabling();
                }
            }
        );
        team[1].addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    Object selected = team[1].getSelectedItem();
                    if (selected == null) {
                        return;
                    }
                    options.teamNumberRed = Byte.valueOf(((String)selected).split(" \\(")[1].split("\\)")[0]);
                    setTeamIcon(1, options.teamNumberRed);
                    teamIconLabel[1].setIcon(teamIcon[1]);
                    teamIconLabel[1].repaint();
                    startEnabling();
                }
            }
        );

        // Create kick off selection controls
        JPanel optionsKickOff = new JPanel();
        kickOffBlue = new JRadioButton();
        kickOffBlue.setText("Kick off blue");
        kickOffRed = new JRadioButton();
        kickOffRed.setText("Kick off red");
        kickOffBlue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startEnabling();
            }});
        kickOffRed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startEnabling();
            }});
        ButtonGroup kickOffGroup = new ButtonGroup();
        kickOffGroup.add(kickOffBlue);
        kickOffGroup.add(kickOffRed);
        optionsKickOff.add(kickOffBlue);
        optionsKickOff.add(kickOffRed);
        if (options.kickOffTeamIndex == 0)
            kickOffBlue.setSelected(true);
        else if (options.kickOffTeamIndex == 1)
            kickOffRed.setSelected(true);
        optionsKickOff.setPreferredSize(new Dimension(WINDOW_WIDTH-2*STANDARD_SPACE, OPTIONS_HEIGHT));
        optionsKickOff.setLayout(new FlowLayout(FlowLayout.CENTER));
        add(optionsKickOff);

        // Create the left-hand control panel, containing 'full screen' and 'auto colour change' options
        JPanel optionsLeft = new JPanel();
        optionsLeft.setPreferredSize(new Dimension(WINDOW_WIDTH/2-2*STANDARD_SPACE, OPTIONS_CONTAINER_HEIGHT));
        optionsLeft.setLayout(new FlowLayout(FlowLayout.CENTER));
        add(optionsLeft);

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
        add(optionsRight);
        Dimension optionsDim = new Dimension(WINDOW_WIDTH/3-2*STANDARD_SPACE, OPTIONS_HEIGHT);
        league = new JComboBox();
        for (int i=0; i < Rules.LEAGUES.length; i++) {
            league.addItem(Rules.LEAGUES[i].leagueName);
            if (Rules.LEAGUES[i] == Rules.league) {
                league.setSelectedIndex(i);
            }
        }
        league.setPreferredSize(optionsDim);
        league.addActionListener(new ActionListener()
            {
            @Override
                public void actionPerformed(ActionEvent e)
                {
                    if (e != null) { // not initial setup
                        for (int i=0; i < Rules.LEAGUES.length; i++) {
                            if (Rules.LEAGUES[i].leagueName.equals(league.getSelectedItem())) {
                                Rules.league = Rules.LEAGUES[i];
                                break;
                            }
                        }
                    }
                    if (Rules.league instanceof SPLDropIn) {
                        nofulltime.setVisible(false);
                        fulltime.setVisible(false);
                        autoColorChange.setVisible(false);
                    } else {
                        nofulltime.setVisible(true);
                        fulltime.setVisible(true);
                        if (Rules.league instanceof SPL) {
                            nofulltime.setText(FULLTIME_LABEL_NO);
                            fulltime.setText(FULLTIME_LABEL_YES);
                            autoColorChange.setVisible(false);
                        } else {
                            nofulltime.setText(FULLTIME_LABEL_HL_NO);
                            fulltime.setText(FULLTIME_LABEL_HL_YES);
                            autoColorChange.setState(Rules.league.colorChangeAuto);
                            autoColorChange.setVisible(true);
                        }
                    }
                    showAvailableTeams();
                    startEnabling();
                }
            }
        );
        optionsRight.add(league);
        nofulltime = new JRadioButton();
        nofulltime.setPreferredSize(optionsDim);
        fulltime = new JRadioButton();
        fulltime.setPreferredSize(optionsDim);
        ButtonGroup fulltimeGroup = new ButtonGroup();
        fulltimeGroup.add(nofulltime);
        fulltimeGroup.add(fulltime);
        optionsRight.add(nofulltime);
        optionsRight.add(fulltime);
        nofulltime.addActionListener(new ActionListener() {
            @Override
                public void actionPerformed(ActionEvent e) {
                    startEnabling();
                }});
        fulltime.addActionListener(new ActionListener() {
            @Override
                public void actionPerformed(ActionEvent e) {
                    startEnabling();
                }});
        if (options.playOff != null) {
            if (options.playOff)
                fulltime.setSelected(true);
            else
                nofulltime.setSelected(true);
        }

        // Create the start button
        start = new JButton(START_LABEL);
        start.setPreferredSize(new Dimension(WINDOW_WIDTH/3-2*STANDARD_SPACE, START_HEIGHT));
        start.setEnabled(false);
        add(start);
        start.addActionListener(new ActionListener() {
            @Override
                public void actionPerformed(ActionEvent e) {
                    options.playOff = fulltime.isSelected() && fulltime.isVisible();
                    options.fullScreenMode = fullscreen.getState();
                    options.colorChangeAuto = autoColorChange.getState();
                    if (kickOffBlue.isSelected())
                        options.kickOffTeamIndex = 0;
                    else if (kickOffRed.isSelected())
                        options.kickOffTeamIndex = 1;
                    else
                        throw new AssertionError("Start button should not be enabled if no kick off team selected.");
                    latch.countDown();
                }});

        // Trigger selection of the league
        league.getActionListeners()[league.getActionListeners().length - 1].actionPerformed(null);

        // Set window size, perform layout, then show on screen
        getContentPane().setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        pack();
        setVisible(true);
    }

    /** Show in the combo box which teams are available for the selected league and competition. */
    private void showAvailableTeams() 
    {
        for (int i=0; i < 2; i++) {
            // Set background image according to active league
            String backgroundImagePath = ICONS_PATH + Rules.league.leagueDirectory + "/" + BACKGROUND_SIDE[i];
            teamContainer[i].setImage(new ImageIcon(backgroundImagePath).getImage());

            // Determine the team number, or use zero if unspecified at this point
            int teamNumber = options.teamNumberByIndex(i);
            if (teamNumber == -1)
                teamNumber = 0;

            // Suspend change notification on combo during population
            assert(team[i].getActionListeners().length == 1);
            ActionListener actionListener = team[i].getActionListeners()[0];
            team[i].removeActionListener(actionListener);

            // Populate combo box
            String[] names = getShortTeams();
            team[i].removeAllItems();
            if (Rules.league.dropInPlayerMode) {
                // In SPL drop in games, there are only two teams (red and blue), plus we add team 0 (invisibles).
                assert(names.length == 3);
                team[i].addItem(names[0]);
                team[i].addItem(names[i == 0 ?  1 : 2]);
                // TODO support setting of drop-in team numbers from StartOptions (for now just set to zero)
                options.setTeamNumberByIndex(i, (byte)0);
                teamNumber = 0;
            } else {
                boolean found = false;
                for (int j=0; j < names.length; j++) {
                    team[i].addItem(names[j]);
                    // TODO this test is a bit ugly -- need a better (non-string) representation of teams
                    if (names[j].contains("(" + teamNumber + ")")) {
                        team[i].setSelectedIndex(j);
                        found = true;
                    }
                }
                if (!found) {
                    options.setTeamNumberByIndex(i, (byte)0);
                    teamNumber = 0;
                }
            }

            // Reinstate change notification
            team[i].addActionListener(actionListener);

            // Set team icon
            setTeamIcon(i, teamNumber);
            teamIconLabel[i].setIcon(teamIcon[i]);
            teamIconLabel[i].repaint();
        }
    }

    /**
     * Calculates an array that contains only the existing Teams of the current league.
     * 
     * @return  Short teams array with numbers
     */ 
    private String[] getShortTeams()
    {
        String[] fullTeams = Teams.getNames(true);
        String[] out;
        int k = 0;
        for (String fullTeam : fullTeams) {
            if (fullTeam != null) {
                k++;
            }
        }
        out = new String[k];
        k = 0;
        for (String fullTeam : fullTeams) {
            if (fullTeam != null) {
                out[k++] = fullTeam;
            }
        }

        Arrays.sort(out, 1, out.length, String.CASE_INSENSITIVE_ORDER);

        return out;
    }
    
    /**
     * Sets the Team-Icon on the GUI.
     * 
     * @param side      The side (0=left, 1=right)
     * @param team      The number of the Team
     */ 
    private void setTeamIcon(int side, int team)
    {
        teamIcon[side] = new ImageIcon(Teams.getIcon(team));
        float scaleFactor;
        if (teamIcon[side].getImage().getWidth(null) > teamIcon[side].getImage().getHeight(null)) {
            scaleFactor = (float)IMAGE_SIZE/teamIcon[side].getImage().getWidth(null);
        } else {
            scaleFactor = (float)IMAGE_SIZE/teamIcon[side].getImage().getHeight(null);
        }

        // getScaledInstance/SCALE_SMOOTH does not work with all color models, so we need to convert image
        BufferedImage image = (BufferedImage) teamIcon[side].getImage();
        if (image.getType() != BufferedImage.TYPE_INT_ARGB) {
            BufferedImage temp = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = temp.createGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            image = temp;
        }

        teamIcon[side].setImage(image.getScaledInstance(
                (int)(teamIcon[side].getImage().getWidth(null)*scaleFactor),
                (int)(teamIcon[side].getImage().getHeight(null)*scaleFactor),
                Image.SCALE_SMOOTH));
    }
    
    /**
     * Enables the start button, if the conditions are ok.
     */
    private void startEnabling()
    {
        boolean isEnabled = options.teamNumberBlue != options.teamNumberRed;
        isEnabled &= fulltime.isSelected() || nofulltime.isSelected() || !fulltime.isVisible();
        isEnabled &= kickOffBlue.isSelected() || kickOffRed.isSelected();
        start.setEnabled(isEnabled);
    }

    /**
     * @author Michel Bartsch
     * 
     * This is a normal JPanel, but it has a background image.
     */
    class ImagePanel extends JPanel
    {
        private static final long serialVersionUID = 1L;
        
        /** The image that is shown in the background. */
        private Image image;

        /**
         * Creates a new ImagePanel.
         * 
         * @param image     The Image to be shown in the background.
         */
        public ImagePanel(Image image)
        {
            this.image = image;
        }
        
        /**
         * Changes the background image.
         * 
         * @param image     Changes the image to this one.
         */
        public void setImage(Image image)
        {
            this.image = image;
        }
        
        /**
         * Paints this Component, should be called automatically.
         * 
         * @param g     This components graphical content.
         */
        @Override
        public void paintComponent(Graphics g)
        {
            if (super.isOpaque()) {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            g.drawImage(image, (getWidth()-image.getWidth(null))/2, 0, image.getWidth(null), image.getHeight(null), null);
        }
    }
}
