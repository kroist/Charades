package main.java.org;

import java.net.Socket;

public class Player {
    private Server.ConnectionThread conn;
    private boolean isDrawing;
    private Game game;
    public Player(Server.ConnectionThread conn, Game game){
        this.conn = conn;
        this.game = game;
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
    @Override
    public boolean equals(Object obj) {
        if (obj == null)return false;
        if (!(obj instanceof Player))return false;
        return conn.equals(((Player) obj).conn);
    }
}
