package controller;

import common.Event;
import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.action.ActionTrigger;
import controller.action.GCAction;
import data.GameState;

import java.awt.EventQueue;

/**
 * If the actionPerformed method of an action is called, it executes the register
 * method of this class. Later the action`s perform method will be called
 * in the GUI`s thread. This is to avoid resource conflicts caused by multiple
 * threads without massive use of synchronized get- and set-methods.
 *
 * This class is a singleton!
 *
 * @author Michel Bartsch
 */
public class ActionHandler
{
    /* SINGLETON MEMBERS ------------------------------------------------------------------- */

    /** The instance of the singleton. */
    @Nullable
    private static ActionHandler instance;

    /**
     * To get the singleton instance for public attribute access.
     *
     * @return The singleton`s instance.
     */
    @NotNull
    public static ActionHandler getInstance()
    {
        if (instance == null) {
            instance = new ActionHandler();
        }
        return instance;
    }

    public static void destroy()
    {
        if (instance == null) {
            throw new AssertionError("Singleton not yet initialised.");
        }
        instance = null;
    }

    /* INSTANCE MEMBERS ------------------------------------------------------------------- */

    /**
     * The current game state. You should write into data only in actions
     * and than use the data giving as parameters. The data is not private,
     * only because the Log may change it to a later version.
     */
    public GameState state;
    /** The last user action. */
    public GCAction lastUserAction = null;
    /**
     * This may be set only in actions. If true, lastUserAction will be set to
     * null, even if the current action is an User action.
     */
    public boolean noLastUserAction = false;

    public final Event<GameState> gameStateUpdated = new Event<GameState>();

    private ActionHandler() {}
    
    /**
     * Enqueues the specified action to be performed against the global game state object.
     * <p>
     * Actions will be run on the UI thread.
     * 
     * @param action the action to execute.
     */
    public void register(@NotNull final GCAction action)
    {
        // Ensure we are running on the GUI thread
        if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    register(action);
                }
            });
            return;
        }

        if (action.isLegal(state)) {

            action.perform(state);

            if (action.type == ActionTrigger.User) {
                lastUserAction = action;
            }

            if (noLastUserAction) {
                noLastUserAction = false;
                lastUserAction = null;
            }

            gameStateUpdated.fire(state);
        }
    }
}