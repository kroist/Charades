package main.java.org.Server;

public class Player {
    private Server.ConnectionThread conn;
    private Lobby lobby;
    private String username;
    private boolean inGame;
    private int score;
    public Player(Server.ConnectionThread conn, String username){
        this.conn = conn;
        this.username = username;
        this.score = 0;
    }


    Server.ConnectionThread getConn(){
        return conn;
    }
    public String getUsername(){
        return username;
    }
    public int getScore(){
        return score;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null)return false;
        if (!(obj instanceof Player))return false;
        return conn.equals(((Player) obj).conn);
    }

    public boolean inLobby() {
        return lobby != null;
    }

    public boolean inGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    public void reset() {
        if (inLobby()){
            lobby.removePlayer(this);
            if (lobby.empty()){
                Server.lobbyIDs.remove(lobby.getID());
            }
        }
        inGame = false;
        lobby = null;
        score = 0;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return username;
    }
}
