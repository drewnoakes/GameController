package controller.ui;

import data.GameState;

/**
 * This interface is for a GUI.
 *
 * Every time the model changes the update method will be called with the new
 * model as data.
 *
 * In additional a GUI can get the last action that caused the change from
 * the EventHandler, but this should not be used too often to avoid
 * dependencies.
 *
 * @author Michel Bartsch
 */
public interface GCGUI
{   
    /**
     * Called every time the model has changed, so the GUI can update its view.
     * 
     * @param data  The Model to view.
     */
    public void update(GameState data);
}