package controller.action;

import controller.action.clock.ClockTick;
import controller.action.net.Manual;
import controller.action.ui.*;
import controller.action.ui.penalty.*;
import controller.action.ui.period.*;
import controller.action.ui.playmode.*;
import data.League;
import data.UISide;

/**
 * This class holds global instances of most actions, for when you want to
 * execute or identify them.
 * <p>
 * It may be useful to have instances of actions that are not listed here,
 * that would be ok but for basic features it should not be needed.
 * Because of multi-threading you should not take actions from here to write
 * into their attributes. However, you should always avoid writing in
 * action's attributes except in their constructor.
 * <p>
 * You can read a detailed description of each action in its class.
 *
 * @author Michel Bartsch
 */
public class ActionBoard
{
    public static ClockTick clock;
    
    public static Quit quit;
    public static Testmode testmode;
    public static Undo[] undo;
    public static CancelUndo cancelUndo;
    public static final int MAX_NUM_UNDOS_AT_ONCE = 8;
    
    public static final Goal[] goalDec = new Goal[2];
    public static final Goal[] goalInc = new Goal[2];
    public static final KickOff[] kickOff = new KickOff[2];
    public static RobotButton[][] robotButton;
    public static final TimeOut[] timeOut = new TimeOut[2];
    public static final GlobalStuck[] stuck = new GlobalStuck[2];
    public static final Out[] out = new Out[2];
    public static ClockReset clockReset;
    public static ClockPause clockPause;
    public static IncGameClock incGameClock;
    public static FirstHalf firstHalf;
    public static SecondHalf secondHalf;
    public static FirstHalfOvertime firstHalfOvertime;
    public static SecondHalfOvertime secondHalfOvertime;
    public static PenaltyShoot penaltyShoot;
    public static RefereeTimeout refereeTimeout;
    public static Initial initial;
    public static Ready ready;
    public static Set set;
    public static Play play;
    public static Finish finish;
    public static Pushing pushing;
    public static Leaving leaving;
    public static Fallen fallen;
    public static Inactive inactive;
    public static Defender defender;
    public static Holding holding;
    public static Hands hands;
    public static PickUpSPL pickUpSPL;
    public static BallManipulation ballManipulation;
    public static Attack attack;
    public static Defense defense;
    public static PickUpHL pickUpHL;
    public static Service service;
    public static CoachMotion coachMotion;
    public static TeammatePushing teammatePushing;
    public static Substitute substitute;
    public static DropBall dropBall;

    public static Manual[][] manualPen;
    public static Manual[][] manualUnpen;

    private ActionBoard() {}

    /**
     * This must be called before using actions from this class. It creates
     * all the actions instances.
     */
    public static void initalise(League league)
    {
        clock = new ClockTick();
        
        quit = new Quit();
        testmode = new Testmode();
        undo = new Undo[MAX_NUM_UNDOS_AT_ONCE];
        for (int i=0; i<undo.length; i++) {
            undo[i] = new Undo(i);
        }
        cancelUndo = new CancelUndo();

        // We construct team arrays during initialisation as the league may change between runs
        int robotCount = league.settings().teamSize + (league.settings().isCoachAvailable ? 1 : 0);

        robotButton = new RobotButton[2][robotCount];

        for (int i=0; i<2; i++) {
            UISide side = i == 0 ? UISide.Left : UISide.Right;
            goalDec[i] = new Goal(side, -1);
            goalInc[i] = new Goal(side, 1);
            kickOff[i] = new KickOff(side);
            for (int j=0; j< robotButton[i].length; j++) {
                robotButton[i][j] = new RobotButton(league, side, j + 1);
            }
            timeOut[i] = new TimeOut(side);
            stuck[i] = new GlobalStuck(side);
            out[i] = new Out(side);
        }
        
        clockReset = new ClockReset();
        clockPause = new ClockPause();
        incGameClock = new IncGameClock();
        firstHalf = new FirstHalf();
        secondHalf = new SecondHalf();
        firstHalfOvertime = new FirstHalfOvertime();
        secondHalfOvertime = new SecondHalfOvertime();
        penaltyShoot = new PenaltyShoot();
        refereeTimeout = new RefereeTimeout();

        initial = new Initial();
        ready = new Ready();
        set = new Set();
        play = new Play();
        finish = new Finish();

        pushing = new Pushing();
        leaving = new Leaving();
        fallen = new Fallen();
        inactive = new Inactive();
        defender = new Defender();
        holding = new Holding();
        hands = new Hands();
        pickUpSPL = new PickUpSPL();
        ballManipulation = new BallManipulation();
        attack = new Attack();
        defense = new Defense();
        pickUpHL = new PickUpHL();
        service = new Service();
        coachMotion = new CoachMotion();
        teammatePushing = new TeammatePushing();
        substitute = new Substitute();
        dropBall = new DropBall();

        manualPen = new Manual[2][robotCount];
        manualUnpen = new Manual[2][robotCount];
        for (int i=0; i<2; i++) {
            UISide side = i == 0 ? UISide.Left : UISide.Right;
            for (int j=0; j<robotCount; j++) {
                manualPen[i][j] = new Manual(side, j + 1, false);
                manualUnpen[i][j] = new Manual(side, j + 1, true);
            }
        }
    }
}
