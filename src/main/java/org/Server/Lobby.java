package main.java.org.Server;


import main.java.org.Tools.ChatMessage;
import main.java.org.Tools.Comparators;
import main.java.org.Tools.ConnectionMessage;
import main.java.org.Tools.WordGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Lobby {
    private final CopyOnWriteArrayList<Player> gamePlayers = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Player> lobbyPlayers = new CopyOnWriteArrayList<>();
    private boolean gameStarted;
    private final String ID;
    private Game game;
    private Player drawer;
    private final Random random = new Random();
    private final boolean isPrivate;
    private final int maxPlayers;
    private final String lobbyName;
    private final String difficulty;

    public Lobby(String id, Player player, boolean isPrivate, int maxPlayers, String lobbyName, String difficulty) throws IOException {
        ID = id;
        addPlayer(player);
        drawer = player;
        player.getConn().sendObject(ConnectionMessage.NEW_DRAWER);
        gameStarted = false;
        this.isPrivate = isPrivate;
        this.maxPlayers = maxPlayers;
        this.lobbyName = lobbyName;
        this.difficulty = difficulty;
    }
    public ArrayList<String> createLeaderBoard(CopyOnWriteArrayList<Player> arr){
        //ArrayList<Pair<String, Integer>> new_arr = new ArrayList<>();
        ArrayList<String> new_arr = new ArrayList<>();
        for(Player p : arr){
            new_arr.add(p.getUsername() + ":" + Integer.toString(p.getScore()));
            //new_arr.add(new Pair<>(p.getUsername(), p.getScore()));
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
        game = new Game(this, WordGenerator.getRandomWord(difficulty));
        gameStarted = true;
        game.startGame();
    }
    public void endGame(Player winner){
        lobbyPlayers.addAll(gamePlayers);
        gamePlayers.clear();
        gameStarted = false;
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
            } catch (Exception e) {
                System.out.println("NEW_DRAWER impossible");
            }
        }else generateDrawer();
    }
    public void addPlayer(Player player) throws IOException {
        lobbyPlayers.add(player);
        player.getConn().sendObject(ConnectionMessage.CONNECTED_TO_LOBBY);
        player.getConn().sendObject(ID);
        player.setLobby(this);
        System.out.println(gameStarted);
        if (gameStarted){
            updateWhaitingList();
            player.getConn().sendObject(createLeaderBoard(gamePlayers));
        }else {
            sendLobbyAll(createLeaderBoard(lobbyPlayers));
        }
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
                updateWhaitingList();
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
            msg.setText("[" + player.getUsername() + "]: " + msg.getText());
            sendLobbyAll(msg);
        }else {
            String word = msg.getText();
            msg.setText("[" + player.getUsername() + "]: " + msg.getText());
            sendGameAll(msg);
            game.handleAnswer(word, player);
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
    public void sendAll(Object obj) {
        sendGameAll(obj);
        sendLobbyAll(obj);
    }
    public void handleMessage(Object obj, Player player) {
        if (obj instanceof ChatMessage){
            sendChatMessage((ChatMessage)obj, player);
            return;
        }
        if (obj.equals(ConnectionMessage.START_GAME)){
            if (gameStarted){
                System.out.println("start game button problem");
                return;
            }
            startGame(player);
            return;
        }
        if (!gameStarted){
            System.out.println("i do not want receive it");
        }


        if (game != null)game.handleMessage(obj, player);
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

    public String getID() {
        return ID;
    }

    public String getMetadata(){
        int numberOfPlayers = gamePlayers.size() + lobbyPlayers.size();
        String drawerName = drawer.getUsername();
        return ((Integer)numberOfPlayers).toString() + "/" + ((Integer)maxPlayers).toString() + ":" + difficulty+ ":" + lobbyName + ":" + ID;
    }

    public boolean isPrivate(){
        return this.isPrivate;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void closeLobby(){
        Server.lobbyIDs.remove(getID());
    }

    public boolean isFull() {
        //System.out.println(gamePlayers.size() + lobbyPlayers.size() + " " + maxPlayers);
        return gamePlayers.size() + lobbyPlayers.size() >= maxPlayers;
    }
}