package data;

import java.io.Serializable;


/**
 * Models the state of a team at a given moment.
 *
 * This class's representation is independent of any particular network protocol, though in
 * practice there are many similarities.
 *
 * @author Michel Bartsch
 * @author Drew Noakes https://drewnoakes.com
 */
public class TeamState implements Serializable
{
    /**
     * The number of player entries in a game state message.
     * This may be more than the number of players allowed by the rules.
     */
    public static final byte NUM_PLAYERS_IN_GAME_STATE_MESSAGE = 11;

    // TODO can we remove duplicated state (team num / color) here?

    /** This team's uniquely identifying number. */
    public int teamNumber;
    /** This team's uniform colour. */
    public TeamColor teamColor;
    /** The team's current score. */
    public byte score;
    /** The number of penalty shots this team has taken. */
    public byte penaltyShot = 0;
    /** Bit flag, indicating success of penalty shots so far. */
    public short singleShots = 0;
    /** The last coach message (only used in SPL). */
    public byte[] coachMessage = new byte[SPLCoachMessage.SPL_COACH_MESSAGE_SIZE];
    /** Data about the team's coach (only used in SPL). */
    public final PlayerState coach = new PlayerState();
    /** Data about the players in this team. */
    public final PlayerState[] player = new PlayerState[NUM_PLAYERS_IN_GAME_STATE_MESSAGE];

    public TeamState(TeamColor teamColor)
    {
        this.teamColor = teamColor;

        for (int i=0; i<player.length; i++) {
            player[i] = new PlayerState();
        }
    }

    @Override
    public String toString()
    {
        return "--------------------------------------\n"
             + "         teamNumber: " + teamNumber + '\n'
             + "          teamColor: " + teamColor + '\n'
             + "              score: " + score + '\n'
             + "        penaltyShot: " + penaltyShot + '\n'
             + "        singleShots: " + Integer.toBinaryString(singleShots) + '\n'
             + "       coachMessage: " + new String(coachMessage) + '\n'
             + "        coachStatus: " + coach + '\n';
    }
}
