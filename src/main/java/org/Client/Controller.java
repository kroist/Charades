package main.java.org.Client;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import main.java.org.Tools.*;

import java.util.ArrayList;
import java.util.HashSet;


public class Controller {
    private View view;
    private Model model;
    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
    }
    public void login(String username){
        if(!CheckUsername.check(username)){
            returnToLogin("Bad nickname");
            return;
        }
        int msg = model.connect(username);
        if(msg != 1){
            if(msg == -1) {
                returnToLogin("Busy nickname");
            }
            else{
                returnToLogin("Server offline");
            }
            return;
        }
        view.setMenuScene();
        askingThread = new AskingThread();
        askingThread.start();
    }

    public class AskingThread extends Thread {
        @Override
        public void run(){
            boolean running = true;
            System.out.println("Starting to refresh lobbies");
            while(running){
                try {
                    View.fxmlController.refreshList();
                    Thread.sleep(2000);
                } catch (InterruptedException e){
                    System.out.println("Stopped refreshing lobbies");
                    running = false;
                }
            }
        }
    }
    AskingThread askingThread;


    public void createNewLobby(boolean isPrivate, int maxPlayers, String lobbyName, String difficulty){
        askingThread.interrupt();
        try {
            askingThread.join();
        } catch (Exception e){
            System.out.println(e);
        }
        // TODO: 14.05.2020
        //System.out.println(Thread.currentThread());
        /// maxPlayers * 2 + private
        String lobbyMessage = ((Integer)maxPlayers).toString() + ":" + ((Integer)(isPrivate ? 1 : 0)).toString() + ":" + lobbyName + ":" + difficulty;
        if (!model.sendObject(lobbyMessage)) {
            returnToMenu("Cannot create new lobby");
            return;
        }
        Object o = model.getObject();
        if (!ConnectionMessage.CONNECTED_TO_LOBBY.equals(o)){
            returnToMenu("cannot connect to lobby");
            return;
        }
        o = model.getObject();
        if (o instanceof String){
            String ID = (String)o;
            System.out.println("Your lobby ID is: " + ID);
            view.setGameID(ID);
        }else {
            returnToMenu("cannot receive ID");
            return;
        }
        resetPlayer();
        model.setInLobby(true);
        view.setLobbyScene();
        model.startReadingObjects();
    }
    public void connectToTheExistingLobby(String ID) {
        askingThread.interrupt();
        try {
            askingThread.join();
        } catch (Exception e){
            System.out.println(e);
        }

        if (!model.sendObject(ConnectionMessage.CONNECT_TO_LOBBY)){
            returnToMenu("Cannot connect to game");
            return;
        }
        if (!model.sendObject(ID)){
            returnToMenu("Cannot send ID");
            return;
        }
        Object o = model.getObject();
        System.out.println(o);
        if (ConnectionMessage.BAD_ID.equals(o)){
            returnToMenu("bad id");
            return;
        }
        if (ConnectionMessage.LOBBY_FULL.equals(o)){
            returnToMenu("lobby is full");
            return;
        }
        if (!ConnectionMessage.CONNECTED_TO_LOBBY.equals(o)){
            returnToMenu("cannot connect to lobby");
            return;
        }
        o = model.getObject();
        if (o instanceof String && ID.equals(o)){
            System.out.println("Your lobby ID is: " + ID);
            view.setGameID(ID);
        }else {
            returnToMenu("cannot receive ID");
            return;
        }
        resetPlayer();
        view.setGameID(ID);
        model.setInLobby(true);
        view.setLobbyScene();
        model.startReadingObjects();
    }
    public void resetPlayer(){
        model.setIsDrawer(false);
        model.setGameStarted(false);
        view.setVisibleStartGameButton(false);
        view.setDefaultLineWidth();
        view.setDefaultPickerColor();
        view.getCanvas().setDisable(true);
        view.setColorPickerVisible(false);
        view.setEraserVisible(false);
        view.setBrushVisible(false);
        view.clearCanvas();
        view.setVisibleGameTimer(false);
        view.setEnterMessageVisible(true);
        view.setGameWordVisible(false);
    }

    public void getReadyToWritePoints() {
        Canvas canvas = view.getCanvas();
        System.out.println(canvas);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                event -> {
                    //view.mousePressed(event);
                    System.out.println("hah");
                    model.sendObject(new Point(event.getX(), event.getY(), true));
                });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                event -> {
                    //view.mouseDragged(event);
                    if (model.isGameStarted()){
                        model.sendObject(new Point(event.getX(), event.getY(), false));
                    }
                });
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
                event -> {
                });
    }

    private void reset(String message){
        System.out.println(message);
        model.stopReading();
        finishWritePoints();
        model.setInLobby(false);
        view.setColorPickerVisible(false);
        view.setEraserVisible(false);
        view.setBrushVisible(false);
        view.setDefaultLineWidth();
        view.setDefaultPickerColor();
        view.setMessageText(message);
        view.clearCanvas();
        view.clearChat();
        view.clearLeaderBoard();
        view.clearWhaitingList();
        model.setIsDrawer(false);
    }

    public void returnToLogin(String message) {
        finishWritePoints();
        model.disconnect();
        view.setLoginScene();
        reset(message + " returnToLogin");
        if (message != null){
            if(message.equals("Busy nickname")) {
                View.loginSceneFXMLController.nicknameTakenBox.setText("This username is already taken");
            } else if (message.equals("Bad nickname")) {
                View.loginSceneFXMLController.nicknameTakenBox.setText(
                        "Username must have length from 1 to 16, consist of Latin letters, digits, spaces, characters '_' and '-'"
                );
            }
            else if (message.equals("Server offline")){
                View.loginSceneFXMLController.nicknameTakenBox.setText("Server is offline");
            }
        }
        else {
            View.loginSceneFXMLController.nicknameTakenBox.setText("");
        }
    }

    public void returnToMenu(String message) {
        model.sendObject(ConnectionMessage.RETURN_TO_MENU);
        finishWritePoints();
        view.setMenuScene();
        reset(message + " returnToMenu");
        askingThread = new AskingThread();
        askingThread.start();
    }

    private void finishWritePoints() {
        Canvas canvas = view.getCanvas();
        canvas.setDisable(true);
    }

    public void newPoint(Object obj) {
        view.newPoint(obj);
    }

    public void startGameButton() {
        model.sendObject(ConnectionMessage.START_GAME);
    }

    public void startGame() {
        view.setGameScene();
        model.setGameStarted(true);
        if (model.isDrawer()){
            //getReadyToWritePoints();
            view.getCanvas().setDisable(false);
            view.setVisibleStartGameButton(false);
            view.setColorPickerVisible(true);
            view.setEraserVisible(true);
            view.setBrushVisible(true);
            view.setEnterMessageVisible(false);
        }
        //view.setVisibleGameTimer(true);
    }
    public void setColor(MyColor color){
        model.sendObject(color);
    }
    public void newColor(Object obj){
        view.newColor(obj);
    }
    public void setLineWidth(Integer lineWidth){
        model.sendObject(lineWidth);
    }
    public void newLineWidth(Object obj){
        view.newLineWidth(obj);
    }
    public void sendChatMessage(ChatMessage msg){
        model.sendObject(msg);
    }
    public void newChatMessage(Object msg){
        view.newChatMessage(msg);
    }
    public void newTime(Object obj) {
        view.setNewTime(obj);
    }

    public void newLeaderBoard(Object obj) {
        view.newLeaderBoard(obj);
    }

    public void setIsBrash(boolean b) {
        view.setIsBrash(b);
        if (b){
            setColor(view.getBrushColor());
            setLineWidth(3);
        }else {
            setColor(new MyColor(Color.web("#f4f4f4")));
            setLineWidth(10);
        }
    }

    public void setBrushColor(MyColor myColor) {
        view.setBrushColor(myColor);
        if (view.isBrash())setColor(view.getBrushColor());
    }

    public void newWaitingList(Object obj) {
        @SuppressWarnings("unchecked")
        ObservableList<String> arr = FXCollections.observableArrayList((HashSet<String>)obj);
        view.setWhaitingList(arr);
    }

    public void setDrawer() {
        /*view.getCanvas().setDisable(false);
        view.setColorPickerVisible(true);
        view.setEraserVisible(true);
        view.setBrushVisible(true);*/
        view.setVisibleStartGameButton(true);
    }
    public void returnToLobby(String game_is_ended) {
        resetPlayer();
        view.setLobbyScene();
        view.setVisibleGameTimer(false);
    }


    public synchronized ArrayList<String> askForLobbies(){
        model.sendObject(ConnectionMessage.LOBBY_LIST);
        Object o = model.getObject();
        if (o instanceof Integer) {
            int lobbiesNumber = (Integer) o;
            ArrayList<String> arr = new ArrayList<String>();
            for (int i = 0; i < lobbiesNumber; i++) {
                Object ostr = model.getObject();
                if (ostr instanceof String) {
                    String str = (String) ostr;
                    arr.add(str);
                } else {
                    System.out.println("CANNOT GET LOBBY");
                    break;
                }
            }
            return arr;
        } else {
            System.out.println("CANNOT GET NUMBER OF LOBBIES");
            System.out.println(o);
            return new ArrayList<>();
        }
    }

    public void newWord(Object obj) {
        view.setNewWord(obj);
    }
}