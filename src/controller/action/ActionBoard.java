package controller.action;

import controller.action.clock.ClockTick;
import controller.action.net.Manual;
import controller.action.ui.CancelUndo;
import controller.action.ui.ClockPause;
import controller.action.ui.ClockReset;
import controller.action.ui.DropBall;
import controller.action.ui.GlobalStuck;
import controller.action.ui.Goal;
import controller.action.ui.IncGameClock;
import controller.action.ui.KickOff;
import controller.action.ui.Out;
import controller.action.ui.Quit;
import controller.action.ui.period.RefereeTimeout;
import controller.action.ui.Robot;
import controller.action.ui.TeammatePushing;
import controller.action.ui.Testmode;
import controller.action.ui.period.TimeOut;
import controller.action.ui.Undo;
import controller.action.ui.period.FirstHalf;
import controller.action.ui.period.FirstHalfOvertime;
import controller.action.ui.period.PenaltyShoot;
import controller.action.ui.period.SecondHalf;
import controller.action.ui.period.SecondHalfOvertime;
import controller.action.ui.penalty.Attack;
import controller.action.ui.penalty.BallManipulation;
import controller.action.ui.penalty.CoachMotion;
import controller.action.ui.penalty.Defender;
import controller.action.ui.penalty.Defense;
import controller.action.ui.penalty.Fallen;
import controller.action.ui.penalty.Hands;
import controller.action.ui.penalty.Holding;
import controller.action.ui.penalty.Inactive;
import controller.action.ui.penalty.Leaving;
import controller.action.ui.penalty.PickUp;
import controller.action.ui.penalty.PickUpHL;
import controller.action.ui.penalty.Pushing;
import controller.action.ui.penalty.ServiceHL;
import controller.action.ui.penalty.Substitute;
import controller.action.ui.playmode.Finish;
import controller.action.ui.playmode.Initial;
import controller.action.ui.playmode.Play;
import controller.action.ui.playmode.Ready;
import controller.action.ui.playmode.Set;
import rules.Rules;

/**
 * This class actually holds static every instance of an action to get these
 * actions wherever you want to execute or identify them.
 * It may be useful to have instances of actions that are not listed here,
 * that would be ok but for basic features it should not be needed.
 * Because of multi-threading you should not take actions from here to write
 * into their attributes. However, you should always avoid writing in
 * action`s attributes except in their constructor.
 *
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
    public static Robot[][] robot;
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
    public static PickUp pickUp;
    public static BallManipulation ballManipulation;
    public static Attack attack;
    public static Defense defense;
    public static PickUpHL pickUpHL;
    public static ServiceHL serviceHL;
    public static CoachMotion coachMotion;
    public static TeammatePushing teammatePushing;
    public static Substitute substitute;
    public static DropBall dropBall;

    public static final Manual[][] manualPen = Rules.league.isCoachAvailable ? new Manual[2][Rules.league.teamSize+1] : new Manual[2][Rules.league.teamSize];
    public static final Manual[][] manualUnpen = Rules.league.isCoachAvailable ? new Manual[2][Rules.league.teamSize+1] : new Manual[2][Rules.league.teamSize];

    private ActionBoard() {}

    /**
     * This must be called before using actions from this class. It creates
     * all the actions instances.
     */
    public static void init()
    {
        clock = new ClockTick();
        
        quit = new Quit();
        testmode = new Testmode();
        undo = new Undo[MAX_NUM_UNDOS_AT_ONCE];
        for (int i=0; i<undo.length; i++) {
            undo[i] = new Undo(i);
        }
        cancelUndo = new CancelUndo();
        
        if (Rules.league.isCoachAvailable) {
            robot = new Robot[2][Rules.league.teamSize+1];
        } else {
            robot = new Robot[2][Rules.league.teamSize];
        }
        
        for (int i=0; i<2; i++) {
            goalDec[i] = new Goal(i, -1);
            goalInc[i] = new Goal(i, 1);
            kickOff[i] = new KickOff(i);
            for (int j=0; j<robot[i].length; j++) {
                robot[i][j] = new Robot(i, j);
            }
            timeOut[i] = new TimeOut(i);
            stuck[i] = new GlobalStuck(i);
            out[i] = new Out(i);
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
        pickUp = new PickUp();
        ballManipulation = new BallManipulation();
        attack = new Attack();
        defense = new Defense();
        pickUpHL = new PickUpHL();
        serviceHL = new ServiceHL();
        coachMotion = new CoachMotion();
        teammatePushing = new TeammatePushing();
        substitute = new Substitute();
        dropBall = new DropBall();
        
        for (int i=0; i<2; i++) {
            for (int j=0; j<Rules.league.teamSize; j++) {
                manualPen[i][j] = new Manual(i, j, false);
                manualUnpen[i][j] = new Manual(i, j, true);
            }
        }
    }
}
