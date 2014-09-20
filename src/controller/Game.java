package controller;

import common.Event;
import common.Log;
import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.action.ActionTrigger;
import data.*;
import leagues.LeagueRules;

import java.util.Random;
import java.util.Stack;

/**
 * Governs the process surrounding a complete game of RoboCup soccer.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class Game
{
    /**
     * The stack of game states on the timeline.
     * <p>
     * States are pushed onto the stack via {@link Game#pushState(String)}.
     * <p>
     * Game state may be reverted via {@link Game#undo(int)}.
     */
    private final Stack<TimelineEntry> timeline = new Stack<TimelineEntry>();

    /**
     * Fires when the game state changed.
     * <p>
     * The instance provided via this event is readonly, offering thread-safety.
     */
    public final Event<ReadOnlyGameState> gameStateChanged;

    private final League league;
    private final ReadOnlyPair<Team> teams;
    private final UIOrientation uiOrientation;
    private final boolean isPlayOff;
    private final boolean isFullScreen;
    private final boolean changeColoursEachPeriod;
    private final TeamColor initialKickOffColor;
    private final String broadcastAddress;
    private final int gameId;

    /** The golden record of the game's current state. */
    private GameState gameState;

    /** The last {@link Action} that was executed with trigger {@link ActionTrigger#User}. */
    private Action lastUserAction;
    private boolean skipStoringLastUserAction;

    /** When set to true, the game will stop and the game controller window close. */
    private boolean shutdownRequested = false;

    /** Create a new Game with the specified options. */
    public Game(@NotNull GameOptions options)
    {
        assert(options.isPlayOff != null);

        this.gameId = new Random().nextInt();

        this.league = options.league;
        this.teams = options.teams;
        this.uiOrientation = options.orientation;
        this.isPlayOff = options.isPlayOff;
        this.isFullScreen = options.isFullScreen;
        this.changeColoursEachPeriod = options.changeColoursEachPeriod;
        this.initialKickOffColor = options.initialKickOffColor;
        this.broadcastAddress = options.broadcastAddress;

        gameStateChanged = new Event<ReadOnlyGameState>();

        gameState = new GameState(this);
        gameState.setNextKickOffColor(options.initialKickOffColor);

        pushState(teams.get(UISide.Left).getName() + " vs " + teams.get(UISide.Right).getName());
    }

    /** Gets the league this game is being played in. */
    @NotNull
    public League league()
    {
        return league;
    }

    /** Gets the set of game rules applicable to the league this game is being played in. */
    @NotNull
    public LeagueRules rules()
    {
        return league.rules();
    }

    public boolean isPlayOff()
    {
        return isPlayOff;
    }

    @NotNull
    public ReadOnlyPair<Team> teams()
    {
        return teams;
    }

    @NotNull
    public UIOrientation uiOrientation()
    {
        return uiOrientation;
    }

    public boolean isFullScreen()
    {
        return isFullScreen;
    }

    public boolean changeColoursEachPeriod()
    {
        return changeColoursEachPeriod;
    }

    /** The colour of the team given the first kickoff of the game. */
    public TeamColor initialKickOffColor()
    {
        return initialKickOffColor;
    }

    @NotNull
    public String broadcastAddress()
    {
        return broadcastAddress;
    }

    /**
     * Attempt to apply the specified {@link Action} the the game's state.
     * <p>
     * If the action's {@link Action#canExecute} method returns <code>false</code>, this
     * method returns and the game state is unchanged.
     * <p>
     * When the state is successfully changed, the {@link Game#gameStateChanged} event is fired.
     * <p>
     * This method is <code>synchronized</code> so that concurrent requests are handled serially.
     *
     * @param action the action to attempt to apply to the game's state.
     * @param trigger an indication of what triggered this action to be attempted.
     */
    public synchronized void apply(@NotNull Action action, ActionTrigger trigger)
    {
        if (!action.canExecute(this, gameState))
            return;

        assert(!skipStoringLastUserAction);

        action.execute(this, gameState);

        if (trigger == ActionTrigger.User && !skipStoringLastUserAction)
            lastUserAction = action;

        skipStoringLastUserAction = false;

        gameStateChanged.fire(gameState);
    }

    /**
     * Pushes the current game state onto the timeline for visibility.
     * <p>
     * States on the timeline provide visibility of past actions, and allow erroneous
     * actions to be undone.
     *
     * @param title the text to appear on the timeline for this persisted state.
     */
    public void pushState(@NotNull String title)
    {
        timeline.add(new TimelineEntry(gameState.clone(), title));

        Log.toFile(title);
    }

    /**
     * Gets the current game state. Note that changes to the returned object will have no
     * lasting effect.
     */
    @NotNull
    public ReadOnlyGameState getGameState()
    {
        return gameState;
    }

    /**
     * Gets the last {@link Action} instance to be executed in response to the trigger
     * {@link ActionTrigger#User}. May be <code>null</code>.
     */
    @Nullable
    public Action getLastUserAction()
    {
        return lastUserAction;
    }

    /**
     * Clears the last {@link Action} instance to be executed in response to the trigger
     * {@link ActionTrigger#User}.
     */
    public void clearLastUserAction()
    {
        skipStoringLastUserAction = true;
        lastUserAction = null;
    }

    /**
     * Gets the last N titles of states in the timeline.
     *
     * @param count the maximum number of state titles to return.
     *
     * @return the title strings, as provided to @{link Game#pushState}.
     */
    public String[] getLastTimelineTitles(int count)
    {
        String[] out = new String[count];
        for (int i=0; i<count; i++) {
            if (timeline.size()-1-i >= 0) {
                out[i] = timeline.get(timeline.size()-1-i).getTitle();
            } else {
                out[i] = "";
            }
        }
        return out;
    }

    /**
     * Reverts the game state to some prior position in the timeline. Supports 'undo'.
     *
     * If a game state change is undone, the time when it was left is restored.
     * Thereby, there whole remaining log is moved into the new time frame.
     *
     * @param stateCount the number of states to go back
     */
    public void undo(int stateCount)
    {
        assert(stateCount > 0);

        // Don't allow undoing the first state
        stateCount = Math.min(stateCount, timeline.size() - 1);

        long latestTimestamp = timeline.peek().getState().getWhenCurrentPlayModeBegan();
        long timeInCurrentState = timeline.peek().getState().getTime() - latestTimestamp;

        // Pop the specified number of states, and keep the oldest removed state
        TimelineEntry oldestRemoved = null;
        for (int i = 0; i < stateCount; i++) {
            oldestRemoved = timeline.pop();
        }
        assert(oldestRemoved != null);
        long earliestTimestamp = oldestRemoved.getState().getWhenCurrentPlayModeBegan();

        if (latestTimestamp != timeline.peek().getState().getWhenCurrentPlayModeBegan()) {
            long timeOffset = latestTimestamp - earliestTimestamp + timeInCurrentState;
            for (TimelineEntry entry : timeline) {
                entry.getState().setWhenCurrentPlayModeBegan(entry.getState().getWhenCurrentPlayModeBegan() + timeOffset);
            }
        }

        GameState state = timeline.peek().getState();

        this.gameState = state.clone();

        Log.toFile("Undo " + stateCount + " States to " + timeline.peek().getTitle());
    }

    /**
     * Requests that the game be stopped and the game controller UI be closed.
     */
    public void requestShutdown()
    {
        shutdownRequested = true;
    }

    /**
     * Gets whether a request has been received to stop the game and close the game controller UI.
     */
    public boolean isShutdownRequested()
    {
        return shutdownRequested;
    }

    /**
     * Gets a randomly chosen number that 'uniquely' (1 in 2^32) identifies this game.
     * <p>
     * Can be used by robots to defend against problems seen when multiple game controllers
     * are running.
     *
     * @return the current game ID
     */
    public int gameId()
    {
        return gameId;
    }
}
