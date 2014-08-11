package controller.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

import common.EventHandler;
import common.TotalScaleLayout;
import controller.Action;
import controller.Game;
import controller.action.ActionBoard;
import controller.net.RobotOnlineStatus;
import controller.net.RobotWatcher;
import controller.ui.controls.*;
import data.*;
import rules.HL;
import rules.Rules;
import rules.SPL;

/**
 * This is the main Game Controller GUI.
 *
 * In this class you will find the whole graphical output and the bindings
 * of buttons to their actions, nothing less and nothing more.
 *
 * @author Michel Bartsch
 */
public class GUI
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
    private static final String ICONS_PATH = "config/icons/";
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
    private static final Color COLOR_STANDARD = (new JButton()).getBackground();
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

    private final ImagePanel[] side;
    private final JLabel[] name;
    private final JButton[] goalDec;
    private final JButton[] goalInc;
    private final JLabel[] goals;
    private final JRadioButton[] kickOff;
    private final ButtonGroup kickOffGroup;
    private final JLabel[] pushes;
    private final JPanel[] robots;
    private final JButton[][] robot;
    private final JLabel[][] robotLabel;
    private final ImageIcon[][] lanIcon;
    private final JProgressBar[][] robotTime;
    private final JToggleButton refereeTimeout;
    private final JToggleButton[] timeOut;
    private JButton[] stuck;
    private final JButton[] out;
    private final JPanel mid;
    private final JToggleButton initial;
    private final JToggleButton ready;
    private final JToggleButton set;
    private final JToggleButton play;
    private final JToggleButton finish;
    private final ButtonGroup playModeGroup;
    private final ImageButton clockReset;
    private final ImagePanel clockContainer;
    private final JLabel clock;
    private final JLabel clockSub;
    private final ImageButton incGameClock;
    private final ImageButton clockPause;
    private final JToggleButton firstHalf;
    private final JToggleButton secondHalf;
    private JToggleButton firstHalfOvertime;
    private JToggleButton secondHalfOvertime;
    private final JToggleButton penaltyShoot;
    private final ButtonGroup halfGroup;
    private JToggleButton[] pen;
    private JButton dropBall;
    private final ImagePanel bottom;
    private final JToggleButton[] undoButtons;
    private final JButton cancelUndo;

    private final RobotWatcher robotWatcher;
    private final Game game;

    /**
     * Initialises and displays the GUI.
     *
     * @param game the game to bind the UI to.
     * @param fullscreen whether the window should fill the screen.
     * @param robotWatcher the robot watcher which track the online status of bots.
     */
    public GUI(Game game, boolean fullscreen, RobotWatcher robotWatcher)
    {
        this.game = game;
        this.robotWatcher = robotWatcher;

        game.gameStateChanged.subscribe(new EventHandler<GameState>()
        {
            @Override
            public void handle(GameState state)
            {
                GUI.this.update(state);
            }
        });

        frame = new PaintableFrame(WINDOW_TITLE);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setResizable(true);
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
                GUI.this.game.requestShutdown();
            }
        });
        
        clockImgReset = new ImageIcon(ICONS_PATH+CLOCK_RESET);
        clockImgPlay = new ImageIcon(ICONS_PATH+CLOCK_PLAY);
        clockImgPause = new ImageIcon(ICONS_PATH+CLOCK_PAUSE);
        clockImgPlus = new ImageIcon(ICONS_PATH+CLOCK_PLUS);
        lanOnline = new ImageIcon(ICONS_PATH+ONLINE);
        lanHighLatency = new ImageIcon(ICONS_PATH+HIGH_LATENCY);
        lanOffline = new ImageIcon(ICONS_PATH+OFFLINE);
        lanUnknown = new ImageIcon(ICONS_PATH+UNKNOWN_ONLINE_STATUS);
        
        backgroundSide = new ImageIcon[2][2];
        for (int i=0; i<BACKGROUND_SIDE.length; i++) {
            for (int j=0; j<BACKGROUND_SIDE[i].length; j++) {
                backgroundSide[i][j] = new ImageIcon(ICONS_PATH+Rules.league.leagueDirectory+"/"+BACKGROUND_SIDE[i][j]);
            }
        }
        
        //Components
        side = new ImagePanel[2];
        for (int i=0; i<2; i++) {
            side[i] = new ImagePanel(backgroundSide[i][i].getImage());
            side[i].setOpaque(true);
        }
        mid = new ImagePanel(new ImageIcon(ICONS_PATH+BACKGROUND_MID).getImage());
        bottom = new ImagePanel(new ImageIcon(ICONS_PATH+BACKGROUND_BOTTOM).getImage());
        
        //--side--
        //  score
        name = new JLabel[2];
        goalDec = new JButton[2];
        goalInc = new JButton[2];
        goals = new JLabel[2];
        kickOff = new JRadioButton[3];
        kickOffGroup = new ButtonGroup();
        pushes = new JLabel[2];
        for (int i=0; i<2; i++) {
            name[i] = new JLabel(Teams.getNames(false)[game.getGameState().team[i].teamNumber]);
            name[i].setHorizontalAlignment(JLabel.CENTER);
            name[i].setForeground(game.getGameState().team[i].teamColor.getColor());
            goalInc[i] = new Button("+");
            goalDec[i] = new Button("-");
            kickOff[i] = new JRadioButton(KICKOFF);
            kickOff[i].setOpaque(false);
            kickOff[i].setHorizontalAlignment(JLabel.CENTER);
            kickOffGroup.add(kickOff[i]);
            goals[i] = new JLabel("0");
            goals[i].setHorizontalAlignment(JLabel.CENTER);
            pushes[i] = new JLabel("0");
            pushes[i].setHorizontalAlignment(JLabel.CENTER);
        }
        kickOff[2] = new JRadioButton();
        kickOffGroup.add(kickOff[2]);
        
        //  robots
        robots = new JPanel[2];
        if (Rules.league.isCoachAvailable) {
            robot = new JButton[2][Rules.league.teamSize+1];
            robotLabel = new JLabel[2][Rules.league.teamSize+1];
            lanIcon = new ImageIcon[2][Rules.league.teamSize+1];
            robotTime = new JProgressBar[2][Rules.league.teamSize+1];
        } else {
            robot = new JButton[2][Rules.league.teamSize];
            robotLabel = new JLabel[2][Rules.league.teamSize];
            lanIcon = new ImageIcon[2][Rules.league.teamSize];
            robotTime = new JProgressBar[2][Rules.league.teamSize];
        }
        for (int i=0; i<2; i++) {
            robots[i] = new JPanel();
            robots[i].setLayout(new GridLayout(robot[i].length, 1, 0, 10));
            robots[i].setOpaque(false);
            
            for (int j=0; j<robot[i].length; j++) {
                robot[i][j] = new Button();
                robotLabel[i][j] = new JLabel();
                robotLabel[i][j].setHorizontalAlignment(JLabel.CENTER);
                lanIcon[i][j] = lanUnknown;
                robotLabel[i][j].setIcon(lanIcon[i][j]);
                robotTime[i][j] = new JProgressBar();
                robotTime[i][j].setMaximum(1000);
                robotTime[i][j].setVisible(false);
                TotalScaleLayout robotLayout = new TotalScaleLayout(robot[i][j]);
                robot[i][j].setLayout(robotLayout);
                robotLayout.add(.1, .1, .8, .5, robotLabel[i][j]);
                robotLayout.add(.1, .7, .8, .2, robotTime[i][j]);
                robots[i].add(robot[i][j]);
            }
        }
        //  team
        timeOut = new JToggleButton[2];
        out = new JButton[2];
        for (int i=0; i<2; i++) {
            timeOut[i] = new ToggleButton(TIMEOUT);
            out[i] = new JButton(OUT);
        }
        if (Rules.league instanceof SPL) {
            stuck = new Button[2];
            for (int i=0; i<2; i++) {
                stuck[i] = new Button();
            }
        }
        
        //--mid--
        //  time
        clockReset = new ImageButton(clockImgReset.getImage());
        clockReset.setOpaque(false);
        clockReset.setBorder(null);
        if (Rules.league.lostTime) {
            clockContainer = new ImagePanel(new ImageIcon(ICONS_PATH+BACKGROUND_CLOCK_SMALL).getImage());
        } else {
            clockContainer = new ImagePanel(new ImageIcon(ICONS_PATH+BACKGROUND_CLOCK).getImage());
        }
        clockContainer.setOpaque(false);
        clock = new JLabel("10:00");
        clock.setForeground(Color.WHITE);
        clock.setHorizontalAlignment(JLabel.CENTER);
        clockPause = new ImageButton(clockImgReset.getImage());
        clockPause.setOpaque(false);
        clockPause.setBorder(null);
        clockSub = new JLabel("0:00");
        clockSub.setHorizontalAlignment(JLabel.CENTER);
        incGameClock = new ImageButton(clockImgPlus.getImage());
        incGameClock.setOpaque(false);
        incGameClock.setBorder(null);
        if (!Rules.league.overtime) {
            firstHalf = new ToggleButton(FIRST_HALF);
            firstHalf.setSelected(true);
            secondHalf = new ToggleButton(SECOND_HALF);
            penaltyShoot = new ToggleButton(PENALTY_SHOOT);
            refereeTimeout = new ToggleButton(REFEREE_TIMEOUT);
            halfGroup = new ButtonGroup();
            halfGroup.add(firstHalf);
            halfGroup.add(secondHalf);
            halfGroup.add(penaltyShoot);
            
            if (Rules.league.isRefereeTimeoutAvailable) {
                halfGroup.add(refereeTimeout);
            }
        } else {
            firstHalf = new ToggleButton(FIRST_HALF_SHORT);
            firstHalf.setSelected(true);
            secondHalf = new ToggleButton(SECOND_HALF_SHORT);
            firstHalfOvertime = new ToggleButton(FIRST_HALF_OVERTIME);
            secondHalfOvertime = new ToggleButton(SECOND_HALF_OVERTIME);
            penaltyShoot = new ToggleButton(PENALTY_SHOOT_SHORT);
            refereeTimeout = new ToggleButton(REFEREE_TIMEOUT);
            halfGroup = new ButtonGroup();
            halfGroup.add(firstHalf);
            halfGroup.add(secondHalf);
            halfGroup.add(firstHalfOvertime);
            halfGroup.add(secondHalfOvertime);
            halfGroup.add(penaltyShoot);
            
            if (Rules.league.isRefereeTimeoutAvailable) {
                halfGroup.add(refereeTimeout);
            }
        }
        // play mode
        initial = new ToggleButton(PLAY_MODE_INITIAL);
        initial.setSelected(true);
        ready = new ToggleButton(PLAY_MODE_READY);
        set = new ToggleButton(PLAY_MODE_SET);
        play = new ToggleButton(PLAY_MODE_PLAY);
        finish = new ToggleButton(PLAY_MODE_FINISH);
        playModeGroup = new ButtonGroup();
        playModeGroup.add(initial);
        playModeGroup.add(ready);
        playModeGroup.add(set);
        playModeGroup.add(play);
        playModeGroup.add(finish);
        // penalties
        if (Rules.league instanceof SPL) {
            pen = new JToggleButton[10];
            pen[0] = new ToggleButton(PEN_PUSHING);
            pen[1] = new ToggleButton(PEN_LEAVING);
            pen[2] = new ToggleButton(PEN_FALLEN);
            pen[3] = new ToggleButton(PEN_INACTIVE);
            pen[4] = new ToggleButton(PEN_DEFENDER);
            pen[5] = new ToggleButton(PEN_HOLDING);
            pen[6] = new ToggleButton(PEN_HANDS);
            pen[7] = new ToggleButton(PEN_PICKUP);
            pen[8] = new ToggleButton(Rules.league.dropInPlayerMode ? TEAMMATE_PUSHING : PEN_COACH_MOTION);
            pen[9] = new ToggleButton(PEN_SUBSTITUTE);
        } else if (Rules.league instanceof HL) {
            pen = new JToggleButton[7];
            pen[0] = new ToggleButton(PEN_MANIPULATION);
            pen[1] = new ToggleButton(PEN_PHYSICAL);
            pen[2] = new ToggleButton(PEN_ATTACK);
            pen[3] = new ToggleButton(PEN_DEFENSE);
            pen[4] = new ToggleButton(PEN_PICKUP_INCAPABLE);
            pen[5] = new ToggleButton(PEN_SERVICE);
            pen[6] = new ToggleButton(PEN_SUBSTITUTE);
            dropBall = new Button(DROP_BALL);
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
        cancelUndo = new Button(CANCEL);
        cancelUndo.setVisible(false);
      
        //--layout--
        TotalScaleLayout layout = new TotalScaleLayout(frame);
        frame.setLayout(layout);
        
        layout.add(0, 0, .3, .04, name[0]);
        layout.add(.7, 0, .3, .04, name[1]);
        layout.add(.01, .05, .08, .07, goalInc[0]);
        layout.add(.91, .05, .08, .07, goalInc[1]);
        layout.add(.01, .13, .08, .06, goalDec[0]);
        layout.add(.91, .13, .08, .06, goalDec[1]);
        layout.add(.17, .05, .12, .04, kickOff[0]);
        layout.add(.71, .05, .12, .04, kickOff[1]);
        layout.add(.21, .09, .08, .07, goals[0]);
        layout.add(.71, .09, .08, .07, goals[1]);
        layout.add(.21, .16, .08, .04, pushes[0]);
        layout.add(.71, .16, .08, .04, pushes[1]);
        layout.add(.01, .21, .28, .55, robots[0]);
        layout.add(.71, .21, .28, .55, robots[1]);
        if (Rules.league instanceof SPL && !Rules.league.dropInPlayerMode) {
            layout.add(.01, .77, .09, .09, timeOut[0]);
            layout.add(.9, .77, .09, .09, timeOut[1]);
            layout.add(.11, .77, .08, .09, stuck[0]);
            layout.add(.81, .77, .08, .09, stuck[1]);
            layout.add(.20, .77, .09, .09, out[0]);
            layout.add(.71, .77, .09, .09, out[1]);
        } else {
            if (Rules.league instanceof SPL) {
                layout.add(.01, .77, .135, .09, stuck[0]);
                layout.add(.855, .77, .135, .09, stuck[1]);
            } else {
                layout.add(.01, .77, .135, .09, timeOut[0]);
                layout.add(.855, .77, .135, .09, timeOut[1]);
            }
            layout.add(.155, .77, .135, .09, out[0]);
            layout.add(.71, .77, .135, .09, out[1]);
        }
        layout.add(.31, .0, .08, .11, clockReset);
        layout.add(.4, .012, .195, .10, clock);
        layout.add(.61, .0, .08, .11, clockPause);
        layout.add(.4, .11, .2, .07, clockSub);
        if (Rules.league.lostTime) {
            layout.add(.590, .0, .03, .11, incGameClock);
            layout.add(.4, .0, .195, .11, clockContainer);
        }
        else{
            layout.add(.4, .0, .2, .11, clockContainer);
        }
        if (!Rules.league.overtime) {
            if (Rules.league.isRefereeTimeoutAvailable && !Rules.league.dropInPlayerMode) {
                layout.add(.31, .19, .09, .06, firstHalf);
                layout.add(.407, .19, .09, .06, secondHalf);
                layout.add(.503, .19, .09, .06, penaltyShoot);
                layout.add(.60, .19, .09, .06, refereeTimeout);
            } else { // no referee timeout in dropInPlayerMode is not supported!
                layout.add(.31, .19, .12, .06, firstHalf);
                layout.add(.44, .19, .12, .06, secondHalf);
                layout.add(.57, .19, .12, .06, Rules.league.dropInPlayerMode ? refereeTimeout : penaltyShoot);
            }
        } else {
            if (Rules.league.isRefereeTimeoutAvailable) {
                layout.add(.31, .19, .06, .06, firstHalf);
                layout.add(.375, .19, .06, .06, secondHalf);
                layout.add(.439, .19, .06, .06, firstHalfOvertime);
                layout.add(.501, .19, .06, .06, secondHalfOvertime);
                layout.add(.565, .19, .06, .06, penaltyShoot);
                layout.add(.63, .19, .06, .06, refereeTimeout);
            } else {
                layout.add(.31, .19, .07, .06, firstHalf);
                layout.add(.3875, .19, .07, .06, secondHalf);
                layout.add(.465, .19, .07, .06, firstHalfOvertime);
                layout.add(.5425, .19, .07, .06, secondHalfOvertime);
                layout.add(.62, .19, .07, .06, penaltyShoot);
            }
        }
        layout.add(.31, .26, .07, .08, initial);
        layout.add(.3875, .26, .07, .08, ready);
        layout.add(.465, .26, .07, .08, set);
        layout.add(.5425, .26, .07, .08, play);
        layout.add(.62, .26, .07, .08, finish);
        if (Rules.league instanceof SPL) {
            layout.add(.31, .37, .185, .08, pen[0]);
            layout.add(.505, .37, .185, .08, pen[1]);
            layout.add(.31, .47, .185, .08, pen[2]);
            layout.add(.505, .47, .185, .08, pen[3]);
            layout.add(.31, .57, .185, .08, pen[4]);
            layout.add(.505, .57, .185, .08, pen[5]);
            layout.add(.31, .67, .185, .08, pen[6]);
            layout.add(.505, .67, .185, .08, pen[7]);
            layout.add(.31, .77, .185, .08, pen[8]);
            if (Rules.league.teamSize > Rules.league.robotsPlaying) {
                layout.add(.505, .77, .185, .08, pen[9]);
            }
        } else if (Rules.league instanceof HL) {
            layout.add(.31,  .38, .185, .08, pen[0]);
            layout.add(.505, .38, .185, .08, pen[1]);
            layout.add(.31,  .48, .185, .08, pen[2]);
            layout.add(.505, .48, .185, .08, pen[3]);
            layout.add(.31,  .58, .185, .08, pen[4]);
            layout.add(.505, .58, .185, .08, pen[5]);
            layout.add(.31,  .68, .185, .08, pen[6]);
            layout.add(.31,  .78, .38,  .08, dropBall);
        }
        layout.add(.08, .88, .84, .11, timelinePanel);
        layout.add(.925, .88, .07, .11, cancelUndo);
        layout.add(0, 0, .3, .87, side[0]);
        layout.add(.3, 0, .4, .87, mid);
        layout.add(.7, 0, .3, .87, side[1]);
        layout.add(0, .87, 1, .132, bottom);
        
        //--listener--
        for (int i=0; i<2; i++) {
            goalDec[i].addActionListener(new ActionListenerAdapter(game, ActionBoard.goalDec[i]));
            goalInc[i].addActionListener(new ActionListenerAdapter(game, ActionBoard.goalInc[i]));
            kickOff[i].addActionListener(new ActionListenerAdapter(game, ActionBoard.kickOff[i]));
            for (int j=0; j<robot[i].length; j++) {
                robot[i][j].addActionListener(new ActionListenerAdapter(game, ActionBoard.robotButton[i][j]));
            }
            timeOut[i].addActionListener(new ActionListenerAdapter(game, ActionBoard.timeOut[i]));
            out[i].addActionListener(new ActionListenerAdapter(game, ActionBoard.out[i]));
            if (Rules.league instanceof SPL) {
                stuck[i].addActionListener(new ActionListenerAdapter(game, ActionBoard.stuck[i]));
            }
        }
        refereeTimeout.addActionListener(new ActionListenerAdapter(game, ActionBoard.refereeTimeout));
        initial.addActionListener(new ActionListenerAdapter(game, ActionBoard.initial));
        ready.addActionListener(new ActionListenerAdapter(game, ActionBoard.ready));
        set.addActionListener(new ActionListenerAdapter(game, ActionBoard.set));
        play.addActionListener(new ActionListenerAdapter(game, ActionBoard.play));
        finish.addActionListener(new ActionListenerAdapter(game, ActionBoard.finish));
        clockReset.addActionListener(new ActionListenerAdapter(game, ActionBoard.clockReset));
        clockPause.addActionListener(new ActionListenerAdapter(game, ActionBoard.clockPause));
        if (Rules.league.lostTime) {
            incGameClock.addActionListener(new ActionListenerAdapter(game, ActionBoard.incGameClock));
        }
        firstHalf.addActionListener(new ActionListenerAdapter(game, ActionBoard.firstHalf));
        secondHalf.addActionListener(new ActionListenerAdapter(game, ActionBoard.secondHalf));
        if (Rules.league.overtime) {
            firstHalfOvertime.addActionListener(new ActionListenerAdapter(game, ActionBoard.firstHalfOvertime));
            secondHalfOvertime.addActionListener(new ActionListenerAdapter(game, ActionBoard.secondHalfOvertime));
        }
        penaltyShoot.addActionListener(new ActionListenerAdapter(game, ActionBoard.penaltyShoot));
        if (Rules.league instanceof SPL) {
            pen[0].addActionListener(new ActionListenerAdapter(game, ActionBoard.pushing));
            pen[1].addActionListener(new ActionListenerAdapter(game, ActionBoard.leaving));
            pen[2].addActionListener(new ActionListenerAdapter(game, ActionBoard.fallen));
            pen[3].addActionListener(new ActionListenerAdapter(game, ActionBoard.inactive));
            pen[4].addActionListener(new ActionListenerAdapter(game, ActionBoard.defender));
            pen[5].addActionListener(new ActionListenerAdapter(game, ActionBoard.holding));
            pen[6].addActionListener(new ActionListenerAdapter(game, ActionBoard.hands));
            pen[7].addActionListener(new ActionListenerAdapter(game, ActionBoard.pickUpSPL));
            pen[8].addActionListener(new ActionListenerAdapter(game, Rules.league.dropInPlayerMode ? ActionBoard.teammatePushing : ActionBoard.coachMotion));
            pen[9].addActionListener(new ActionListenerAdapter(game, ActionBoard.substitute));
        } else if (Rules.league instanceof HL) {
            pen[0].addActionListener(new ActionListenerAdapter(game, ActionBoard.ballManipulation));
            pen[1].addActionListener(new ActionListenerAdapter(game, ActionBoard.pushing));
            pen[2].addActionListener(new ActionListenerAdapter(game, ActionBoard.attack));
            pen[3].addActionListener(new ActionListenerAdapter(game, ActionBoard.defense));
            pen[4].addActionListener(new ActionListenerAdapter(game, ActionBoard.pickUpHL));
            pen[5].addActionListener(new ActionListenerAdapter(game, ActionBoard.service));
            pen[6].addActionListener(new ActionListenerAdapter(game, ActionBoard.substitute));
            dropBall.addActionListener(new ActionListenerAdapter(game, ActionBoard.dropBall));
        }
        for (int i=0; i< undoButtons.length; i++) {
            undoButtons[i].addActionListener(new ActionListenerAdapter(game, ActionBoard.undo[i+1]));
        }
        cancelUndo.addActionListener(new ActionListenerAdapter(game, ActionBoard.cancelUndo));
      
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
    private void update(GameState state)
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
        
        if (Rules.league instanceof SPL) {
            updateGlobalStuck(state);
            updatePenaltiesSPL(state);
        } else if (Rules.league instanceof HL) {
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
        clock.setText(formatTime(state.getRemainingGameTime()));
        Integer secondaryTime = state.getSecondaryTime(KICKOFF_BLOCKED_HIGHLIGHT_SECONDS - 1);
        if (secondaryTime != null) {
            if (state.playMode == PlayMode.Playing) {
                clockSub.setText(formatTime(Math.max(0, secondaryTime)));
                clockSub.setForeground(secondaryTime <= 0
                        && clockSub.getForeground() != COLOR_HIGHLIGHT ? COLOR_HIGHLIGHT : Color.BLACK);
            } else {
                clockSub.setText(formatTime(secondaryTime));
                clockSub.setForeground(Color.BLACK);
            }
        } else {
            clockSub.setText("");
            clockSub.setForeground(Color.BLACK);
        }
        
        ImageIcon tmp;
        if (ActionBoard.clock.isClockRunning(state)) {
            tmp = clockImgPause;
        } else {
            tmp = clockImgPlay;
        }
        clockPause.setImage(tmp.getImage());
        clockReset.setVisible(ActionBoard.clockReset.canExecute(game, state));
        clockPause.setVisible(ActionBoard.clockPause.canExecute(game, state));
        if (Rules.league.lostTime) {
            incGameClock.setEnabled(ActionBoard.incGameClock.canExecute(game, state));
        }
    }
    
    private void updateHalf(GameState state)
    {
        for (int i=0; i<2; i++) {
            name[i].setText(Teams.getNames(false)[state.team[i].teamNumber]);
        }
        firstHalf.setEnabled(ActionBoard.firstHalf.canExecute(game, state));
        secondHalf.setEnabled(ActionBoard.secondHalf.canExecute(game, state));
        if (Rules.league.overtime) {
            firstHalfOvertime.setEnabled(ActionBoard.firstHalfOvertime.canExecute(game, state));
            secondHalfOvertime.setEnabled(ActionBoard.secondHalfOvertime.canExecute(game, state));
        }
        penaltyShoot.setEnabled(ActionBoard.penaltyShoot.canExecute(game, state));
        firstHalf.setSelected((state.period == Period.Normal)
                            && (state.firstHalf));
        secondHalf.setSelected((state.period == Period.Normal)
                            && (!state.firstHalf));
        if (Rules.league.overtime) {
           firstHalfOvertime.setSelected((state.period == Period.Overtime)
                            && (state.firstHalf));
           secondHalfOvertime.setSelected((state.period == Period.Overtime)
                            && (!state.firstHalf));
        }
        penaltyShoot.setSelected(state.period == Period.PenaltyShootout || state.previousPeriod == Period.PenaltyShootout);
    }
    
    private void updateTeamColors(GameState state)
    {
        for (int i=0; i<2; i++) {
            name[i].setForeground(state.team[i].teamColor.getColor());
            side[i].setImage(backgroundSide[i][state.team[i].teamColor.getValue()].getImage());
        }
    }
    
    private void updatePlayMode(GameState state)
    {
        initial.setEnabled(ActionBoard.initial.canExecute(game, state));
        ready.setEnabled(ActionBoard.ready.canExecute(game, state));
        set.setEnabled(ActionBoard.set.canExecute(game, state));
        play.setEnabled(ActionBoard.play.canExecute(game, state));
        finish.setEnabled(ActionBoard.finish.canExecute(game, state));

        if (state.playMode == PlayMode.Initial) {
            initial.setSelected(true);
        } else if (state.playMode == PlayMode.Ready) {
            ready.setSelected(true);
        } else if (state.playMode == PlayMode.Set) {
            set.setSelected(true);
        } else if (state.playMode == PlayMode.Playing) {
            play.setSelected(true);
        } else if (state.playMode == PlayMode.Finished) {
            finish.setSelected(true);
        }

        highlight(finish,
                state.playMode != PlayMode.Finished
                && state.getRemainingGameTime() <= FINISH_HIGHLIGHT_SECONDS
                && finish.getBackground() != COLOR_HIGHLIGHT);
    }
    
    private void updateGoal(GameState state)
    {
        for (int i=0; i<2; i++) {
            goals[i].setText(""+state.team[i].score);
            goalInc[i].setEnabled(ActionBoard.goalInc[i].canExecute(game, state));
            goalDec[i].setVisible(ActionBoard.goalDec[i].canExecute(game, state));
        }
    }
    
    private void updateKickoff(GameState state)
    {
        if (state.kickOffTeam == null) {
            // drop ball
            kickOff[2].setSelected(true);
        } else {
            kickOff[state.team[0].teamColor == state.kickOffTeam ? 0 : 1].setSelected(true);
        }
        for (int i=0; i<2; i++) {
            kickOff[i].setEnabled(ActionBoard.kickOff[i].canExecute(game, state));
            if (state.period != Period.PenaltyShootout
                && state.previousPeriod != Period.PenaltyShootout) {
                kickOff[i].setText(KICKOFF);
            } else {
                kickOff[i].setText(KICKOFF_PENALTY_SHOOTOUT);
            }
        }
    }
    
    private void updatePushes(GameState state)
    {
        for (int i=0; i<2; i++) {
            if (state.period != Period.PenaltyShootout && state.previousPeriod != Period.PenaltyShootout) {
                if (Rules.league.pushesToEjection == null || Rules.league.pushesToEjection.length == 0) {
                    pushes[i].setText("");
                } else {
                    pushes[i].setText(PUSHES+": "+state.pushes[i]);
                }
            } else {
                pushes[i].setText((i == 0 && (state.playMode == PlayMode.Set
                        || state.playMode == PlayMode.Playing) ? SHOT : SHOTS)+": "+state.team[i].penaltyShot);
            }
        }
    }
    
    private void updateRobots(GameState state)
    {
        RobotOnlineStatus[][] onlineStatus = robotWatcher.updateRobotOnlineStatus();

        for (int i=0; i<robot.length; i++) {
            for (int j=0; j<robot[i].length; j++) {
                if (ActionBoard.robotButton[i][j].isCoach()) {
                   if (state.team[i].coach.penalty == Penalty.SplCoachMotion) {
                      robot[i][j].setEnabled(false);
                      robotLabel[i][j].setText(EJECTED);
                  } else {
                      robotLabel[i][j].setText(state.team[i].teamColor+" "+COACH);
                  }
                }
                else {
                    if (state.team[i].player[j].penalty != Penalty.None) {
                        if (!state.ejected[i][j]) {
                            int seconds = state.getRemainingPenaltyTime(i, j);
                            boolean pickup = ((Rules.league instanceof SPL &&
                                        state.team[i].player[j].penalty == Penalty.SplRequestForPickup)
                                   || (Rules.league instanceof HL &&
                                       ( state.team[i].player[j].penalty == Penalty.HLPickupOrIncapable
                                      || state.team[i].player[j].penalty == Penalty.Service))
                                    );
                            if (seconds == 0) {
                                if (pickup) {
                                    robotLabel[i][j].setText(state.team[i].teamColor+" "+(j+1)+" ("+PEN_PICKUP+")");
                                    highlight(robot[i][j], true);
                                } else if (state.team[i].player[j].penalty == Penalty.Substitute) {
                                    robotLabel[i][j].setText(state.team[i].teamColor+" "+(j+1)+" ("+PEN_SUBSTITUTE_SHORT+")");
                                    highlight(robot[i][j], false);
                                } else if (!(Rules.league instanceof SPL) ||
                                        !(state.team[i].player[j].penalty == Penalty.SplCoachMotion)) {
                                    robotLabel[i][j].setText(state.team[i].teamColor+" "+(j+1)+": "+formatTime(seconds));
                                    highlight(robot[i][j], seconds <= UNPEN_HIGHLIGHT_SECONDS && robot[i][j].getBackground() != COLOR_HIGHLIGHT);
                                }
                            }  else {
                                robotLabel[i][j].setText(state.team[i].teamColor+" "+(j+1)+": "+formatTime(seconds)+(pickup ? " (P)" : ""));
                                highlight(robot[i][j], seconds <= UNPEN_HIGHLIGHT_SECONDS && robot[i][j].getBackground() != COLOR_HIGHLIGHT);
                            }
                            int penTime = (seconds + state.getSecondsSince(state.whenPenalized[i][j]));
                            if (seconds != 0) {
                                robotTime[i][j].setValue(1000 * seconds / penTime);
                            }
                            robotTime[i][j].setVisible(seconds != 0);
                        } else {
                            robotLabel[i][j].setText(EJECTED);
                            robotTime[i][j].setVisible(false);
                            highlight(robot[i][j], false);
                        }
                    } else {
                        robotLabel[i][j].setText(state.team[i].teamColor+" "+(j+1));
                        robotTime[i][j].setVisible(false);
                        highlight(robot[i][j], false);
                    }
                }    
                
                robot[i][j].setEnabled(ActionBoard.robotButton[i][j].canExecute(game, state));
                
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
                timeOut[i].setSelected(false);
                highlight(timeOut[i], false);
            } else {
                boolean shouldHighlight = (state.getRemainingSeconds(state.whenCurrentPlayModeBegan, Rules.league.timeOutTime) < TIMEOUT_HIGHLIGHT_SECONDS)
                        && (timeOut[i].getBackground() != COLOR_HIGHLIGHT);
                timeOut[i].setSelected(!IS_OSX || !shouldHighlight);
                highlight(timeOut[i], shouldHighlight);
            }
            timeOut[i].setEnabled(ActionBoard.timeOut[i].canExecute(game, state));
        }
    }
    
    private void updateRefereeTimeout(GameState state)
    {
        refereeTimeout.setSelected(state.refereeTimeout);
        refereeTimeout.setEnabled(ActionBoard.refereeTimeout.canExecute(game, state));
    }
    
    private void updateGlobalStuck(GameState state)
    {
        for (int i=0; i<2; i++) {
            if (state.playMode == PlayMode.Playing
                    && state.getRemainingSeconds(state.whenCurrentPlayModeBegan, Rules.league.kickoffTime + Rules.league.minDurationBeforeStuck) > 0)
            {
                if (state.kickOffTeam == state.team[i].teamColor)
                {
                    stuck[i].setEnabled(true);
                    stuck[i].setText("<font color=#000000>"+KICKOFF_GOAL);
                } else {
                    stuck[i].setEnabled(false);
                    stuck[i].setText("<font color=#808080>"+STUCK);
                }
            } else {
                stuck[i].setEnabled(ActionBoard.stuck[i].canExecute(game, state));
                stuck[i].setText((ActionBoard.stuck[i].canExecute(game, state) ? "<font color=#000000>" : "<font color=#808080>")+STUCK);
            }
        }
    }
    
    private void updateDropBall(GameState state)
    {
        dropBall.setEnabled(ActionBoard.dropBall.canExecute(game, state));
    }
    
    private void updateOut(GameState state)
    {
        for (int i=0; i<2; i++) {
            out[i].setEnabled(ActionBoard.out[i].canExecute(game, state));
        }
    }

    private void updatePenaltiesSPL(GameState state)
    {
        pen[0].setEnabled(ActionBoard.pushing.canExecute(game, state));
        pen[1].setEnabled(ActionBoard.leaving.canExecute(game, state));
        pen[2].setEnabled(ActionBoard.fallen.canExecute(game, state));
        pen[3].setEnabled(ActionBoard.inactive.canExecute(game, state));
        pen[3].setText("<html><center>"
                +(ActionBoard.inactive.canExecute(game, state) ? "<font color=#000000>" : "<font color=#808080>")
                +PEN_INACTIVE);
        pen[4].setEnabled(ActionBoard.defender.canExecute(game, state));
        pen[5].setEnabled(ActionBoard.holding.canExecute(game, state));
        pen[6].setEnabled(ActionBoard.hands.canExecute(game, state));
        pen[7].setEnabled(ActionBoard.pickUpSPL.canExecute(game, state));
        pen[8].setEnabled(Rules.league.dropInPlayerMode
                ? ActionBoard.teammatePushing.canExecute(game, state)
                : ActionBoard.coachMotion.canExecute(game, state));
        pen[9].setEnabled(ActionBoard.substitute.canExecute(game, state));
        
        Action highlightAction = game.getLastUserAction();
        pen[0].setSelected(highlightAction == ActionBoard.pushing);
        pen[1].setSelected(highlightAction == ActionBoard.leaving);
        pen[2].setSelected(highlightAction == ActionBoard.fallen);
        pen[3].setSelected(highlightAction == ActionBoard.inactive);
        pen[4].setSelected(highlightAction == ActionBoard.defender);
        pen[5].setSelected(highlightAction == ActionBoard.holding);
        pen[6].setSelected(highlightAction == ActionBoard.hands);
        pen[7].setSelected(highlightAction == ActionBoard.pickUpSPL);
        pen[8].setSelected(Rules.league.dropInPlayerMode
                ? highlightAction == ActionBoard.teammatePushing
                : highlightAction == ActionBoard.coachMotion);
        pen[9].setSelected(highlightAction == ActionBoard.substitute);
    }
    
    private void updatePenaltiesHL(GameState state)
    {
        pen[0].setEnabled(ActionBoard.ballManipulation.canExecute(game, state));
        pen[1].setEnabled(ActionBoard.pushing.canExecute(game, state));
        pen[2].setEnabled(ActionBoard.attack.canExecute(game, state));
        pen[3].setEnabled(ActionBoard.defense.canExecute(game, state));
        pen[4].setEnabled(ActionBoard.pickUpHL.canExecute(game, state));
        pen[5].setEnabled(ActionBoard.service.canExecute(game, state));
        pen[6].setEnabled(ActionBoard.substitute.canExecute(game, state));

        Action highlightAction = game.getLastUserAction();
        pen[0].setSelected(highlightAction == ActionBoard.ballManipulation);
        pen[1].setSelected(highlightAction == ActionBoard.pushing);
        pen[2].setSelected(highlightAction == ActionBoard.attack);
        pen[3].setSelected(highlightAction == ActionBoard.defense);
        pen[4].setSelected(highlightAction == ActionBoard.pickUpHL);
        pen[5].setSelected(highlightAction == ActionBoard.service);
        pen[6].setSelected(highlightAction == ActionBoard.substitute);
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
        cancelUndo.setVisible(isUndoingAnything);
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
            name[i].setFont(titleFont);
            goalInc[i].setFont(standardFont);
            goalDec[i].setFont(standardFont);
            kickOff[i].setFont(standardFont);
            goals[i].setFont(goalsFont);
            pushes[i].setFont(standardFont);
            for (int j=0; j<robot[i].length; j++) {
                robotLabel[i][j].setFont(titleFont);
            }
            timeOut[i].setFont(timeoutFont);
            out[i].setFont(timeoutFont);
            if (Rules.league instanceof SPL) {
                stuck[i].setFont(timeoutFont);
            }
        }
        clock.setFont(timeFont);
        clockSub.setFont(timeSubFont);
        
        firstHalf.setFont(timeoutFont);
        secondHalf.setFont(timeoutFont);
        if (Rules.league.overtime) {
            firstHalfOvertime.setFont(timeoutFont);
            secondHalfOvertime.setFont(timeoutFont);
        }
        penaltyShoot.setFont(timeoutFont);
        if (Rules.league.isRefereeTimeoutAvailable) {
            refereeTimeout.setFont(timeoutFont);
        }

        initial.setFont(playModeFont);
        ready.setFont(playModeFont);
        set.setFont(playModeFont);
        play.setFont(playModeFont);
        finish.setFont(playModeFont);
        for (JToggleButton penaltyButton : pen) {
            penaltyButton.setFont(standardFont);
        }
        if (dropBall != null) {
            dropBall.setFont(standardFont);
        }
        for (JToggleButton undoButton : undoButtons) {
            undoButton.setFont(timeoutFont);
        }
        cancelUndo.setFont(standardFont);
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
