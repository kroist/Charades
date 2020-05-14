package main.java.org.Server;

import main.java.org.Tools.ConnectionMessage;
import main.java.org.Tools.MyColor;
import main.java.org.Tools.Point;

public class Game implements Runnable{
    private String word;
    private Lobby lobby;
    public Game(Lobby lobby, String word) {
        this.lobby = lobby;
        this.word = word;
    }
    public void startGame(){
        for (Player player : lobby.getGamePlayers()){
            player.setInGame(true);
        }
        lobby.sendGameAll(ConnectionMessage.GAME_STARTED);
        lobby.sendGameAll(lobby.createLeaderBoard(lobby.getGamePlayers()));
        //run();
        // TODO: 14.05.2020
    }
    @Override
    public void run() {

    }

    public void endGame(Player winner) {
        for (Player player : lobby.getGamePlayers()){
            player.setInGame(false);
        }
        lobby.sendGameAll(ConnectionMessage.GAME_ENDED);
        lobby.endGame(winner);
    }

    public void remove(Player player) {
        if (lobby.getDrawer() == player){
            lobby.getGamePlayers().remove(player);
            lobby.setDrawer(null);
            endGame(null);
        }else{
            lobby.getGamePlayers().remove(player);
            if (lobby.getGamePlayers().size() == 0)endGame(null);
        }
    }

    public void handleMessage(Object obj, Player player) {
        if (!(player == lobby.getDrawer())){
            System.out.println("not drawer wants to draw");
            return;
        }
        if (obj instanceof Point)lobby.sendGameAll(obj);
        if (obj instanceof MyColor)lobby.sendGameAll(obj);
        if (obj instanceof Integer)lobby.sendGameAll(obj);
    }
}
