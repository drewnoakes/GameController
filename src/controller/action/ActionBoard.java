package controller.action;

import controller.action.clock.ClockTick;
import controller.action.net.Manual;
import controller.action.ui.*;
import controller.action.ui.penalty.*;
import controller.action.ui.period.*;
import controller.action.ui.playmode.*;
import data.*;

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
    
    public static ReadOnlyPair<Goal> goalDec;
    public static ReadOnlyPair<Goal> goalInc;
    public static ReadOnlyPair<KickOff> kickOff;
    public static ReadOnlyPair<RobotButton[]> robotButton;
    public static ReadOnlyPair<TimeOut> timeOut;
    public static ReadOnlyPair<GlobalStuck> stuck;
    public static ReadOnlyPair<Out> out;
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

    public static ReadOnlyPair<Manual[]> manualPen;
    public static ReadOnlyPair<Manual[]> manualUnpen;

    private ActionBoard() {}

    /**
     * This must be called before using actions from this class. It creates
     * all the actions instances.
     */
    public static void initialise(League league, UIOrientation uiOrientation)
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

        robotButton = new Pair<RobotButton[]>(uiOrientation, new RobotButton[robotCount], new RobotButton[robotCount]);
        goalDec = new Pair<Goal>(uiOrientation, new Goal(UISide.Left, -1), new Goal(UISide.Right, -1));
        goalInc = new Pair<Goal>(uiOrientation, new Goal(UISide.Left, 1), new Goal(UISide.Right, 1));
        kickOff = new Pair<KickOff>(uiOrientation, new KickOff(UISide.Left), new KickOff(UISide.Right));
        timeOut = new Pair<TimeOut>(uiOrientation, new TimeOut(UISide.Left), new TimeOut(UISide.Right));
        stuck = new Pair<GlobalStuck>(uiOrientation, new GlobalStuck(UISide.Left), new GlobalStuck(UISide.Right));
        out = new Pair<Out>(uiOrientation, new Out(UISide.Left), new Out(UISide.Right));

        for (UISide side : UISide.both()) {
            for (int j = 0; j < robotButton.get(side).length; j++) {
                robotButton.get(side)[j] = new RobotButton(league, side, j + 1);
            }
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

        manualPen = new Pair<Manual[]>(uiOrientation, new Manual[robotCount], new Manual[robotCount]);
        manualUnpen = new Pair<Manual[]>(uiOrientation, new Manual[robotCount], new Manual[robotCount]);

        for (UISide side : UISide.both()) {
            for (int j = 0; j < robotCount; j++) {
                manualPen.get(side)[j] = new Manual(side, j + 1, false);
                manualUnpen.get(side)[j] = new Manual(side, j + 1, true);
            }
        }
    }
}
