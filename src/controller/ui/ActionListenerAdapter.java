package controller.ui;

import controller.action.GCAction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActionListenerAdapter implements ActionListener
{
    private final GCAction action;

    public ActionListenerAdapter(GCAction action)
    {
        this.action = action;
    }

    /**
     * This gets called when the button an action is added to was pushed or
     * if the action is called otherwise.
     *
     * The action`s perform method will not be executed right away but
     * later in the GUI`s thread.
     *
     * @param e the event that happened, but this is ignored.
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        this.action.invoke();
    }
}
