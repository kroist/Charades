package main.java.org.Server;

import main.java.org.Client.Point;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class Game extends Thread{
    private CopyOnWriteArrayList<Player> players;
    private int gameID;
    private boolean isStarted;
    private Player whoDrawing;
    public Game(int gameID){
        this.gameID = gameID;
        isStarted = false;
        players = new CopyOnWriteArrayList<>();
    }
    public int getGameID(){
        return gameID;
    }
    public boolean isStarted(){
        return isStarted;
    }
    public void addPlayer(Player player){
        players.add(player);
    }
    public void removePlayer(Player player){
        players.remove(player);
    }
    public Player whoDrawing(){
        return whoDrawing;
    }

    @Override
    public void run() {
        try {
            while (players.size() < 2){
                //System.out.println("hahahah " + players.size());
                synchronized (this){
                    wait();
                }
            }
            System.out.println("game started");
            isStarted = true;
            whoDrawing = players.get(0);
            whoDrawing.setIsDrawing(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public synchronized void writeEvent(Object o) throws IOException {
        if (o instanceof Point){
            drawAll((Point)o);
        }
    }
    public synchronized void drawAll(Point p) throws IOException {
            for (Player player : players){
                player.getConn().sendObject(p);
            }
    }
}
