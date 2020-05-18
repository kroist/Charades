package main.java.org.Server;

import main.java.org.Tools.*;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Game implements Runnable{
    private String word;
    private Lobby lobby;
    private Timer timer;
    public Game(Lobby lobby, String word) {
        this.lobby = lobby;
        this.word = word + '\n';
    }
    public void startGame(){
        for (Player player : lobby.getGamePlayers()){
            player.setInGame(true);
        }
        lobby.sendGameAll(ConnectionMessage.GAME_STARTED);
        lobby.sendGameAll(lobby.createLeaderBoard(lobby.getGamePlayers()));
        lobby.sendGameAll(new ChatMessage("New drawer is " + lobby.getDrawer().getUsername() + "\n"));
        try {
            lobby.getDrawer().getConn().sendObject(new GameWord(word));
        } catch (IOException ignored) {
        }

        startTimer();

        //run();
        // TODO: 14.05.2020
    }

    private void startTimer() {
        if (timer != null)timer.cancel();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            private int counter = WordGenerator.getTime(lobby.getDifficulty());
            @Override
            public void run() {
                    //System.out.println("counter = " + counter);
                if (counter > 0){
                    lobby.sendAll(new GameTime(counter--));
                }
                else {
                    endGame(null);
                    timer.cancel();
                }
            }
        }, 0,1000);
    }

    @Override
    public void run() {

    }

    public synchronized void endGame(Player winner) {
        if (!lobby.isGameStarted())return;
        if (timer != null)timer.cancel();
        for (Player player : lobby.getGamePlayers()){
            player.setInGame(false);
        }
        lobby.sendAll(ConnectionMessage.GAME_ENDED);// TODO: 15.05.2020
        if (winner == null){
            lobby.sendAll(new GameResult(null, word));
        }else {
            lobby.sendAll(new GameResult(winner.getUsername(), word));
        }
        lobby.endGame(winner);
    }

    public void remove(Player player) {
        if (lobby.getDrawer() == player){
            lobby.getGamePlayers().remove(player);
            lobby.setDrawer(null);
            endGame(null);
        }else{
            lobby.getGamePlayers().remove(player);
            lobby.sendAll(lobby.createLeaderBoard(lobby.getGamePlayers()));
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
        if (ConnectionMessage.CLEAR_CANVAS.equals(obj))lobby.sendGameAll(obj);
    }

    public void handleAnswer(String msg, Player player) {
        System.out.println(msg);
        System.out.println(word);
        if (word.equals(msg))endGame(player);
    }
}
