package main.java.org.Client;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import main.java.org.Tools.ChatMessage;
import main.java.org.Tools.ConnectionMessage;
import main.java.org.Tools.MyColor;
import main.java.org.Tools.Point;

import java.util.HashSet;

import javax.print.DocFlavor;
import java.util.ArrayList;


public class Controller {
    private View view;
    private Model model;
    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
    }
    public void login(String username){
        if (!model.connect(username)){
            returnToLogin("Busy nickname");
            return;
        }
        view.setMenuScene();
    }


    public void createNewLobby(boolean isPrivate){
        // TODO: 14.05.2020
        //System.out.println(Thread.currentThread());
        if (!model.sendObject(ConnectionMessage.CREATE_NEW_LOBBY)) {
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
        view.setVisibleStartGameButton(false);
        view.setDefaultLineWidth();
        view.setDefaultPickerColor();
        view.getCanvas().setDisable(true);
        view.setColorPickerVisible(false);
        view.setEraserVisible(false);
        view.setBrushVisible(false);
        model.setIsDrawer(false);
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
                    model.sendObject(new Point(event.getX(), event.getY(), false));
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
    }

    public void returnToMenu(String message) {
        model.sendObject(ConnectionMessage.RETURN_TO_MENU);
        finishWritePoints();
        view.setMenuScene();
        reset(message + " returnToMenu");
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
        if (model.isDrawer()){
            //getReadyToWritePoints();
            view.getCanvas().setDisable(false);
            view.setVisibleStartGameButton(false);
            view.setColorPickerVisible(true);
            view.setEraserVisible(true);
            view.setBrushVisible(true);
        }
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
    }


    public ArrayList<String> askForLobbies(){
        model.sendObject(ConnectionMessage.LOBBY_LIST);
        Object o = model.getObject();
        if (o instanceof Integer){
            int lobbiesNumber = (Integer)o;
            ArrayList<String> arr = new ArrayList<String>();
            for (int i = 0; i < lobbiesNumber; i++){
                Object ostr = model.getObject();
                if (ostr instanceof String){
                    String str = (String)ostr;
                    arr.add(str);
                }
                else {
                    System.out.println("CANNOT GET LOBBY");
                    break;
                }
            }
            return arr;
        }
        else {
            System.out.println("CANNOT GET NUMBER OF LOBBIES");
            return null;
        }
    }

}