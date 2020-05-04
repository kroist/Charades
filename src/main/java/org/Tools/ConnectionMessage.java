package main.java.org.Tools;

public enum ConnectionMessage {
    GAME_ENDED, GAME_STARTED, CONN_TO_GAME, CREATE_NEW_GAME,
    BAD_ID {
        @Override
        public String toString(){
            return "You entered bad ID!";
        }
    },
    CONNECTED, GAME_ALREADY_STARTED, MAX_NUM_LOBBY, START_GAME, LOGGED_IN
}