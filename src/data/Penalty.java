package data;

import rules.Rules;
import rules.SPL;

/**
 * Enum of penalty states.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public enum Penalty
{
    None(0, -1, "None"),

    SplBallHolding(1, 45, "Ball Holding"),
    SplPlayerPushing(2, 45, "Player Pushing"),
    SplObstruction(3, 45, "Obstruction"),
    SplInactivePlayer(4, 45, "Inactive Player"),
    SplIllegalDefender(5, 45, "Illegal Defender"),
    SplLeavingTheField(6, 45, "Leaving The Field"),
    SplPlayingWithHands(7, 45, "Playing With Hands"),
    SplRequestForPickup(8, 45, "Request For Pickup"),
    SplCoachMotion(9, 2*10*60, "Coach Motion"),

    HLBallManipulation(1, 30, "Ball Manipulation"),
    HLPhysicalContact(2, 30, "Physical Contact"),
    HLIllegalAttack(3, 30, "Illegal Attack"),
    HLIllegalDefense(4, 30, "Illegal Defense"),
    HLPickupOrIncapable(5, 30, "Pickup or Incapable"),
    Service(6, 60, "Service"),

    Substitute(14, -1, "Substitute"),
    Manual(15, -1, "Manual");

    private final byte value;
    private final byte durationSeconds;
    private final String title;

    Penalty(int value, int durationSeconds, String title)
    {
        this.value = (byte)value;
        this.durationSeconds = (byte)durationSeconds;
        this.title = title;
    }

    /** Get the numeric value used in network messages for this penalty. */
    public byte getValue()
    {
        return value;
    }

    public byte getDurationSeconds()
    {
        return durationSeconds;
    }

    @Override
    public String toString()
    {
        return title;
    }

    /** Decode a numeric value from a network message. */
    public static Penalty fromValue(byte value)
    {
        if (Rules.league instanceof SPL) {
            switch (value) {
                case 0: return None;
                case 1: return SplBallHolding;
                case 2: return SplPlayerPushing;
                case 3: return SplObstruction;
                case 4: return SplInactivePlayer;
                case 5: return SplIllegalDefender;
                case 6: return SplLeavingTheField;
                case 7: return SplPlayingWithHands;
                case 8: return SplRequestForPickup;
                case 9: return SplCoachMotion;
                case 14: return Substitute;
                case 15: return Manual;
                default:
                    throw new AssertionError("Invalid Penalty enum value: " + value);
            }
        } else {
            switch (value) {
                case 0: return None;
                case 1: return HLBallManipulation;
                case 2: return HLPhysicalContact;
                case 3: return HLIllegalAttack;
                case 4: return HLIllegalDefense;
                case 5: return HLPickupOrIncapable;
                case 6: return Service;
                case 14: return Substitute;
                case 15: return Manual;
                default:
                    throw new AssertionError("Invalid Penalty enum value: " + value);
            }
        }
    }
}
