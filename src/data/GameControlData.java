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
    /** Some constants from the C-structure. */
    public static final int GAMECONTROLLER_RETURNDATA_PORT = 3838; // port to receive return-packets on
    public static final int GAMECONTROLLER_GAMEDATA_PORT= 3838; // port to send game state packets to

    public static final String GAMECONTROLLER_STRUCT_HEADER = "RGme";
    public static final byte GAMECONTROLLER_STRUCT_VERSION = 8;

    public static final byte TEAM_BLUE = 0;
    public static final byte TEAM_RED = 1;
    public static final byte DROPBALL = 2;

    public static final byte STATE_INITIAL = 0;
    public static final byte STATE_READY = 1;
    public static final byte STATE_SET = 2;
    public static final byte STATE_PLAYING = 3;
    public static final byte STATE_FINISHED = 4;

    public static final byte STATE2_NORMAL = 0;
    public static final byte STATE2_PENALTYSHOOT = 1;
    public static final byte STATE2_OVERTIME = 2;
    public static final byte STATE2_TIMEOUT = 3;             
    
    public static final byte C_FALSE = 0;
    public static final byte C_TRUE = 1;

    public byte playersPerTeam = (byte)Rules.league.teamSize;   // The number of players on a team
    public byte gameState = STATE_INITIAL;                      // state of the game (STATE_READY, STATE_PLAYING, etc)
    public byte firstHalf = C_TRUE;                             // 1 = game in first half, 0 otherwise
    public byte kickOffTeam = TEAM_BLUE;                        // the next team to kick off
    public byte secGameState = STATE2_NORMAL;                   // Extra state information - (STATE2_NORMAL, STATE2_PENALTYSHOOT, etc)
    public byte dropInTeam;                                     // team that caused last drop in
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
        team[0].teamColor = TEAM_BLUE;
        team[1].teamColor = TEAM_RED;
    }
    
    @Override
    public String toString()
    {
        String out = "";
        String temp;
        
        out += "             Header: "+GAMECONTROLLER_STRUCT_HEADER+"\n";
        out += "            Version: "+GAMECONTROLLER_STRUCT_VERSION+"\n";
        out += "   Players per Team: "+playersPerTeam+"\n";
        switch (gameState) {
            case STATE_INITIAL:  temp = "initial"; break;
            case STATE_READY:    temp = "ready";   break;
            case STATE_SET:      temp = "set";     break;
            case STATE_PLAYING:  temp = "playing"; break;
            case STATE_FINISHED: temp = "finish";  break;
            default: temp = "undefined("+gameState+")";
        }
        out += "          gameState: "+temp+"\n";
        switch (firstHalf) {
            case C_TRUE:  temp = "true";  break;
            case C_FALSE: temp = "false"; break;
            default: temp = "undefined("+firstHalf+")";
        }
        out += "          firstHalf: "+temp+"\n";
        switch (kickOffTeam) {
            case TEAM_BLUE: temp = "blue"; break;
            case TEAM_RED:  temp = "red";  break;
            default: temp = "undefined("+kickOffTeam+")";
        }
        out += "        kickOffTeam: "+temp+"\n";
        switch (secGameState) {
            case STATE2_NORMAL:       temp = "normal"; break;
            case STATE2_PENALTYSHOOT: temp = "penaltyshoot";  break;
            case STATE2_OVERTIME:     temp = "overtime";  break;
            case STATE2_TIMEOUT:     temp = "timeout";  break;
            default: temp = "undefined("+secGameState+")";
        }
        out += "       secGameState: "+temp+"\n";
        switch (dropInTeam) {
            case TEAM_BLUE: temp = "blue"; break;
            case TEAM_RED:  temp = "red";  break;
            default: temp = "undefined("+dropInTeam+")";
        }
        out += "         dropInTeam: "+temp+"\n";
        out += "         dropInTime: "+dropInTime+"\n";
        out += "      secsRemaining: "+secsRemaining+"\n";
        out += "      secondaryTime: "+secondaryTime+"\n";
        return out;
    }
}