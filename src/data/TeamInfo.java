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
public class TeamInfo implements Serializable
{
    /**
     * How many players a team may have.
     * Actually that many players in each team need to be sent, even if
     * playersPerTeam in GameControlData is less.
     */
    public static final byte MAX_NUM_PLAYERS = 11;

    public byte teamNumber;                                         // unique team number
    public TeamColor teamColor;                                     // colour of the team
    public byte score;                                              // team's score
    public byte penaltyShot = 0;                                    // penalty shot counter
    public short singleShots = 0;                                   // bits represent penalty shot success
    public byte[] coachMessage = new byte[SPLCoachMessage.SPL_COACH_MESSAGE_SIZE];
    public PlayerInfo coach = new PlayerInfo();
    public PlayerInfo[] player = new PlayerInfo[MAX_NUM_PLAYERS];   // the team's players
    
    /**
     * Creates a new TeamInfo.
     */
    public TeamInfo()
    {
        for (int i=0; i<player.length; i++) {
            player[i] = new PlayerInfo();
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
