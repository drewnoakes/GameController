package controller;

import common.annotations.NotNull;

/**
 * Action classes mutate the game state in some way.
 * <p>
 * Subclasses override {@link Action#execute(Game, WriteableGameState)} in order to perform
 * whatever change to the game state they require.
 * <p>
 * To invoke an action, call {@link Game#apply(Action, controller.action.ActionTrigger)}.
 * <p>
 * Before an action will be executed, {@link Action#canExecute} will be called and must
 * return <code>true</code>. The default implementation always returns true, but
 * subclasses can override this method to enforce preconditions on the game state.
 *
 * @author Michel Bartsch
 * @author Drew Noakes https://drewnoakes.com
 */
public abstract class Action
{
    /**
     * Executes the action on the specified {@link WriteableGameState}.
     *
     * @param game the game to operate on.
     * @param state the game state to operate on.
     */
    protected abstract void execute(@NotNull Game game, @NotNull WriteableGameState state);

    /**
     * Specifies if this action is legal at a specific state of the game.
     *
     * By default, actions are legal unless this method is overridden.
     *
     * @param game the game to operate on.
     * @param state the current data to calculate the legality by.
     * @return <code>true</code> if the action may be performed, otherwise <code>false</code>.
     */
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return true;
    }
}