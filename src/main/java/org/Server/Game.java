package main.java.org.Server;

import main.java.org.Tools.ConnectionMessage;
import main.java.org.Tools.Point;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class Game extends Thread{
    private CopyOnWriteArrayList<Player> players;
    private int gameID;
    private boolean isStarted, isPrivate;
    private Player whoDrawing;
    public Game(int gameID, boolean isPrivate){
        this.gameID = gameID;
        this.isPrivate = isPrivate;
        isStarted = false;
        players = new CopyOnWriteArrayList<>();
    }
    public int getGameID(){
        return gameID;
    }
    public boolean isStarted(){
        return isStarted;
    }
    public boolean isPrivate(){
        return isPrivate;
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
            System.out.println("KEKOS");
            for (Player player : players){
                player.getConn().sendObject(ConnectionMessage.GAME_STARTED);
            }
            System.out.println("Game started");
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
