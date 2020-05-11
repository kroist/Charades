package main.java.org.Server;

import java.net.Socket;

public class Player {
    private Server.ConnectionThread conn;
    private boolean isDrawing;
    private Game game;
    private String username;
    private int score;
    public Player(Server.ConnectionThread conn, Game game, String username){
        this.conn = conn;
        this.game = game;
        this.username = username;
        this.score = 0;
    }
    Server.ConnectionThread getConn(){
        return conn;
    }
    public void setIsDrawing(boolean a){
        isDrawing = a;
    }
    public boolean isDrawing(){
        return isDrawing;
    }
    public Game getGame(){
        return game;
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
}
