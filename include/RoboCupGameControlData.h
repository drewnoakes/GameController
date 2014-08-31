#ifndef ROBOCUPGAMECONTROLDATA_H
#define ROBOCUPGAMECONTROLDATA_H

// NOTE for Humanoid League, remove this define
#define SPL_LEAGUE

#ifdef SPL_LEAGUE
#include "SPLCoachMessage.h"
#endif

#define GAMECONTROLLER_PORT            3838

#define GAMECONTROLLER_STRUCT_HEADER   "RGme"
#define GAMECONTROLLER_STRUCT_VERSION  9

#define MAX_NUM_PLAYERS             11

#define TEAM_BLUE                   0
#define TEAM_CYAN                   0
#define TEAM_RED                    1
#define TEAM_MAGENTA                1
#define DROPBALL                    2

#define PLAY_MODE_INITIAL           0
#define PLAY_MODE_READY             1
#define PLAY_MODE_SET               2
#define PLAY_MODE_PLAYING           3
#define PLAY_MODE_FINISHED          4

#define PERIOD_NORMAL               0
#define PERIOD_PENALTYSHOOT         1
#define PERIOD_OVERTIME             2
#define PERIOD_TIMEOUT              3

#define LEAGUE_SPL                  1
#define LEAGUE_SPL_DROP_IN          2
#define LEAGUE_HL_KID               17
#define LEAGUE_HL_TEEN              18
#define LEAGUE_HL_ADULT             19

#define PENALTY_NONE                        0

#ifdef SPL_LEAGUE
// SPL
#define PENALTY_SPL_BALL_HOLDING            1
#define PENALTY_SPL_PLAYER_PUSHING          2
#define PENALTY_SPL_OBSTRUCTION             3
#define PENALTY_SPL_INACTIVE_PLAYER         4
#define PENALTY_SPL_ILLEGAL_DEFENDER        5
#define PENALTY_SPL_LEAVING_THE_FIELD       6
#define PENALTY_SPL_PLAYING_WITH_HANDS      7
#define PENALTY_SPL_REQUEST_FOR_PICKUP      8
#define PENALTY_SPL_COACH_MOTION            9
#else
// HL Kid Size
#define PENALTY_HL_KID_BALL_MANIPULATION    1
#define PENALTY_HL_KID_PHYSICAL_CONTACT     2
#define PENALTY_HL_KID_ILLEGAL_ATTACK       3
#define PENALTY_HL_KID_ILLEGAL_DEFENSE      4
#define PENALTY_HL_KID_REQUEST_FOR_PICKUP   5
#define PENALTY_HL_KID_REQUEST_FOR_SERVICE  6
// HL Teen Size
#define PENALTY_HL_TEEN_BALL_MANIPULATION   1
#define PENALTY_HL_TEEN_PHYSICAL_CONTACT    2
#define PENALTY_HL_TEEN_ILLEGAL_ATTACK      3
#define PENALTY_HL_TEEN_ILLEGAL_DEFENSE     4
#define PENALTY_HL_TEEN_REQUEST_FOR_PICKUP  5
#define PENALTY_HL_TEEN_REQUEST_FOR_SERVICE 6
#endif

#define PENALTY_SUBSTITUTE                  14
#define PENALTY_MANUAL                      15

struct RobotInfo
{
  uint8_t penalty;              // Penalty state of the player
  uint8_t secsTillUnpenalised;  // Estimate of time till unpenalised
};

struct TeamInfo
{
  uint8_t teamNumber;           // Unique team number
  uint8_t teamColour;           // Colour of the team (TEAM_BLUE/TEAM_RED, or equivalently TEAM_CYAN/TEAM_MAGENTA)
  uint8_t score;                // Team's score
  uint8_t penaltyShot;          // Penalty shot counter
  uint16_t singleShots;         // Bits represent penalty shot success
#ifdef SPL_LEAGUE
  uint8_t coachMessage[SPL_COACH_MESSAGE_SIZE]; // the coach's message to the team
  RobotInfo coach;
#endif
  RobotInfo players[MAX_NUM_PLAYERS]; // the team's players
};

struct RoboCupGameControlData
{
  char header[4];               // Header to identify the structure
  uint8_t version;              // Version of the data structure
  uint8_t leagueNumber;         // Identifies the league being played in (LEAGUE_SPL, LEAGUE_HL_KID, etc)
  uint8_t packetNumber;         // Number incremented with each packet sent (with wraparound)
  uint32_t gameControllerId;    // A randomly chosen number that is consistent throughout the lifespan of a game
  uint8_t playersPerTeam;       // The maximum number of players on each team, including substitutes
  uint8_t playMode;             // The play mode of the game (PLAY_MODE_READY, PLAY_MODE_PLAYING, etc)
  uint8_t firstHalf;            // '1' if game in first half, '0' otherwise
  uint8_t kickOffTeam;          // The next team to kick off (0=TEAM_BLUE, 1=TEAM_RED, 2=DROP_BALL)
  uint8_t period;               // Extra state information (PERIOD_NORMAL, PERIOD_PENALTYSHOOT, etc)
  uint8_t dropInTeam;           // Team that caused last drop in (0=TEAM_BLUE, 1=TEAM_RED, 2=NONE)
  uint8_t isKnockOutGame;       // Whether the game is a knockout/playoff (1) or not (0)
  uint16_t dropInTime;          // Number of seconds passed since the last drop in (-1 before first drop in)
  uint16_t secsRemaining;       // An estimate of the number of seconds remaining in the half
  uint16_t secondaryTime;       // Number of seconds shown as secondary time (remaining ready, until free ball, etc)
  TeamInfo teams[2];
};

// data structure header
#define GAMECONTROLLER_RETURN_STRUCT_HEADER      "RGrt"
#define GAMECONTROLLER_RETURN_STRUCT_VERSION     2

#define GAMECONTROLLER_RETURN_MSG_MAN_PENALISE   0
#define GAMECONTROLLER_RETURN_MSG_MAN_UNPENALISE 1
#define GAMECONTROLLER_RETURN_MSG_ALIVE          2

struct RoboCupGameControlReturnData
{
  char header[4];
  uint8_t version;              // Version of the data structure
  uint8_t team;                 // Team number
  uint8_t player;               // Player number (starts with 1)
  uint8_t message;              // One of the three message types (GAMECONTROLLER_RETURN_MSG_ALIVE, etc)

#ifdef __cplusplus
  // constructor
  RoboCupGameControlReturnData()
  {
    *(uint32_t*) header = *(const uint32_t*) GAMECONTROLLER_RETURN_STRUCT_HEADER;
    version = GAMECONTROLLER_RETURN_STRUCT_VERSION;
  }
#endif
};

#endif // ROBOCUPGAMECONTROLDATA_H
