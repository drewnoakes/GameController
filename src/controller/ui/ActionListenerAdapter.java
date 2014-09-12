package controller.ui;

import controller.Action;
import controller.Game;
import controller.action.ActionTrigger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActionListenerAdapter implements ActionListener
{
    private final Game game;
    private final Action action;

    public ActionListenerAdapter(Game game, Action action)
    {
        this.game = game;
        this.action = action;
    }

    /**
     * This gets called when the button an action is added to was pushed or
     * if the action is called otherwise.
     *
     * The action's execute method will not be executed right away but
     * later in the GUI thread.
     *
     * @param e the event that happened, but this is ignored.
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        game.apply(action, ActionTrigger.User);
    }
}
