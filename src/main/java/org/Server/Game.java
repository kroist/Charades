package main.java.org.Server;

import main.java.org.Tools.Point;

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
            /*while (players.size() < 2){
                //System.out.println("hahahah " + players.size());
                synchronized (this){
                    wait();
                }
            }*/
            synchronized (this){
                wait();
            }
            for (Player player : players){
                player.getConn().sendObject("game started");
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
        if (!isStarted)return;
        if (o instanceof Point){
            sendAll(o);
        }
    }
    public synchronized void sendAll(Object o)  {
        for (Player player : players){
            //System.out.println("sent to " + player + " " + o);
            try {
                player.getConn().sendObject(o);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("cannot send message");
            }
        }
    }

}
