package controller;

import controller.action.ActionType;
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
    private static EventHandler instance;

    public static void initialise(GameStateSender gameStateSender)
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
    public synchronized static EventHandler getInstance()
    {
        if (instance == null) {
            throw new AssertionError("Singleton must be initialised.");
        }
        return instance;
    }

    /* INSTANCE MEMBERS ------------------------------------------------------------------- */

    /** This GUI`s update method will be called. */
    private GCGUI gui;
    /** The sender has a send method to update the data to send */
    private final GameStateSender gameStateSender;
    /**
     * This is the current data. You should write into data only in actions
     * and than use the data giving as parameters. The data is not private,
     * only because the Log may change it to a later version.
     */
    public GameState data;
    /** The last UI action. */
    public GCAction lastUIAction = null;
    /**
     * This may be set only in actions. If true, lastUIAction will be set to
     * null, even if the current action is an UI action.
     */
    public boolean noLastUIAction = false;

    /**
     * Creates a new EventHandler.
     */
    private EventHandler(GameStateSender gameStateSender)
    {
        this.gameStateSender = gameStateSender;
    }
    
    /**
     * Sets the GUI.
     * 
     * @param gui   The GUI to be updated when the  changes.
     */
    public void setGUI(GCGUI gui)
    {
        this.gui = gui;
    }
    
    /**
     * Very important method called automatically by every action in its
     * @{link GCAction#actionPerformed} method to later call its
     * @{link GCAction#perform} method in the GUI-Thread.
     * 
     * @param action the action calling.
     */
    public void register(final GCAction action)
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

        if (action.isLegal(data)) {
            action.perform(data);
            update(action);
        }
    }
    
    /**
     * After the perform method this updates some attributes, calls the GUI`s
     * update method and changes the data to be send.
     * 
     * @param action the action that has been called.
     */
    private void update(GCAction action)
    {
        if (action.type != ActionType.CLOCK && action.type == ActionType.UI) {
            lastUIAction = action;
        }

        if (noLastUIAction) {
            noLastUIAction = false;
            lastUIAction = null;
        }
        gameStateSender.send(data);
        gui.update(data);
    }
}