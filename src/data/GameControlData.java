package data;

import rules.Rules;

import java.io.Serializable;


/**
 * Models the state of the game at a given moment.
 *
 * This class's representation is independent of any particular network protocol, though in
 * practice there are many similarities.
 *
 * @author Michel Bartsch
 * @author Drew Noakes https://drewnoakes.com
 */
public class GameControlData implements Serializable
{
    public static final int GAMECONTROLLER_RETURNDATA_PORT = 3838; // port to receive return-packets on
    public static final int GAMECONTROLLER_GAMEDATA_PORT = 3838; // port to send game state packets to

    public byte playersPerTeam = (byte)Rules.league.teamSize;
    public GameState gameState = GameState.Initial;
    public boolean firstHalf = true;
    public TeamColor kickOffTeam = TeamColor.Blue;
    public SecondaryGameState secGameState = SecondaryGameState.Normal; // Extra state information - (STATE2_NORMAL, STATE2_PENALTYSHOOT, etc)
    public TeamColor dropInTeam;                                // team that caused last drop in
    public short dropInTime = -1;                               // number of seconds passed since the last drop in. -1 before first dropin
    public short secsRemaining = (short) Rules.league.halfTime; // estimate of number of seconds remaining in the half
    public short secondaryTime = 0;                             // sub-time (remaining in ready state etc.) in seconds
    public TeamInfo[] team = new TeamInfo[2];
    
    /**
     * Creates a new GameControlData.
     */
    public GameControlData()
    {
        for (int i=0; i<team.length; i++) {
            team[i] = new TeamInfo();
        }
        team[0].teamColor = TeamColor.Blue;
        team[1].teamColor = TeamColor.Red;
    }
    
    @Override
    public String toString()
    {
        return "     playersPerTeam: " + playersPerTeam + '\n' +
               "          gameState: " + gameState + '\n' +
               "          firstHalf: " + (firstHalf ? "true" : "false") + '\n' +
               "        kickOffTeam: " + kickOffTeam + '\n' +
               "       secGameState: " + secGameState + '\n' +
               "         dropInTeam: " + dropInTeam + '\n' +
               "         dropInTime: " + dropInTime + '\n' +
               "      secsRemaining: " + secsRemaining + '\n' +
               "      secondaryTime: " + secondaryTime + '\n';
    }
}
