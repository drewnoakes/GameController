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
    /** The last actions as the name says. */
    private GCAction lastNonClockEvent = null;
    public GCAction lastUIEvent = null;
    private GCAction lastNetEvent = null;
    /**
     * This may be set only in actions. If true, lastUIEvent will be set to
     * null, even if the current action is an UIEvent.
     */
    public boolean noLastUIEvent = false;

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
     * @param event     The action calling.
     */
    public void register(final GCAction event)
    {
        // Ensure we are running on the GUI thread
        if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    register(event);
                }
            });
            return;
        }

        if (event.isLegal(data)) {
            event.perform(data);
            update(event);
        }
    }
    
    /**
     * After the perform method this updates some attributes, calls the GUI`s
     * update method and changes the data to be send.
     * 
     * @param event     The action that has been called.
     */
    private void update(GCAction event)
    {
        if (event.type != ActionType.CLOCK) {
            lastNonClockEvent = event;
            if (event.type == ActionType.UI) {
                lastUIEvent = event;
            } else if (event.type == ActionType.NET) {
                lastNetEvent = event;
            }
        }
        if (noLastUIEvent) {
            noLastUIEvent = false;
            lastUIEvent = null;
        }
        gameStateSender.send(data);
        gui.update(data);
    }
}