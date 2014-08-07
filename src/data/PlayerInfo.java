package data;

import rules.Rules;
import rules.SPL;

import java.io.Serializable;


/**
 * Models the state of a player at a given moment.
 *
 * This class's representation is independent of any particular network protocol, though in
 * practice there are many similarities.
 *
 * @author Michel Bartsch
 * @author Drew Noakes https://drewnoakes.com
 */
public class PlayerInfo implements Serializable
{
    /** What type of penalty a player may have. */
    public static final byte PENALTY_NONE = 0;
    
    public static final byte PENALTY_SPL_BALL_HOLDING = 1;
    public static final byte PENALTY_SPL_PLAYER_PUSHING = 2;
    public static final byte PENALTY_SPL_OBSTRUCTION = 3;
    public static final byte PENALTY_SPL_INACTIVE_PLAYER = 4;
    public static final byte PENALTY_SPL_ILLEGAL_DEFENDER = 5;
    public static final byte PENALTY_SPL_LEAVING_THE_FIELD = 6;
    public static final byte PENALTY_SPL_PLAYING_WITH_HANDS = 7;
    public static final byte PENALTY_SPL_REQUEST_FOR_PICKUP = 8;
    public static final byte PENALTY_SPL_COACH_MOTION = 9;
    
    public static final byte PENALTY_HL_BALL_MANIPULATION = 1;
    public static final byte PENALTY_HL_PHYSICAL_CONTACT = 2;
    public static final byte PENALTY_HL_ILLEGAL_ATTACK = 3;
    public static final byte PENALTY_HL_ILLEGAL_DEFENSE = 4;
    public static final byte PENALTY_HL_PICKUP_OR_INCAPABLE = 5;
    public static final byte PENALTY_HL_SERVICE = 6;

    public static final byte PENALTY_SUBSTITUTE = 14;
    public static final byte PENALTY_MANUAL = 15;

    public byte penalty = PENALTY_NONE; // penalty state of the player
    public byte secsTillUnpenalised;    // estimate of time till unpenalised

    @Override
    public String toString()
    {
        String out = "----------------------------------------\n";
        String temp;
        
        if (Rules.league instanceof SPL) {
            switch (penalty) {
                case PENALTY_NONE:                   temp = "none"; break;
                case PENALTY_SPL_BALL_HOLDING:       temp = "ball holding"; break;
                case PENALTY_SPL_PLAYER_PUSHING:     temp = "pushing"; break;
                case PENALTY_SPL_OBSTRUCTION:        temp = "fallen robot"; break;
                case PENALTY_SPL_INACTIVE_PLAYER:    temp = "inactive"; break;
                case PENALTY_SPL_ILLEGAL_DEFENDER:   temp = "illegal defender"; break;
                case PENALTY_SPL_LEAVING_THE_FIELD:  temp = "leaving the field"; break;
                case PENALTY_SPL_PLAYING_WITH_HANDS: temp = "hands"; break;
                case PENALTY_SPL_REQUEST_FOR_PICKUP: temp = "request for pickup"; break;
                case PENALTY_SPL_COACH_MOTION:       temp = "coach motion"; break;
                case PENALTY_SUBSTITUTE:             temp = "substitute"; break;
                case PENALTY_MANUAL:                 temp = "manual"; break;
                default: temp = "undefined("+penalty+")";
            }
        } else {
            switch (penalty) {
                case PENALTY_NONE:
                case PENALTY_HL_BALL_MANIPULATION:   temp = "none"; break;
                case PENALTY_HL_PHYSICAL_CONTACT:    temp = "pushing"; break;
                case PENALTY_HL_ILLEGAL_ATTACK:      temp = "illegal attack"; break;
                case PENALTY_HL_ILLEGAL_DEFENSE:     temp = "illegal defender"; break;
                case PENALTY_HL_PICKUP_OR_INCAPABLE: temp = "pickup/incapable"; break;
                case PENALTY_HL_SERVICE:             temp = "service"; break;
                case PENALTY_MANUAL:                 temp = "manual"; break;
                case PENALTY_SUBSTITUTE:             temp = "substitute"; break;
                default: temp = "undefined("+penalty+")";
            }
        }
        out += "            penalty: "+temp+"\n";
        out += "secsTillUnpenalised: "+secsTillUnpenalised+"\n";
        return out;
    }
}