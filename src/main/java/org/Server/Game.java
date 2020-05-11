package main.java.org.Server;

import javafx.util.Pair;
import main.java.org.Tools.ChatMessage;
import main.java.org.Tools.ConnectionMessage;
import main.java.org.Tools.MyColor;
import main.java.org.Tools.Point;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

public class Game extends Thread{
    private CopyOnWriteArrayList<Player> players;
    private int gameID;
    private boolean isStarted, isPrivate;
    private Player whoDrawing;
    private Comparator<Pair<String, Integer>> pairComparator = (t1, t2) -> {
        int val = Integer.compare(t1.getValue(), t2.getValue());
        if(val == 0) {
            return t1.getKey().compareTo(t2.getKey());
        }
        else {
            return val;
        }
    };
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
            ArrayList<Pair<String, Integer>> leaderBoard = createLeaderBoard(players);
            for (Player player : players){
                player.getConn().sendObject(ConnectionMessage.GAME_STARTED);
                player.getConn().sendObject(leaderBoard);
            }
            System.out.println("Game started");
            isStarted = true;
            whoDrawing = players.get(0);
            whoDrawing.setIsDrawing(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private ArrayList<Pair<String, Integer>> createLeaderBoard(CopyOnWriteArrayList<Player> arr){
        ArrayList<Pair<String, Integer>> new_arr = new ArrayList<>();
        for(Player p : arr){
            new_arr.add(new Pair<>(p.getUsername(), p.getScore()));
        }
        new_arr.sort(pairComparator);
        return new_arr;
    }
    public synchronized void writeEvent(Object o) throws IOException {
        if (o instanceof ChatMessage){
            sendAll(o);
        }
        if (!isStarted)return;
        if (o instanceof Point){
            sendAll(o);
        }
        if (o instanceof MyColor){
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
