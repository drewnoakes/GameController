package controller.ui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

import common.EventHandler;
import common.TotalScaleLayout;
import common.annotations.NotNull;
import controller.Action;
import controller.Config;
import controller.Game;
import controller.GameState;
import controller.action.ActionBoard;
import controller.net.RobotOnlineStatus;
import controller.net.RobotWatcher;
import controller.ui.controls.*;
import controller.ui.controls.Button;
import data.*;

/**
 * The interface used during the play of a game. Allows control over the play mode, period, penalties and score.
 * Supports undo.
 *
 * @author Michel Bartsch
 * @author Drew Noakes https://drewnoakes.com
 */
public class ControllerUI
{
    private static final boolean IS_OSX = System.getProperty("os.name").contains("OS X");

    // Various UI constants

    private static final int WINDOW_WIDTH = 1024;
    private static final int WINDOW_HEIGHT = 768;
    private static final int STANDARD_FONT_SIZE = 17;
    private static final int TITLE_FONT_SIZE = 24;
    private static final String STANDARD_FONT = "Helvetica";
    private static final int GOALS_FONT_SIZE = 60;
    private static final int TIME_FONT_SIZE = 50;
    private static final int TIME_SUB_FONT_SIZE = 40;
    private static final int TIMEOUT_FONT_SIZE = 14;
    private static final int PLAY_MODE_FONT_SIZE = 12;

    private static final String WINDOW_TITLE = "RoboCup Game Controller";
    private static final String[][] BACKGROUND_SIDE = {{"robot_left_blue.png",
                                                        "robot_left_red.png"},
                                                       {"robot_right_blue.png",
                                                        "robot_right_red.png"}};
    private static final String BACKGROUND_MID = "field.png";
    private static final String BACKGROUND_CLOCK_SMALL = "time_ground_small.png";
    private static final String BACKGROUND_CLOCK = "time_ground.png";
    private static final String KICKOFF = "Kickoff";
    private static final String KICKOFF_PENALTY_SHOOTOUT = "P.-taker";
    private static final String PUSHES = "Pushes";
    private static final String SHOT = "Shot";
    private static final String SHOTS = "Shots";
    private static final String EJECTED = "Ejected";
    private static final String ONLINE = "wlan_status_green.png";
    private static final String OFFLINE = "wlan_status_red.png";
    private static final String HIGH_LATENCY = "wlan_status_yellow.png";
    private static final String UNKNOWN_ONLINE_STATUS = "wlan_status_grey.png";
    private static final String TIMEOUT = "Timeout";
    private static final String REFEREE_TIMEOUT = "Referee<br/>Timeout";
    private static final String STUCK = "Global <br/> Game <br/> Stuck";
    private static final String KICKOFF_GOAL = "Kickoff Goal";
    private static final String OUT = "Out";
    private static final String PLAY_MODE_INITIAL = "Initial";
    private static final String PLAY_MODE_READY = "Ready";
    private static final String PLAY_MODE_SET = "Set";
    private static final String PLAY_MODE_PLAY = "Play";
    private static final String PLAY_MODE_FINISH = "Finish";
    private static final String CLOCK_RESET = "reset.png";
    private static final String CLOCK_PAUSE = "pause.png";
    private static final String CLOCK_PLAY = "play.png";
    private static final String CLOCK_PLUS = "plus.png";
    private static final String FIRST_HALF = "First Half";
    private static final String SECOND_HALF = "Second Half";
    private static final String FIRST_HALF_SHORT = "1st Half";
    private static final String SECOND_HALF_SHORT = "2nd Half";
    private static final String FIRST_HALF_OVERTIME = "1st Extra";
    private static final String SECOND_HALF_OVERTIME = "2nd Extra";
    private static final String PENALTY_SHOOT = "Penalty Shots";
    private static final String PENALTY_SHOOT_SHORT = "Penalty";
    private static final String PEN_PUSHING = "Pushing";
    private static final String PEN_LEAVING = "Leaving the Field";
    private static final String PEN_FALLEN = "Fallen Robot";
    private static final String PEN_INACTIVE = "Inactive / Local Game Stuck";
    private static final String PEN_DEFENDER = "Illegal Defender";
    private static final String PEN_HOLDING = "Ball Holding";
    private static final String PEN_HANDS = "Hands";
    private static final String PEN_COACH_MOTION = "Coach Motion";
    private static final String PEN_PICKUP = "Pick-Up";
    private static final String PEN_MANIPULATION = "Ball Manipulation";
    private static final String PEN_PHYSICAL = "Physical Contact";
    private static final String PEN_DEFENSE = "Illegal Defense";
    private static final String PEN_ATTACK = "Illegal Attack";
    private static final String PEN_PICKUP_INCAPABLE = "Pickup/Incapable";
    private static final String PEN_SERVICE = "Service";
    private static final String PEN_SUBSTITUTE = "Substitute";
    private static final String PEN_SUBSTITUTE_SHORT = "Sub";
    private static final String DROP_BALL = "Dropped Ball";
    private static final String TEAMMATE_PUSHING = "Teammate Pushing";
    private static final String CANCEL = "Cancel";
    private static final String COACH = "Coach";
    private static final String BACKGROUND_BOTTOM = "timeline_ground.png";

    private static final Color COLOR_HIGHLIGHT = Color.YELLOW;
    private static final Color COLOR_STANDARD = new JButton().getBackground();

    private static final int UNPEN_HIGHLIGHT_SECONDS = 10;
    private static final int TIMEOUT_HIGHLIGHT_SECONDS = 10;
    private static final int FINISH_HIGHLIGHT_SECONDS = 10;
    private static final int KICKOFF_BLOCKED_HIGHLIGHT_SECONDS = 3;

    // Some attributes used in the GUI components

    private double lastSize = 0;
    private final ImageIcon clockImgReset;
    private final ImageIcon clockImgPlay;
    private final ImageIcon clockImgPause;
    private final ImageIcon clockImgPlus;
    private final ImageIcon lanOnline;
    private final ImageIcon lanHighLatency;
    private final ImageIcon lanOffline;
    private final ImageIcon lanUnknown;
    private final ImageIcon[][] backgroundSide;
    
    // All the components of this GUI
    private final PaintableFrame frame;

    private final ImagePanel[] sidePanel;
    private final JLabel[] nameLabels;
    private final JButton[] goalDecButton;
    private final JButton[] goalIncButton;
    private final JLabel[] goalCountLabels;
    private final JRadioButton[] kickOffRadioButtons;
    private final JLabel[] pushLabels;
    private final JButton[][] robotButtons;
    private final JLabel[][] robotLabel;
    private final JProgressBar[][] robotProgressBars;
    private final JToggleButton refereeTimeoutButton;
    private final JToggleButton[] timeOutButton;
    private JButton[] gameStuckButtons;
    private final JButton[] outButtons;
    private final JToggleButton initialPlayModeButton;
    private final JToggleButton readyPlayModeButton;
    private final JToggleButton setPlayModeButton;
    private final JToggleButton playPlayModeButton;
    private final JToggleButton finishPlayModeButton;
    private final ImageButton clockResetButton;
    private final JLabel clockLabel;
    private final JLabel secondaryTimeLabel;
    private final ImageButton incGameClockButton;
    private final ImageButton clockPauseButton;
    private final JToggleButton firstHalfPeriodButton;
    private final JToggleButton secondHalfPeriodButton;
    private JToggleButton firstHalfOvertimePeriodButton;
    private JToggleButton secondHalfOvertimePeriodButton;
    private final JToggleButton penaltyShootPeriodButton;
    private JToggleButton[] penaltyButtons;
    private JButton dropBallButton;
    private final JToggleButton[] undoButtons;
    private final JButton cancelUndoButton;

    private final RobotWatcher robotWatcher;
    private final Game game;

    /**
     * Initialises and displays the GUI.
     *
     * @param game the game to bind the UI to.
     * @param fullscreen whether the window should fill the screen.
     * @param robotWatcher the robot watcher which track the online status of bots.
     */
    public ControllerUI(@NotNull Game game, boolean fullscreen, @NotNull RobotWatcher robotWatcher)
    {
        this.game = game;
        this.robotWatcher = robotWatcher;

        game.gameStateChanged.subscribe(new EventHandler<GameState>()
        {
            @Override
            public void handle(GameState state)
            {
                ControllerUI.this.update(state);
            }
        });

        frame = new PaintableFrame(WINDOW_TITLE);
        ImageIcon img = new ImageIcon("~/rc/kid-size/game-controller/resources/icon.svg");
        frame.setIconImage(img.getImage());
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setResizable(true);
        frame.setIconImage(new ImageIcon(Config.ICONS_PATH + "window_icon.png").getImage());
        frame.beforePaint.subscribe(new EventHandler<Graphics>()
        {
            @Override
            public void handle(Graphics value)
            {
                updateFonts();
            }
        });

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        frame.setLocation((width-WINDOW_WIDTH)/2, (height-WINDOW_HEIGHT)/2);

        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e) {
                ControllerUI.this.game.requestShutdown();
            }
        });
        
        clockImgReset = new ImageIcon(Config.ICONS_PATH+CLOCK_RESET);
        clockImgPlay = new ImageIcon(Config.ICONS_PATH+CLOCK_PLAY);
        clockImgPause = new ImageIcon(Config.ICONS_PATH+CLOCK_PAUSE);
        clockImgPlus = new ImageIcon(Config.ICONS_PATH+CLOCK_PLUS);
        lanOnline = new ImageIcon(Config.ICONS_PATH+ONLINE);
        lanHighLatency = new ImageIcon(Config.ICONS_PATH+HIGH_LATENCY);
        lanOffline = new ImageIcon(Config.ICONS_PATH+OFFLINE);
        lanUnknown = new ImageIcon(Config.ICONS_PATH+UNKNOWN_ONLINE_STATUS);

        backgroundSide = new ImageIcon[2][2];
        for (int i=0; i<BACKGROUND_SIDE.length; i++) {
            for (int j=0; j<BACKGROUND_SIDE[i].length; j++) {
                backgroundSide[i][j] = new ImageIcon(Config.ICONS_PATH + game.league().getDirectoryName() + "/" + BACKGROUND_SIDE[i][j]);
            }
        }
        
        //Components
        sidePanel = new ImagePanel[2];
        for (int i=0; i<2; i++) {
            sidePanel[i] = new ImagePanel(ImagePanel.Mode.Stretch, backgroundSide[i][i].getImage());
            sidePanel[i].setOpaque(true);
        }
        JPanel midPanel = new ImagePanel(ImagePanel.Mode.Stretch, new ImageIcon(Config.ICONS_PATH + BACKGROUND_MID).getImage());
        ImagePanel bottomPanel = new ImagePanel(ImagePanel.Mode.Stretch, new ImageIcon(Config.ICONS_PATH + BACKGROUND_BOTTOM).getImage());
        
        //--side--
        //  score
        nameLabels = new JLabel[2];
        goalDecButton = new JButton[2];
        goalIncButton = new JButton[2];
        goalCountLabels = new JLabel[2];
        kickOffRadioButtons = new JRadioButton[3];
        ButtonGroup kickOffGroup = new ButtonGroup();
        pushLabels = new JLabel[2];
        for (int i=0; i<2; i++) {
            nameLabels[i] = new JLabel(game.league().getTeam(game.getGameState().teams[i].teamNumber).getName());
            nameLabels[i].setHorizontalAlignment(JLabel.CENTER);
            nameLabels[i].setForeground(game.getGameState().teams[i].teamColor.getColor(game.league()));
            goalIncButton[i] = new Button("+");
            goalDecButton[i] = new Button("-");
            kickOffRadioButtons[i] = new JRadioButton(KICKOFF);
            kickOffRadioButtons[i].setOpaque(false);
            kickOffRadioButtons[i].setHorizontalAlignment(JLabel.CENTER);
            kickOffGroup.add(kickOffRadioButtons[i]);
            goalCountLabels[i] = new JLabel("0");
            goalCountLabels[i].setHorizontalAlignment(JLabel.CENTER);
            pushLabels[i] = new JLabel("0");
            pushLabels[i].setHorizontalAlignment(JLabel.CENTER);
        }
        kickOffRadioButtons[2] = new JRadioButton();
        kickOffGroup.add(kickOffRadioButtons[2]);
        
        //  robots
        JPanel[] robotPanels = new JPanel[2];
        if (game.settings().isCoachAvailable) {
            robotButtons = new JButton[2][game.settings().teamSize+1];
            robotLabel = new JLabel[2][game.settings().teamSize+1];
            robotProgressBars = new JProgressBar[2][game.settings().teamSize+1];
        } else {
            robotButtons = new JButton[2][game.settings().teamSize];
            robotLabel = new JLabel[2][game.settings().teamSize];
            robotProgressBars = new JProgressBar[2][game.settings().teamSize];
        }
        for (int i=0; i<2; i++) {
            robotPanels[i] = new JPanel();
            robotPanels[i].setLayout(new GridLayout(robotButtons[i].length, 1, 0, 10));
            robotPanels[i].setOpaque(false);
            
            for (int j=0; j< robotButtons[i].length; j++) {
                robotButtons[i][j] = new Button();
                robotLabel[i][j] = new JLabel();
                robotLabel[i][j].setHorizontalAlignment(JLabel.CENTER);
                robotProgressBars[i][j] = new JProgressBar();
                robotProgressBars[i][j].setMaximum(1000);
                robotProgressBars[i][j].setVisible(false);
                TotalScaleLayout robotLayout = new TotalScaleLayout(robotButtons[i][j]);
                robotButtons[i][j].setLayout(robotLayout);
                robotLayout.add(.1, .1, .8, .5, robotLabel[i][j]);
                robotLayout.add(.1, .7, .8, .2, robotProgressBars[i][j]);
                robotPanels[i].add(robotButtons[i][j]);
            }
        }
        //  team
        timeOutButton = new JToggleButton[2];
        outButtons = new JButton[2];
        for (int i=0; i<2; i++) {
            timeOutButton[i] = new ToggleButton(TIMEOUT);
            outButtons[i] = new JButton(OUT);
        }
        if (game.league().isSPLFamily()) {
            gameStuckButtons = new Button[2];
            for (int i=0; i<2; i++) {
                gameStuckButtons[i] = new Button();
            }
        }
        
        //--mid--
        //  time
        clockResetButton = new ImageButton(clockImgReset.getImage());
        clockResetButton.setOpaque(false);
        clockResetButton.setBorder(null);

        String imagePath = Config.ICONS_PATH + (game.settings().lostTime ? BACKGROUND_CLOCK_SMALL : BACKGROUND_CLOCK);
        ImagePanel clockPanel = new ImagePanel(ImagePanel.Mode.Stretch, new ImageIcon(imagePath).getImage());
        clockPanel.setOpaque(false);
        clockLabel = new JLabel("10:00");
        clockLabel.setForeground(Color.WHITE);
        clockLabel.setHorizontalAlignment(JLabel.CENTER);
        clockPauseButton = new ImageButton(clockImgReset.getImage());
        clockPauseButton.setOpaque(false);
        clockPauseButton.setBorder(null);
        secondaryTimeLabel = new JLabel("0:00");
        secondaryTimeLabel.setHorizontalAlignment(JLabel.CENTER);
        incGameClockButton = new ImageButton(clockImgPlus.getImage());
        incGameClockButton.setOpaque(false);
        incGameClockButton.setBorder(null);
        ButtonGroup halfGroup;
        if (!game.settings().overtime) {
            firstHalfPeriodButton = new ToggleButton(FIRST_HALF);
            firstHalfPeriodButton.setSelected(true);
            secondHalfPeriodButton = new ToggleButton(SECOND_HALF);
            penaltyShootPeriodButton = new ToggleButton(PENALTY_SHOOT);
            refereeTimeoutButton = new ToggleButton(REFEREE_TIMEOUT);
            halfGroup = new ButtonGroup();
            halfGroup.add(firstHalfPeriodButton);
            halfGroup.add(secondHalfPeriodButton);
            halfGroup.add(penaltyShootPeriodButton);
            
            if (game.settings().isRefereeTimeoutAvailable) {
                halfGroup.add(refereeTimeoutButton);
            }
        } else {
            firstHalfPeriodButton = new ToggleButton(FIRST_HALF_SHORT);
            firstHalfPeriodButton.setSelected(true);
            secondHalfPeriodButton = new ToggleButton(SECOND_HALF_SHORT);
            firstHalfOvertimePeriodButton = new ToggleButton(FIRST_HALF_OVERTIME);
            secondHalfOvertimePeriodButton = new ToggleButton(SECOND_HALF_OVERTIME);
            penaltyShootPeriodButton = new ToggleButton(PENALTY_SHOOT_SHORT);
            refereeTimeoutButton = new ToggleButton(REFEREE_TIMEOUT);
            halfGroup = new ButtonGroup();
            halfGroup.add(firstHalfPeriodButton);
            halfGroup.add(secondHalfPeriodButton);
            halfGroup.add(firstHalfOvertimePeriodButton);
            halfGroup.add(secondHalfOvertimePeriodButton);
            halfGroup.add(penaltyShootPeriodButton);
            
            if (game.settings().isRefereeTimeoutAvailable) {
                halfGroup.add(refereeTimeoutButton);
            }
        }
        // play mode
        initialPlayModeButton = new ToggleButton(PLAY_MODE_INITIAL);
        initialPlayModeButton.setSelected(true);
        readyPlayModeButton = new ToggleButton(PLAY_MODE_READY);
        setPlayModeButton = new ToggleButton(PLAY_MODE_SET);
        playPlayModeButton = new ToggleButton(PLAY_MODE_PLAY);
        finishPlayModeButton = new ToggleButton(PLAY_MODE_FINISH);
        ButtonGroup playModeGroup = new ButtonGroup();
        playModeGroup.add(initialPlayModeButton);
        playModeGroup.add(readyPlayModeButton);
        playModeGroup.add(setPlayModeButton);
        playModeGroup.add(playPlayModeButton);
        playModeGroup.add(finishPlayModeButton);
        // penalties
        if (game.league().isSPLFamily()) {
            penaltyButtons = new JToggleButton[10];
            penaltyButtons[0] = new ToggleButton(PEN_PUSHING);
            penaltyButtons[1] = new ToggleButton(PEN_LEAVING);
            penaltyButtons[2] = new ToggleButton(PEN_FALLEN);
            penaltyButtons[3] = new ToggleButton(PEN_INACTIVE);
            penaltyButtons[4] = new ToggleButton(PEN_DEFENDER);
            penaltyButtons[5] = new ToggleButton(PEN_HOLDING);
            penaltyButtons[6] = new ToggleButton(PEN_HANDS);
            penaltyButtons[7] = new ToggleButton(PEN_PICKUP);
            penaltyButtons[8] = new ToggleButton(game.settings().dropInPlayerMode ? TEAMMATE_PUSHING : PEN_COACH_MOTION);
            penaltyButtons[9] = new ToggleButton(PEN_SUBSTITUTE);
        } else if (game.league().isHLFamily()) {
            penaltyButtons = new JToggleButton[7];
            penaltyButtons[0] = new ToggleButton(PEN_MANIPULATION);
            penaltyButtons[1] = new ToggleButton(PEN_PHYSICAL);
            penaltyButtons[2] = new ToggleButton(PEN_ATTACK);
            penaltyButtons[3] = new ToggleButton(PEN_DEFENSE);
            penaltyButtons[4] = new ToggleButton(PEN_PICKUP_INCAPABLE);
            penaltyButtons[5] = new ToggleButton(PEN_SERVICE);
            penaltyButtons[6] = new ToggleButton(PEN_SUBSTITUTE);
            dropBallButton = new Button(DROP_BALL);
        }
        //--bottom--
        //  timeline
        JPanel timelinePanel = new JPanel();
        timelinePanel.setOpaque(false);
        timelinePanel.setLayout(new GridLayout(1, ActionBoard.MAX_NUM_UNDOS_AT_ONCE - 1, 10, 0));
        undoButtons = new JToggleButton[ActionBoard.MAX_NUM_UNDOS_AT_ONCE - 1];
        for (int i = undoButtons.length - 1; i >= 0; i--) {
            undoButtons[i] = new ToggleButton();
            undoButtons[i].setVisible(false);
            timelinePanel.add(undoButtons[i]);
        }
        cancelUndoButton = new Button(CANCEL);
        cancelUndoButton.setVisible(false);
      
        //--layout--
        TotalScaleLayout layout = new TotalScaleLayout(frame);
        frame.setLayout(layout);
        
        layout.add(0, 0, .3, .04, nameLabels[0]);
        layout.add(.7, 0, .3, .04, nameLabels[1]);
        layout.add(.01, .05, .08, .07, goalIncButton[0]);
        layout.add(.91, .05, .08, .07, goalIncButton[1]);
        layout.add(.01, .13, .08, .06, goalDecButton[0]);
        layout.add(.91, .13, .08, .06, goalDecButton[1]);
        layout.add(.17, .05, .12, .04, kickOffRadioButtons[0]);
        layout.add(.71, .05, .12, .04, kickOffRadioButtons[1]);
        layout.add(.21, .09, .08, .07, goalCountLabels[0]);
        layout.add(.71, .09, .08, .07, goalCountLabels[1]);
        layout.add(.21, .16, .08, .04, pushLabels[0]);
        layout.add(.71, .16, .08, .04, pushLabels[1]);
        layout.add(.01, .21, .28, .55, robotPanels[0]);
        layout.add(.71, .21, .28, .55, robotPanels[1]);
        if (game.league().isSPLFamily() && !game.settings().dropInPlayerMode) {
            layout.add(.01, .77, .09, .09, timeOutButton[0]);
            layout.add(.9, .77, .09, .09, timeOutButton[1]);
            layout.add(.11, .77, .08, .09, gameStuckButtons[0]);
            layout.add(.81, .77, .08, .09, gameStuckButtons[1]);
            layout.add(.20, .77, .09, .09, outButtons[0]);
            layout.add(.71, .77, .09, .09, outButtons[1]);
        } else {
            if (game.league().isSPLFamily()) {
                layout.add(.01, .77, .135, .09, gameStuckButtons[0]);
                layout.add(.855, .77, .135, .09, gameStuckButtons[1]);
            } else {
                layout.add(.01, .77, .135, .09, timeOutButton[0]);
                layout.add(.855, .77, .135, .09, timeOutButton[1]);
            }
            layout.add(.155, .77, .135, .09, outButtons[0]);
            layout.add(.71, .77, .135, .09, outButtons[1]);
        }
        layout.add(.31, .0, .08, .11, clockResetButton);
        layout.add(.4, .012, .195, .10, clockLabel);
        layout.add(.61, .0, .08, .11, clockPauseButton);
        layout.add(.4, .11, .2, .07, secondaryTimeLabel);
        if (game.settings().lostTime) {
            layout.add(.590, .0, .03, .11, incGameClockButton);
            layout.add(.4, .0, .195, .11, clockPanel);
        }
        else{
            layout.add(.4, .0, .2, .11, clockPanel);
        }
        if (!game.settings().overtime) {
            if (game.settings().isRefereeTimeoutAvailable && !game.settings().dropInPlayerMode) {
                layout.add(.31, .19, .09, .06, firstHalfPeriodButton);
                layout.add(.407, .19, .09, .06, secondHalfPeriodButton);
                layout.add(.503, .19, .09, .06, penaltyShootPeriodButton);
                layout.add(.60, .19, .09, .06, refereeTimeoutButton);
            } else { // no referee timeout in dropInPlayerMode is not supported!
                layout.add(.31, .19, .12, .06, firstHalfPeriodButton);
                layout.add(.44, .19, .12, .06, secondHalfPeriodButton);
                layout.add(.57, .19, .12, .06, game.settings().dropInPlayerMode ? refereeTimeoutButton : penaltyShootPeriodButton);
            }
        } else {
            if (game.settings().isRefereeTimeoutAvailable) {
                layout.add(.31, .19, .06, .06, firstHalfPeriodButton);
                layout.add(.375, .19, .06, .06, secondHalfPeriodButton);
                layout.add(.439, .19, .06, .06, firstHalfOvertimePeriodButton);
                layout.add(.501, .19, .06, .06, secondHalfOvertimePeriodButton);
                layout.add(.565, .19, .06, .06, penaltyShootPeriodButton);
                layout.add(.63, .19, .06, .06, refereeTimeoutButton);
            } else {
                layout.add(.31, .19, .07, .06, firstHalfPeriodButton);
                layout.add(.3875, .19, .07, .06, secondHalfPeriodButton);
                layout.add(.465, .19, .07, .06, firstHalfOvertimePeriodButton);
                layout.add(.5425, .19, .07, .06, secondHalfOvertimePeriodButton);
                layout.add(.62, .19, .07, .06, penaltyShootPeriodButton);
            }
        }
        layout.add(.31, .26, .07, .08, initialPlayModeButton);
        layout.add(.3875, .26, .07, .08, readyPlayModeButton);
        layout.add(.465, .26, .07, .08, setPlayModeButton);
        layout.add(.5425, .26, .07, .08, playPlayModeButton);
        layout.add(.62, .26, .07, .08, finishPlayModeButton);
        if (game.league().isSPLFamily()) {
            layout.add(.31, .37, .185, .08, penaltyButtons[0]);
            layout.add(.505, .37, .185, .08, penaltyButtons[1]);
            layout.add(.31, .47, .185, .08, penaltyButtons[2]);
            layout.add(.505, .47, .185, .08, penaltyButtons[3]);
            layout.add(.31, .57, .185, .08, penaltyButtons[4]);
            layout.add(.505, .57, .185, .08, penaltyButtons[5]);
            layout.add(.31, .67, .185, .08, penaltyButtons[6]);
            layout.add(.505, .67, .185, .08, penaltyButtons[7]);
            layout.add(.31, .77, .185, .08, penaltyButtons[8]);
            if (game.settings().teamSize > game.settings().robotsPlaying) {
                layout.add(.505, .77, .185, .08, penaltyButtons[9]);
            }
        } else if (game.league().isHLFamily()) {
            layout.add(.31,  .38, .185, .08, penaltyButtons[0]);
            layout.add(.505, .38, .185, .08, penaltyButtons[1]);
            layout.add(.31,  .48, .185, .08, penaltyButtons[2]);
            layout.add(.505, .48, .185, .08, penaltyButtons[3]);
            layout.add(.31,  .58, .185, .08, penaltyButtons[4]);
            layout.add(.505, .58, .185, .08, penaltyButtons[5]);
            layout.add(.31,  .68, .185, .08, penaltyButtons[6]);
            layout.add(.31,  .78, .38,  .08, dropBallButton);
        }
        layout.add(.08, .88, .84, .11, timelinePanel);
        layout.add(.925, .88, .07, .11, cancelUndoButton);
        layout.add(0, 0, .3, .87, sidePanel[0]);
        layout.add(.3, 0, .4, .87, midPanel);
        layout.add(.7, 0, .3, .87, sidePanel[1]);
        layout.add(0, .87, 1, .132, bottomPanel);
        
        //--listener--
        for (int i=0; i<2; i++) {
            goalDecButton[i].addActionListener(new ActionListenerAdapter(game, ActionBoard.goalDec[i]));
            goalIncButton[i].addActionListener(new ActionListenerAdapter(game, ActionBoard.goalInc[i]));
            kickOffRadioButtons[i].addActionListener(new ActionListenerAdapter(game, ActionBoard.kickOff[i]));
            for (int j=0; j< robotButtons[i].length; j++) {
                robotButtons[i][j].addActionListener(new ActionListenerAdapter(game, ActionBoard.robotButton[i][j]));
            }
            timeOutButton[i].addActionListener(new ActionListenerAdapter(game, ActionBoard.timeOut[i]));
            outButtons[i].addActionListener(new ActionListenerAdapter(game, ActionBoard.out[i]));
            if (game.league().isSPLFamily()) {
                gameStuckButtons[i].addActionListener(new ActionListenerAdapter(game, ActionBoard.stuck[i]));
            }
        }
        refereeTimeoutButton.addActionListener(new ActionListenerAdapter(game, ActionBoard.refereeTimeout));
        initialPlayModeButton.addActionListener(new ActionListenerAdapter(game, ActionBoard.initial));
        readyPlayModeButton.addActionListener(new ActionListenerAdapter(game, ActionBoard.ready));
        setPlayModeButton.addActionListener(new ActionListenerAdapter(game, ActionBoard.set));
        playPlayModeButton.addActionListener(new ActionListenerAdapter(game, ActionBoard.play));
        finishPlayModeButton.addActionListener(new ActionListenerAdapter(game, ActionBoard.finish));
        clockResetButton.addActionListener(new ActionListenerAdapter(game, ActionBoard.clockReset));
        clockPauseButton.addActionListener(new ActionListenerAdapter(game, ActionBoard.clockPause));
        if (game.settings().lostTime) {
            incGameClockButton.addActionListener(new ActionListenerAdapter(game, ActionBoard.incGameClock));
        }
        firstHalfPeriodButton.addActionListener(new ActionListenerAdapter(game, ActionBoard.firstHalf));
        secondHalfPeriodButton.addActionListener(new ActionListenerAdapter(game, ActionBoard.secondHalf));
        if (game.settings().overtime) {
            firstHalfOvertimePeriodButton.addActionListener(new ActionListenerAdapter(game, ActionBoard.firstHalfOvertime));
            secondHalfOvertimePeriodButton.addActionListener(new ActionListenerAdapter(game, ActionBoard.secondHalfOvertime));
        }
        penaltyShootPeriodButton.addActionListener(new ActionListenerAdapter(game, ActionBoard.penaltyShoot));
        if (game.league().isSPLFamily()) {
            penaltyButtons[0].addActionListener(new ActionListenerAdapter(game, ActionBoard.pushing));
            penaltyButtons[1].addActionListener(new ActionListenerAdapter(game, ActionBoard.leaving));
            penaltyButtons[2].addActionListener(new ActionListenerAdapter(game, ActionBoard.fallen));
            penaltyButtons[3].addActionListener(new ActionListenerAdapter(game, ActionBoard.inactive));
            penaltyButtons[4].addActionListener(new ActionListenerAdapter(game, ActionBoard.defender));
            penaltyButtons[5].addActionListener(new ActionListenerAdapter(game, ActionBoard.holding));
            penaltyButtons[6].addActionListener(new ActionListenerAdapter(game, ActionBoard.hands));
            penaltyButtons[7].addActionListener(new ActionListenerAdapter(game, ActionBoard.pickUpSPL));
            penaltyButtons[8].addActionListener(new ActionListenerAdapter(game, game.settings().dropInPlayerMode ? ActionBoard.teammatePushing : ActionBoard.coachMotion));
            penaltyButtons[9].addActionListener(new ActionListenerAdapter(game, ActionBoard.substitute));
        } else if (game.league().isHLFamily()) {
            penaltyButtons[0].addActionListener(new ActionListenerAdapter(game, ActionBoard.ballManipulation));
            penaltyButtons[1].addActionListener(new ActionListenerAdapter(game, ActionBoard.pushing));
            penaltyButtons[2].addActionListener(new ActionListenerAdapter(game, ActionBoard.attack));
            penaltyButtons[3].addActionListener(new ActionListenerAdapter(game, ActionBoard.defense));
            penaltyButtons[4].addActionListener(new ActionListenerAdapter(game, ActionBoard.pickUpHL));
            penaltyButtons[5].addActionListener(new ActionListenerAdapter(game, ActionBoard.service));
            penaltyButtons[6].addActionListener(new ActionListenerAdapter(game, ActionBoard.substitute));
            dropBallButton.addActionListener(new ActionListenerAdapter(game, ActionBoard.dropBall));
        }
        for (int i=0; i< undoButtons.length; i++) {
            undoButtons[i].addActionListener(new ActionListenerAdapter(game, ActionBoard.undo[i+1]));
        }
        cancelUndoButton.addActionListener(new ActionListenerAdapter(game, ActionBoard.cancelUndo));
      
        //fullscreen
        if (fullscreen) {
            frame.setUndecorated(true);
            GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
            devices[0].setFullScreenWindow(frame);
            if (IS_OSX) {
                frame.setVisible(false); // without this, keyboard input is missing on OS X
            }
        }

        // Populate the UI with the initial game state
        update(game.getGameState());

        frame.setVisible(true);
    }

    /**
     * Updates the UI to reflect the provided game state.
     *
     * There are three additional sources of information that can be used here:
     *
     *  1. The RobotWatcher, you can ask him for the robots online-status.
     *  2. The last action from the ActionHandler, but you should try to avoid
     *     this for less dependencies between actions and GUI (view and control).
     *  3. The actions canExecute method to enable or disable buttons.
     *
     * This method should never have other effects than updating the view!
     * 
     * @param state the game state to use when populating the UI.
     */
    private void update(@NotNull GameState state)
    {
        updateClock(state);
        updateHalf(state);
        updateTeamColors(state);
        updatePlayMode(state);
        updateGoal(state);
        updateKickoff(state);
        updateRobots(state);
        updatePushes(state);
        updateTimeOut(state);
        updateRefereeTimeout(state);
        updateOut(state);
        
        if (game.league().isSPLFamily()) {
            updateGlobalStuck(state);
            updatePenaltiesSPL(state);
        } else if (game.league().isHLFamily()) {
            updatePenaltiesHL(state);
            updateDropBall(state);
        }

        updateTimelineUndo();

        frame.repaint();
    }

    /** Closes the UI and disposes held resources. */
    public void close()
    {
        frame.dispose();
    }

    private void updateClock(GameState state)
    {
        clockLabel.setText(formatTime(state.getRemainingGameTime()));
        Integer secondaryTime = state.getSecondaryTime(KICKOFF_BLOCKED_HIGHLIGHT_SECONDS - 1);
        if (secondaryTime != null) {
            if (state.playMode == PlayMode.Playing) {
                secondaryTimeLabel.setText(formatTime(Math.max(0, secondaryTime)));
                secondaryTimeLabel.setForeground(secondaryTime <= 0
                        && secondaryTimeLabel.getForeground() != COLOR_HIGHLIGHT ? COLOR_HIGHLIGHT : Color.BLACK);
            } else {
                secondaryTimeLabel.setText(formatTime(secondaryTime));
                secondaryTimeLabel.setForeground(Color.BLACK);
            }
        } else {
            secondaryTimeLabel.setText("");
            secondaryTimeLabel.setForeground(Color.BLACK);
        }
        
        ImageIcon tmp;
        if (ActionBoard.clock.isClockRunning(game, state)) {
            tmp = clockImgPause;
        } else {
            tmp = clockImgPlay;
        }
        clockPauseButton.setImage(tmp.getImage());
        clockResetButton.setVisible(ActionBoard.clockReset.canExecute(game, state));
        clockPauseButton.setVisible(ActionBoard.clockPause.canExecute(game, state));
        if (game.settings().lostTime) {
            incGameClockButton.setEnabled(ActionBoard.incGameClock.canExecute(game, state));
        }
    }
    
    private void updateHalf(GameState state)
    {
        for (int i=0; i<2; i++) {
            nameLabels[i].setText(game.league().getTeam(state.teams[i].teamNumber).getName());
        }
        firstHalfPeriodButton.setEnabled(ActionBoard.firstHalf.canExecute(game, state));
        secondHalfPeriodButton.setEnabled(ActionBoard.secondHalf.canExecute(game, state));
        if (game.settings().overtime) {
            firstHalfOvertimePeriodButton.setEnabled(ActionBoard.firstHalfOvertime.canExecute(game, state));
            secondHalfOvertimePeriodButton.setEnabled(ActionBoard.secondHalfOvertime.canExecute(game, state));
        }
        penaltyShootPeriodButton.setEnabled(ActionBoard.penaltyShoot.canExecute(game, state));
        firstHalfPeriodButton.setSelected((state.period == Period.Normal)
                && (state.firstHalf));
        secondHalfPeriodButton.setSelected((state.period == Period.Normal)
                && (!state.firstHalf));
        if (game.settings().overtime) {
           firstHalfOvertimePeriodButton.setSelected((state.period == Period.Overtime)
                   && (state.firstHalf));
           secondHalfOvertimePeriodButton.setSelected((state.period == Period.Overtime)
                   && (!state.firstHalf));
        }
        penaltyShootPeriodButton.setSelected(state.period == Period.PenaltyShootout || state.previousPeriod == Period.PenaltyShootout);
    }
    
    private void updateTeamColors(GameState state)
    {
        for (int i=0; i<2; i++) {
            nameLabels[i].setForeground(state.teams[i].teamColor.getColor(game.league()));
            sidePanel[i].setImage(backgroundSide[i][state.teams[i].teamColor.getValue()].getImage());
        }
    }
    
    private void updatePlayMode(GameState state)
    {
        initialPlayModeButton.setEnabled(ActionBoard.initial.canExecute(game, state));
        readyPlayModeButton.setEnabled(ActionBoard.ready.canExecute(game, state));
        setPlayModeButton.setEnabled(ActionBoard.set.canExecute(game, state));
        playPlayModeButton.setEnabled(ActionBoard.play.canExecute(game, state));
        finishPlayModeButton.setEnabled(ActionBoard.finish.canExecute(game, state));

        if (state.playMode == PlayMode.Initial) {
            initialPlayModeButton.setSelected(true);
        } else if (state.playMode == PlayMode.Ready) {
            readyPlayModeButton.setSelected(true);
        } else if (state.playMode == PlayMode.Set) {
            setPlayModeButton.setSelected(true);
        } else if (state.playMode == PlayMode.Playing) {
            playPlayModeButton.setSelected(true);
        } else if (state.playMode == PlayMode.Finished) {
            finishPlayModeButton.setSelected(true);
        }

        highlight(finishPlayModeButton,
                state.playMode != PlayMode.Finished
                && state.getRemainingGameTime() <= FINISH_HIGHLIGHT_SECONDS
                && finishPlayModeButton.getBackground() != COLOR_HIGHLIGHT);
    }
    
    private void updateGoal(GameState state)
    {
        for (int i=0; i<2; i++) {
            goalCountLabels[i].setText("" + state.teams[i].score);
            goalIncButton[i].setEnabled(ActionBoard.goalInc[i].canExecute(game, state));
            goalDecButton[i].setVisible(ActionBoard.goalDec[i].canExecute(game, state));
        }
    }
    
    private void updateKickoff(GameState state)
    {
        if (state.nextKickOffColor == null) {
            // drop ball
            kickOffRadioButtons[2].setSelected(true);
        } else {
            kickOffRadioButtons[state.teams[0].teamColor == state.nextKickOffColor ? 0 : 1].setSelected(true);
        }
        for (int i=0; i<2; i++) {
            kickOffRadioButtons[i].setEnabled(ActionBoard.kickOff[i].canExecute(game, state));
            if (state.period != Period.PenaltyShootout
                && state.previousPeriod != Period.PenaltyShootout) {
                kickOffRadioButtons[i].setText(KICKOFF);
            } else {
                kickOffRadioButtons[i].setText(KICKOFF_PENALTY_SHOOTOUT);
            }
        }
    }
    
    private void updatePushes(GameState state)
    {
        for (int i=0; i<2; i++) {
            if (state.period != Period.PenaltyShootout && state.previousPeriod != Period.PenaltyShootout) {
                if (game.settings().pushesToEjection == null || game.settings().pushesToEjection.length == 0) {
                    pushLabels[i].setText("");
                } else {
                    pushLabels[i].setText(PUSHES + ": " + state.pushes[i]);
                }
            } else {
                pushLabels[i].setText((i == 0 && (state.playMode == PlayMode.Set
                        || state.playMode == PlayMode.Playing) ? SHOT : SHOTS) + ": " + state.teams[i].penaltyShot);
            }
        }
    }
    
    private void updateRobots(GameState state)
    {
        RobotOnlineStatus[][] onlineStatus = robotWatcher.updateRobotOnlineStatus();

        for (int i=0; i< robotButtons.length; i++) {
            for (int j=0; j< robotButtons[i].length; j++) {
                if (ActionBoard.robotButton[i][j].isCoach()) {
                   if (state.teams[i].coach.penalty == Penalty.SplCoachMotion) {
                      robotButtons[i][j].setEnabled(false);
                      robotLabel[i][j].setText(EJECTED);
                  } else {
                      robotLabel[i][j].setText(state.teams[i].teamColor+" "+COACH);
                  }
                }
                else {
                    if (state.teams[i].player[j].penalty != Penalty.None) {
                        if (!state.ejected[i][j]) {
                            int seconds = state.getRemainingPenaltyTime(i, j);
                            boolean pickup = ((game.league().isSPLFamily() &&
                                        state.teams[i].player[j].penalty == Penalty.SplRequestForPickup)
                                   || (game.league().isHLFamily() &&
                                       ( state.teams[i].player[j].penalty == Penalty.HLPickupOrIncapable
                                      || state.teams[i].player[j].penalty == Penalty.Service))
                                    );
                            if (seconds == 0) {
                                if (pickup) {
                                    robotLabel[i][j].setText(state.teams[i].teamColor+" "+(j+1)+" ("+PEN_PICKUP+")");
                                    highlight(robotButtons[i][j], true);
                                } else if (state.teams[i].player[j].penalty == Penalty.Substitute) {
                                    robotLabel[i][j].setText(state.teams[i].teamColor+" "+(j+1)+" ("+PEN_SUBSTITUTE_SHORT+")");
                                    highlight(robotButtons[i][j], false);
                                } else if (!(game.league().isSPLFamily()) ||
                                        !(state.teams[i].player[j].penalty == Penalty.SplCoachMotion)) {
                                    robotLabel[i][j].setText(state.teams[i].teamColor+" "+(j+1)+": "+formatTime(seconds));
                                    highlight(robotButtons[i][j], seconds <= UNPEN_HIGHLIGHT_SECONDS && robotButtons[i][j].getBackground() != COLOR_HIGHLIGHT);
                                }
                            }  else {
                                robotLabel[i][j].setText(state.teams[i].teamColor+" "+(j+1)+": "+formatTime(seconds)+(pickup ? " (P)" : ""));
                                highlight(robotButtons[i][j], seconds <= UNPEN_HIGHLIGHT_SECONDS && robotButtons[i][j].getBackground() != COLOR_HIGHLIGHT);
                            }
                            int penTime = (seconds + state.getSecondsSince(state.whenPenalized[i][j]));
                            if (seconds != 0) {
                                robotProgressBars[i][j].setValue(1000 * seconds / penTime);
                            }
                            robotProgressBars[i][j].setVisible(seconds != 0);
                        } else {
                            robotLabel[i][j].setText(EJECTED);
                            robotProgressBars[i][j].setVisible(false);
                            highlight(robotButtons[i][j], false);
                        }
                    } else {
                        robotLabel[i][j].setText(state.teams[i].teamColor+" "+(j+1));
                        robotProgressBars[i][j].setVisible(false);
                        highlight(robotButtons[i][j], false);
                    }
                }    
                
                robotButtons[i][j].setEnabled(ActionBoard.robotButton[i][j].canExecute(game, state));
                
                ImageIcon currentLanIcon;
                if (onlineStatus[i][j] == RobotOnlineStatus.ONLINE) {
                    currentLanIcon = lanOnline;
                } else if (onlineStatus[i][j] == RobotOnlineStatus.HIGH_LATENCY) {
                    currentLanIcon = lanHighLatency;
                } else if (onlineStatus[i][j] == RobotOnlineStatus.OFFLINE) {
                    currentLanIcon = lanOffline;
                } else {
                    currentLanIcon = lanUnknown;
                }
                robotLabel[i][j].setIcon(currentLanIcon);
            }
        }
    }
    
    private void updateTimeOut(GameState state)
    {
        for (int i=0; i<2; i++) {
            if (!state.timeOutActive[i]) {
                timeOutButton[i].setSelected(false);
                highlight(timeOutButton[i], false);
            } else {
                boolean shouldHighlight = (state.getRemainingSeconds(state.whenCurrentPlayModeBegan, game.settings().timeOutTime) < TIMEOUT_HIGHLIGHT_SECONDS)
                        && (timeOutButton[i].getBackground() != COLOR_HIGHLIGHT);
                timeOutButton[i].setSelected(!IS_OSX || !shouldHighlight);
                highlight(timeOutButton[i], shouldHighlight);
            }
            timeOutButton[i].setEnabled(ActionBoard.timeOut[i].canExecute(game, state));
        }
    }
    
    private void updateRefereeTimeout(GameState state)
    {
        refereeTimeoutButton.setSelected(state.refereeTimeout);
        refereeTimeoutButton.setEnabled(ActionBoard.refereeTimeout.canExecute(game, state));
    }
    
    private void updateGlobalStuck(GameState state)
    {
        for (int i=0; i<2; i++) {
            if (state.playMode == PlayMode.Playing
                    && state.getRemainingSeconds(state.whenCurrentPlayModeBegan, game.settings().kickoffTime + game.settings().minDurationBeforeStuck) > 0)
            {
                if (state.nextKickOffColor == state.teams[i].teamColor)
                {
                    gameStuckButtons[i].setEnabled(true);
                    gameStuckButtons[i].setText("<font color=#000000>" + KICKOFF_GOAL);
                } else {
                    gameStuckButtons[i].setEnabled(false);
                    gameStuckButtons[i].setText("<font color=#808080>" + STUCK);
                }
            } else {
                gameStuckButtons[i].setEnabled(ActionBoard.stuck[i].canExecute(game, state));
                gameStuckButtons[i].setText((ActionBoard.stuck[i].canExecute(game, state) ? "<font color=#000000>" : "<font color=#808080>") + STUCK);
            }
        }
    }
    
    private void updateDropBall(GameState state)
    {
        dropBallButton.setEnabled(ActionBoard.dropBall.canExecute(game, state));
    }
    
    private void updateOut(GameState state)
    {
        for (int i=0; i<2; i++) {
            outButtons[i].setEnabled(ActionBoard.out[i].canExecute(game, state));
        }
    }

    private void updatePenaltiesSPL(GameState state)
    {
        penaltyButtons[0].setEnabled(ActionBoard.pushing.canExecute(game, state));
        penaltyButtons[1].setEnabled(ActionBoard.leaving.canExecute(game, state));
        penaltyButtons[2].setEnabled(ActionBoard.fallen.canExecute(game, state));
        penaltyButtons[3].setEnabled(ActionBoard.inactive.canExecute(game, state));
        penaltyButtons[3].setText("<html><center>"
                +(ActionBoard.inactive.canExecute(game, state) ? "<font color=#000000>" : "<font color=#808080>")
                +PEN_INACTIVE);
        penaltyButtons[4].setEnabled(ActionBoard.defender.canExecute(game, state));
        penaltyButtons[5].setEnabled(ActionBoard.holding.canExecute(game, state));
        penaltyButtons[6].setEnabled(ActionBoard.hands.canExecute(game, state));
        penaltyButtons[7].setEnabled(ActionBoard.pickUpSPL.canExecute(game, state));
        penaltyButtons[8].setEnabled(game.settings().dropInPlayerMode
                ? ActionBoard.teammatePushing.canExecute(game, state)
                : ActionBoard.coachMotion.canExecute(game, state));
        penaltyButtons[9].setEnabled(ActionBoard.substitute.canExecute(game, state));
        
        Action highlightAction = game.getLastUserAction();
        penaltyButtons[0].setSelected(highlightAction == ActionBoard.pushing);
        penaltyButtons[1].setSelected(highlightAction == ActionBoard.leaving);
        penaltyButtons[2].setSelected(highlightAction == ActionBoard.fallen);
        penaltyButtons[3].setSelected(highlightAction == ActionBoard.inactive);
        penaltyButtons[4].setSelected(highlightAction == ActionBoard.defender);
        penaltyButtons[5].setSelected(highlightAction == ActionBoard.holding);
        penaltyButtons[6].setSelected(highlightAction == ActionBoard.hands);
        penaltyButtons[7].setSelected(highlightAction == ActionBoard.pickUpSPL);
        penaltyButtons[8].setSelected(game.settings().dropInPlayerMode
                ? highlightAction == ActionBoard.teammatePushing
                : highlightAction == ActionBoard.coachMotion);
        penaltyButtons[9].setSelected(highlightAction == ActionBoard.substitute);
    }
    
    private void updatePenaltiesHL(GameState state)
    {
        penaltyButtons[0].setEnabled(ActionBoard.ballManipulation.canExecute(game, state));
        penaltyButtons[1].setEnabled(ActionBoard.pushing.canExecute(game, state));
        penaltyButtons[2].setEnabled(ActionBoard.attack.canExecute(game, state));
        penaltyButtons[3].setEnabled(ActionBoard.defense.canExecute(game, state));
        penaltyButtons[4].setEnabled(ActionBoard.pickUpHL.canExecute(game, state));
        penaltyButtons[5].setEnabled(ActionBoard.service.canExecute(game, state));
        penaltyButtons[6].setEnabled(ActionBoard.substitute.canExecute(game, state));

        Action highlightAction = game.getLastUserAction();
        penaltyButtons[0].setSelected(highlightAction == ActionBoard.ballManipulation);
        penaltyButtons[1].setSelected(highlightAction == ActionBoard.pushing);
        penaltyButtons[2].setSelected(highlightAction == ActionBoard.attack);
        penaltyButtons[3].setSelected(highlightAction == ActionBoard.defense);
        penaltyButtons[4].setSelected(highlightAction == ActionBoard.pickUpHL);
        penaltyButtons[5].setSelected(highlightAction == ActionBoard.service);
        penaltyButtons[6].setSelected(highlightAction == ActionBoard.substitute);
    }
    
    private void updateTimelineUndo()
    {
        Action highlightAction = game.getLastUserAction();
        String[] undos = game.getLastTimelineTitles(ActionBoard.MAX_NUM_UNDOS_AT_ONCE);
        boolean isUndoingAnything = false;
        for (int i = undoButtons.length - 1; i >= 0; i--) {
            undoButtons[i].setVisible(!undos[i].equals(""));
            undoButtons[i].setEnabled(!undos[i].contains(" vs "));
            if (highlightAction == ActionBoard.undo[i + 1] && ActionBoard.undo[i + 1].isPreview()) {
                isUndoingAnything = true;
                undoButtons[i].setText("<html><center>Undo '" + undos[i] + "'?");
                undoButtons[i].setSelected(true);
            } else {
                undoButtons[i].setText("<html><center>" + undos[i]);
                undoButtons[i].setSelected(false);
            }
        }
        cancelUndoButton.setVisible(isUndoingAnything);
    }
    
    private void updateFonts()
    {
        double size = Math.min((frame.getWidth()/(double)WINDOW_WIDTH), (frame.getHeight()/(double)WINDOW_HEIGHT));

        // Only update fonts if the window size has actually changed
        if (size == lastSize) {
            return;
        }
        lastSize = size;
        
        Font titleFont = new Font(STANDARD_FONT, Font.PLAIN, (int)(TITLE_FONT_SIZE*(size)));
        Font standardFont = new Font(STANDARD_FONT, Font.PLAIN, (int)(STANDARD_FONT_SIZE * (size)));
        Font goalsFont = new Font(STANDARD_FONT, Font.PLAIN, (int)(GOALS_FONT_SIZE*(size)));
        Font timeFont = new Font(STANDARD_FONT, Font.PLAIN, (int)(TIME_FONT_SIZE*(size)));
        Font timeSubFont = new Font(STANDARD_FONT, Font.PLAIN, (int)(TIME_SUB_FONT_SIZE*(size)));
        Font timeoutFont = new Font(STANDARD_FONT, Font.PLAIN, (int)(TIMEOUT_FONT_SIZE*(size)));
        Font playModeFont = new Font(STANDARD_FONT, Font.PLAIN, (int)(PLAY_MODE_FONT_SIZE *(size)));

        for (int i=0; i<=1; i++) {
            nameLabels[i].setFont(titleFont);
            goalIncButton[i].setFont(standardFont);
            goalDecButton[i].setFont(standardFont);
            kickOffRadioButtons[i].setFont(standardFont);
            goalCountLabels[i].setFont(goalsFont);
            pushLabels[i].setFont(standardFont);
            for (int j=0; j< robotButtons[i].length; j++) {
                robotLabel[i][j].setFont(titleFont);
            }
            timeOutButton[i].setFont(timeoutFont);
            outButtons[i].setFont(timeoutFont);
            if (game.league().isSPLFamily()) {
                gameStuckButtons[i].setFont(timeoutFont);
            }
        }
        clockLabel.setFont(timeFont);
        secondaryTimeLabel.setFont(timeSubFont);
        
        firstHalfPeriodButton.setFont(timeoutFont);
        secondHalfPeriodButton.setFont(timeoutFont);
        if (game.settings().overtime) {
            firstHalfOvertimePeriodButton.setFont(timeoutFont);
            secondHalfOvertimePeriodButton.setFont(timeoutFont);
        }
        penaltyShootPeriodButton.setFont(timeoutFont);
        if (game.settings().isRefereeTimeoutAvailable) {
            refereeTimeoutButton.setFont(timeoutFont);
        }

        initialPlayModeButton.setFont(playModeFont);
        readyPlayModeButton.setFont(playModeFont);
        setPlayModeButton.setFont(playModeFont);
        playPlayModeButton.setFont(playModeFont);
        finishPlayModeButton.setFont(playModeFont);
        for (JToggleButton penaltyButton : penaltyButtons) {
            penaltyButton.setFont(standardFont);
        }
        if (dropBallButton != null) {
            dropBallButton.setFont(standardFont);
        }
        for (JToggleButton undoButton : undoButtons) {
            undoButton.setFont(timeoutFont);
        }
        cancelUndoButton.setFont(standardFont);
    }
    
    /**
     * Set the given button highlighted or normal.
     * 
     * @param button        The button to highlight.
     * @param highlight     If the button should be highlighted.
     */
    private void highlight(AbstractButton button, boolean highlight)
    {
        button.setBackground(highlight ? COLOR_HIGHLIGHT : COLOR_STANDARD);
        if (IS_OSX) {
            button.setOpaque(highlight);
            button.setBorderPainted(!highlight);
        }
    }

    private static String formatTime(int seconds)
    {
        int displaySeconds = Math.abs(seconds) % 60;
        int displayMinutes = Math.abs(seconds) / 60;
        return (seconds < 0 ? "-" : "") + String.format("%02d:%02d", displayMinutes, displaySeconds);
    }
}
