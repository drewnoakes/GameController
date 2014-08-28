package controller.net.protocol;

import common.annotations.NotNull;
import common.annotations.Nullable;
import data.*;

import java.nio.ByteBuffer;

/**
 * Implements game state network protocol, version 9.
 *
 * <ul>
 *     <li>
 *         Adds field 'game controller ID' to the message, which can be used to prevent
 *         against problems seen when multiple game controllers are running.
 *     </li>
 *     <li>
 *         Adds a byte indicating the league being played in. Allows teams that play in
 *         multiple leagues to behave accordingly (eg. both HL kid-size and teen-size).
 *     </li>
 *     <li>
 *         When no drop in has yet occurred, 'lastDropInColor' has value 2 instead of 0.
 *     </li>
 * </ul>
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class GameStateProtocol9 extends GameStateProtocol
{
    /**
     * A unique number for this game controller instance.
     *
     * Can be used by robots to defend against problems seen when multiple game controllers
     * are running.
     */
    private final int gameControllerId;

    private final League league;

    public GameStateProtocol9(@NotNull League league, int gameControllerId)
    {
        super((byte) 9);
        this.league = league;
        this.gameControllerId = gameControllerId;
    }

    @Override
    public int getMessageSize()
    {
        final int playerSize =
                1 + // penalty
                1;  // secsToUnpenalize

        final int teamSize =
                1 + // teamNumber
                1 + // teamColor
                1 + // score
                1 + // penaltyShot
                2 + // singleShots
                SPLCoachMessage.SPL_COACH_MESSAGE_SIZE + // coach's message
                (TeamState.NUM_PLAYERS_IN_GAME_STATE_MESSAGE + 1) * playerSize; // +1 for the coach

        return  4 + // header
                1 + // version
                1 + // packet number
                4 + // game controller ID
                1 + // league identifier
                1 + // numPlayers
                1 + // playMode
                1 + // firstHalf
                1 + // nextKickOffColor
                1 + // period
                1 + // lastDropInColor
                2 + // dropInTime
                2 + // secsRemaining
                2 + // secondaryTime
                2 * teamSize;
    }

    @NotNull
    @Override
    public byte[] toBytes(@NotNull GameStateSnapshot state)
    {
        ByteBuffer buffer = writeHeader();

        buffer.put(getVersionNumber());
        buffer.put(nextPacketNumber);
        buffer.putInt(gameControllerId);
        buffer.put(league.number());
        buffer.put((byte)league.settings().teamSize);
        buffer.put(state.playMode.getValue());
        buffer.put(state.firstHalf ? (byte)1 : 0);
        buffer.put(state.nextKickOffColor == null ? 2 : state.nextKickOffColor.getValue());
        buffer.put(state.period.getValue());
        buffer.put(state.lastDropInColor == null ? 2 : state.lastDropInColor.getValue());
        buffer.putShort(state.dropInTime);
        buffer.putShort(state.secsRemaining);
        buffer.putShort(state.secondaryTime);

        for (TeamState team : state.teams) {
            writeTeamInfo(buffer, team);
        }

        return buffer.array();
    }

    @Nullable
    @Override
    public GameStateSnapshot fromBytes(@NotNull ByteBuffer buffer)
    {
        if (!verifyHeader(buffer))
            return null;

        GameStateSnapshot data = new GameStateSnapshot(this.league.settings());

        buffer.get(); // packet number (ignored when decoding)

        data.gameControllerId = buffer.getInt();

        // Ensure the message applies to the current league
        // TODO don't return null, and decode the message according to the advertised league
        byte leagueNumber = buffer.get();
        if (leagueNumber != this.league.number())
            return null;

        buffer.get(); // players per team (ignored when decoding)

        data.playMode = PlayMode.fromValue(buffer.get());
        data.firstHalf = buffer.get() != 0;
        data.nextKickOffColor = TeamColor.fromValue(buffer.get());
        data.period = Period.fromValue(buffer.get());
        data.lastDropInColor = TeamColor.fromValue(buffer.get());
        data.dropInTime = buffer.getShort();
        data.secsRemaining = buffer.getShort();
        data.secondaryTime = buffer.getShort();

        for (TeamState t : data.teams) {
            t.teamNumber = buffer.get();
            t.teamColor = TeamColor.fromValue(buffer.get());
            t.score = buffer.get();
            t.penaltyShot = buffer.get();
            t.singleShots = buffer.getShort();
            buffer.get(t.coachMessage);
            t.coach.penalty = Penalty.fromValue(league, buffer.get());
            t.coach.secsTillUnpenalised = buffer.get();
            for (PlayerState p : t.player) {
                p.penalty = Penalty.fromValue(league, buffer.get());
                p.secsTillUnpenalised = buffer.get();
            }
        }

        return data;
    }

    private static void writeTeamInfo(@NotNull ByteBuffer buffer, @NotNull TeamState teamState)
    {
        buffer.put((byte)teamState.teamNumber);
        buffer.put(teamState.teamColor.getValue());
        buffer.put(teamState.score);
        buffer.put(teamState.penaltyShot);
        buffer.putShort(teamState.singleShots);
        buffer.put(teamState.coachMessage);

        writePlayerInfo(buffer, teamState.coach);

        for (int i=0; i< TeamState.NUM_PLAYERS_IN_GAME_STATE_MESSAGE; i++) {
            writePlayerInfo(buffer, teamState.player[i]);
        }
    }

    private static void writePlayerInfo(@NotNull ByteBuffer buffer, @NotNull PlayerState playerState)
    {
        buffer.put(playerState.penalty.getValue());
        buffer.put(playerState.secsTillUnpenalised);
    }
}
