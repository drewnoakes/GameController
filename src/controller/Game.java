package controller;

import common.Event;
import common.Log;
import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.action.ActionTrigger;
import data.*;
import leagues.LeagueSettings;

import java.io.*;
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
     *
     * The instance provided via this event is a clone, offering thread-safety and
     * keeping the game's copy of the state private.
     */
    public final Event<GameState> gameStateChanged;

    private final League league;
    private final Pair<Team> teams;
    private final UIOrientation uiOrientation;
    private final boolean isPlayOff;
    private final boolean isFullScreen;
    private final boolean changeColoursEachPeriod;
    private final TeamColor initialKickOffColor;
    private final String broadcastAddress;

    /** The golden record of the game's current state. */
    private GameState gameState;

    /**
     * When each action completes, this field is updated with a clone of the resulting state.
     * It may be safely read by all threads, but attempts to modify it will be overwritten.
     */
    private GameState gameStateClone;

    /** The last {@link Action} that was executed with trigger {@link ActionTrigger#User}. */
    private Action lastUserAction;
    private boolean skipStoringLastUserAction;

    /** When set to true, the game will stop and the game controller window close. */
    private boolean shutdownRequested = false;

    /** Create a new Game with the specified options. */
    public Game(GameOptions options)
    {
        assert(options.isPlayOff != null);

        this.league = options.league;
        this.teams = options.teams; // TODO clone mutable bits of this object for single use during game
        this.uiOrientation = options.orientation; // TODO clone mutable bits of this object for single use during game
        this.isPlayOff = options.isPlayOff;
        this.isFullScreen = options.isFullScreen;
        this.changeColoursEachPeriod = options.changeColoursEachPeriod;
        this.initialKickOffColor = options.initialKickOffColor;
        this.broadcastAddress = options.broadcastAddress;

        gameStateChanged = new Event<GameState>();

        gameState = new GameState(this);
        gameState.nextKickOffColor = options.initialKickOffColor;

        gameStateClone = cloneGameState(gameState);

        pushState(teams.get(UISide.Left).getName() + " vs " + teams.get(UISide.Right).getName());
    }

    @NotNull
    public League league()
    {
        return league;
    }

    @NotNull
    public LeagueSettings settings()
    {
        return league.settings();
    }

    public boolean isPlayOff()
    {
        return isPlayOff;
    }

    @NotNull
    public Pair<Team> teams()
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

        gameStateClone = cloneGameState(gameState);
        gameStateChanged.fire(gameStateClone);
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
        timeline.add(new TimelineEntry(cloneGameState(gameState), title));

        Log.toFile(title);
    }

    /**
     * Gets the current game state. Note that changes to the returned object will have no
     * lasting effect.
     */
    @NotNull
    public GameState getGameState()
    {
        return gameStateClone;
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

        long latestTimestamp = timeline.peek().getState().whenCurrentPlayModeBegan;
        long timeInCurrentState = timeline.peek().getState().getTime() - latestTimestamp;

        // Pop the specified number of states, and keep the oldest removed state
        TimelineEntry oldestRemoved = null;
        for (int i = 0; i < stateCount; i++) {
            oldestRemoved = timeline.pop();
        }
        assert(oldestRemoved != null);
        long earliestTimestamp = oldestRemoved.getState().whenCurrentPlayModeBegan;

        if (latestTimestamp != timeline.peek().getState().whenCurrentPlayModeBegan) {
            long timeOffset = latestTimestamp - earliestTimestamp + timeInCurrentState;
            for (TimelineEntry entry : timeline) {
                entry.getState().whenCurrentPlayModeBegan += timeOffset;
            }
        }

        GameState state = timeline.peek().getState();

        this.gameState = cloneGameState(state);
        this.gameStateClone = cloneGameState(state);

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
     * Generically clone this object. Everything referenced must be Serializable.
     *
     * @param gameState the object to clone
     * @return A deep copy of this object.
     */
    private GameState cloneGameState(GameState gameState)
    {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            new ObjectOutputStream(out).writeObject(gameState);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            GameState clone = (GameState)new ObjectInputStream(in).readObject();
            // Populate transient fields
            clone.game = this;
            return clone;
        } catch (ClassNotFoundException e) {
            Log.error(e.getClass().getName() + ": " + e.getMessage());
        } catch (IOException e) {
            Log.error(e.getClass().getName() + ": " + e.getMessage());
        }
        System.exit(1);
        return null;
    }
}
