package controller;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.action.ActionTrigger;
import controller.action.GCAction;
import controller.net.GameStateSender;
import controller.ui.GCGUI;
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
public class EventHandler
{
    /* SINGLETON MEMBERS ------------------------------------------------------------------- */

    /** The instance of the singleton. */
    @Nullable
    private static EventHandler instance;

    public static void initialise(@NotNull GameStateSender gameStateSender)
    {
        if (instance != null) {
            throw new AssertionError("Singleton has already been initialised initialised.");
        }
        instance = new EventHandler(gameStateSender);
    }

    /**
     * To get the singleton instance for public attribute access.
     *
     * @return The singleton`s instance.
     */
    @NotNull
    public static EventHandler getInstance()
    {
        if (instance == null) {
            throw new AssertionError("Singleton must be initialised.");
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

    /** This GUI`s update method will be called. */
    private GCGUI gui;
    /** The sender has a send method to update the data to send */
    private final GameStateSender gameStateSender;
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

    /**
     * Initialises an EventHandler.
     */
    private EventHandler(@NotNull GameStateSender gameStateSender)
    {
        this.gameStateSender = gameStateSender;
    }
    
    /**
     * Sets the GUI.
     * 
     * @param gui   The GUI to be updated when the  changes.
     */
    public void setGUI(@NotNull GCGUI gui)
    {
        this.gui = gui;
    }
    
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
            update(action);
        }
    }
    
    /**
     * After the perform method this updates some attributes, calls the GUI`s
     * update method and changes the data to be send.
     * 
     * @param action the action that has been called.
     */
    private void update(@NotNull GCAction action)
    {
        if (action.type != ActionTrigger.Clock && action.type == ActionTrigger.User) {
            lastUserAction = action;
        }

        if (noLastUserAction) {
            noLastUserAction = false;
            lastUserAction = null;
        }
        gameStateSender.send(state);
        gui.update(state);
    }
}