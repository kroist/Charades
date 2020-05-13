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
    public void createNewGame(boolean isPrivate, String nickname){
        System.out.println(Thread.currentThread());
        if (!model.connect(nickname)){
            System.out.println("Cannot connect");
            returnToMenu("Cannot connect");
            return;
        }
        if (!model.sendObject(ConnectionMessage.CREATE_NEW_LOBBY)) {
            System.out.println("Cannot start new game");
            returnToMenu("Cannot start new game");
            return;
        }
        if(!model.sendObject(isPrivate)){
            System.out.println("Cannot start new private/public game");
            returnToMenu("Cannot start new game");
            return;
        }
        Object o = model.getObject();
        if (o instanceof ConnectionMessage){
            if (o.equals(ConnectionMessage.MAX_NUM_LOBBY)){
                System.out.println("Maximum number of lobbies exceeded");
                returnToMenu("Maximum number of lobbies exceeded");
            }
        }
        int ID;
        if (o instanceof Integer){
            ID = (int)o;
            System.out.println("Your game ID is: " + ID);
            view.setGameID("" + ID);
        }
        //getReadyToWritePoints();
        model.setInGame(true);
        model.setIsSpectator(false);
        view.getCanvas().setDisable(false);
        view.setVisibleStartGameButton(true);
        view.setGameScene();
        System.out.println("I believe");
        model.startReadingObjects();
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

    public void connectToTheExistingGame(String stringID, String nickname) {
        int ID;
        try {
            if (stringID == null){
                returnToMenu("Game ID should be an integer between 0 and 9999");
                return;
            }
            ID = Integer.parseInt(stringID);
        } catch (NumberFormatException e) {
            returnToMenu("Game ID should be an integer between 0 and 9999");
            return;
        }
        if (!model.connect(nickname)){
            returnToMenu("Cannot connect to server?");
            return;
        }
        if (!model.sendObject(ConnectionMessage.CONNECT_TO_LOBBY)){
            returnToMenu("Cannot connect to game");
            return;
        }
        if (!model.sendObject(ID)){
            returnToMenu("Cannot send ID");
            return;
        }
        Object msg = model.getObject();
        System.out.println(msg);
        if (!(msg instanceof ConnectionMessage)){
            returnToMenu("It should not happened. Wow!!! You found the bug!!!");
            return;
        }
        if (!msg.equals(ConnectionMessage.CONNECTED)){
            returnToMenu(msg.toString());
            return;
        }
        view.setGameID(stringID);
        model.setInGame(true);
        model.setIsSpectator(true);
        view.setVisibleStartGameButton(false);
        view.setGameScene();
        model.startReadingObjects();
    }

    public void returnToMenu(String message) {
        finishWritePoints();
        model.disconnect();
        model.setInGame(false);
        System.out.println("I returned to menu");
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
}