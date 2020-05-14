package main.java.org.Client;


import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import main.java.org.Tools.ChatMessage;
import main.java.org.Tools.ConnectionMessage;
import main.java.org.Tools.MyColor;
import main.java.org.Tools.Point;


public class Controller {
    private View view;
    private Model model;
    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
    }
    public void login(String nickname){
        if (!model.connect(nickname)){
            returnToMenu("Cannot connect");
            return;
        }
    }

    public void createNewGame(boolean isPrivate, String nickname){
        // TODO: 14.05.2020
        //System.out.println(Thread.currentThread());
        login(nickname);
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
        model.setInLobby(true);
        model.setIsSpectator(false);
        //setDrawer(false);
        view.getCanvas().setDisable(false);
        view.setVisibleStartGameButton(true);
        view.setGameScene();
        model.startReadingObjects();
    }

    public void setDrawer(boolean isDrawer){
        view.setDefaultLineWidth();
        view.setDefaultPickerColor();
        if (isDrawer){
            view.getCanvas().setDisable(false);
            view.setColorPickerVisible(true);
            view.setEraserVisible(true);
            view.setBrushVisible(true);
        }else {
            view.getCanvas().setDisable(true);
            view.setColorPickerVisible(false);
            view.setEraserVisible(false);
            view.setBrushVisible(false);
        }
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

    public void connectToTheExistingGame(String ID, String nickname) {
        login(nickname);
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
        setDrawer(false);
        view.setGameID(ID);
        model.setInLobby(true);
        model.setIsSpectator(true);
        view.setVisibleStartGameButton(false);
        view.setGameScene();
        model.startReadingObjects();
    }

    public void returnToMenu(String message) {
        System.out.println("I returned to menu with " + message);
        finishWritePoints();
        model.disconnect();
        model.setInLobby(false);
        view.setColorPickerVisible(false);
        view.setEraserVisible(false);
        view.setBrushVisible(false);
        view.setDefaultLineWidth();
        view.setDefaultPickerColor();
        view.setMessageText(message);
        view.setMenuScene();
        view.clearChat();
        view.clearCanvas();
        view.clearLeaderBoard();
    }

    private void finishWritePoints() {
        Canvas canvas = view.getCanvas();
        canvas.setDisable(true);
        /*
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                event -> {
                });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                event -> {
                });
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
                event -> {
                });

         */
    }

    public void newPoint(Object obj) {
        view.newPoint(obj);
    }

    public void startGameButton() {
        model.sendObject(ConnectionMessage.START_GAME);
    }

    public void startGame() {
        if (!model.isSpectator()){
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

    public void newWaitingList() {
        // TODO: 14.05.2020  
    }
}