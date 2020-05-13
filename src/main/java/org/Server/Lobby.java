package main.java.org.Server;

import javafx.util.Pair;
import main.java.org.Tools.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Lobby {
    private CopyOnWriteArrayList<Player> gamePlayers;
    private CopyOnWriteArrayList<Player> lobbyPlayers;
    private boolean gameStarted;
    private final String ID;
    private Game game;
    private Player drawer;
    private Random random = new Random();

    public Lobby(String id, Player player) throws IOException {
        ID = id;
        addPlayer(player);
        drawer = player;
        player.getConn().sendObject(ConnectionMessage.NEW_DRAWER);
    }
    public ArrayList<Pair<String, Integer>> createLeaderBoard(CopyOnWriteArrayList<Player> arr){
        ArrayList<Pair<String, Integer>> new_arr = new ArrayList<>();
        for(Player p : arr){
            new_arr.add(new Pair<>(p.getUsername(), p.getScore()));
        }
        new_arr.sort(Comparators.pairComparator);
        return new_arr;
    }
    public void startGame(Player player){
        if (!player.equals(drawer)){
            System.out.println("Impossible");
            return;
        }
        gamePlayers.addAll(lobbyPlayers);
        lobbyPlayers.clear();
        game = new Game(this, "abacaba");// TODO: 13.05.2020
        gameStarted = true;
        game.startGame();
    }
    public void endGame(Player winner){
        lobbyPlayers.addAll(gamePlayers);
        gamePlayers.clear();
        game = null;

        if (winner != null){
            winner.setScore(winner.getScore() + 1);
        }
        if (drawer != null && winner != null){
            drawer.setScore(drawer.getScore() + 1);
        }

        sendLobbyAll(new HashSet<String>());
        sendLobbyAll(createLeaderBoard(lobbyPlayers));
        if (lobbyPlayers.contains(winner)){
            drawer = winner;
            try {
                drawer.getConn().sendObject(ConnectionMessage.NEW_DRAWER);
            } catch (IOException e) {
                System.out.println("NEW_DRAWER impossible");
            }
        }else generateDrawer();
        gameStarted = false;
    }
    public void addPlayer(Player player) throws IOException {
        lobbyPlayers.add(player);
        if (gameStarted){
            updateWhaitingList();
        }else {
            sendLobbyAll(createLeaderBoard(lobbyPlayers));
        }
        player.setLobby(this);
        player.getConn().sendObject(ConnectionMessage.CONNECTED_TO_LOBBY);
    }
    private void updateWhaitingList() {
        HashSet<String> set = new HashSet<>();
        for (Player p : lobbyPlayers){
            set.add(p.getUsername());
        }
        sendLobbyAll(set);
    }
    public void removePlayer(Player player) {
        if (!gameStarted){
            lobbyPlayers.remove(player);
            if (lobbyPlayers.isEmpty())return;
            if (player == drawer){
                generateDrawer();
            }
            sendLobbyAll(createLeaderBoard(lobbyPlayers));
        }else {
            if (player.inGame()){
                game.remove(player);
            }else {
                lobbyPlayers.remove(player);
                sendLobbyAll(createLeaderBoard(lobbyPlayers));
            }
        }
    }

    private void generateDrawer() {
        if (lobbyPlayers.isEmpty()){
            System.out.println("generateDrawer impossible");
        }else {
            drawer = lobbyPlayers.get(random.nextInt(lobbyPlayers.size()));
            try {
                drawer.getConn().sendObject(ConnectionMessage.NEW_DRAWER);
            } catch (IOException e) {
                System.out.println("generateDrawer NEW_DRAWER impossible");
            }
        }
    }

    public void sendChatMessage(ChatMessage msg, Player player) {
        if (!player.inGame()){
            sendLobbyAll(msg);
        }else {
            sendGameAll(msg);
        }
    }
    public void sendGameAll(Object obj) {
        for (Player player : gamePlayers){
            try {
                player.getConn().sendObject(obj);
            }catch (IOException e){
                System.out.println("player " + player.getUsername() + " left the game");
            }
        }
    }
    private void sendLobbyAll(Object obj) {
        for (Player player : lobbyPlayers){
            try {
                player.getConn().sendObject(obj);
            } catch (IOException e) {
                System.out.println("player " + player.getUsername() + " left the lobby");
                //e.printStackTrace();
            }
        }
    }
    public void handleMessage(Object obj, Player player) {
        if (obj instanceof ChatMessage){
            sendChatMessage((ChatMessage)obj, player);
        }
        if (obj.equals(ConnectionMessage.START_GAME)){
            if (gameStarted){
                System.out.println("start game button problem");
                return;
            }
            startGame(player);
        }
        if (!gameStarted){
            System.out.println("i do not want receive it");
        }


        game.handleMessage(obj, player);
    }
    public CopyOnWriteArrayList<Player> getGamePlayers() {
        return gamePlayers;
    }

    public Player getDrawer() {
        return drawer;
    }

    public void setDrawer(Player player) {
        drawer = player;
    }

    public boolean empty() {
        return gamePlayers.isEmpty() && lobbyPlayers.isEmpty();
    }
}