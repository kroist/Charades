package main.java.org.Tools;

public enum ConnectionMessage {
    GAME_ENDED, GAME_STARTED,
    CONNECT_TO_LOBBY, CREATE_NEW_LOBBY, CONNECTED_TO_LOBBY,
    BAD_ID {
        @Override
        public String toString(){
            return "You entered bad ID!";
        }
    },
    CONNECTED, GAME_ALREADY_STARTED, MAX_NUM_LOBBY, START_GAME, LOGGED_IN,
    BROWSE_GAMES, RETURN_TO_MENU, NEW_DRAWER, STOP_READING
}