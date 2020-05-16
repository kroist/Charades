package main.java.org.Tools;

public enum ConnectionMessage {
    GAME_ENDED, GAME_STARTED,
    CONNECT_TO_LOBBY, CONNECTED_TO_LOBBY, LOBBY_LIST, LOBBY_FULL,
    BAD_ID {
        @Override
        public String toString(){
            return "You entered bad ID!";
        }
    },
    CONNECTED, GAME_ALREADY_STARTED, MAX_NUM_LOBBY, START_GAME, LOGGED_IN,
    RETURN_TO_MENU, NEW_DRAWER, STOP_READING, BUSY_NICKNAME
}