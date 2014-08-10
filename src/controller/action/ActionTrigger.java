package controller.action;

/**
 * Enum of the causes for an action being triggered.
 */
public enum ActionTrigger
{
    /** The action was triggered in response to a clock tick. */
    Clock,

    /** The action was triggered in response to a user event. */
    User,

    /** The action was triggered in response to a message received via the network. */
    Network
}