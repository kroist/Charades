package main.java.org.Client;


import com.sun.javafx.iio.jpeg.JPEGDescriptor;
import javafx.scene.input.MouseEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
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
    public void createNewGame(boolean isPrivate){
        System.out.println(Thread.currentThread());
        if (!model.connect()){
            System.out.println("Cannot connect");
            returnToMenu("Cannot connect");
            return;
        }
        if (!model.sendObject(ConnectionMessage.CREATE_NEW_GAME)) {
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
        view.setVisibleStartGameButton(true);
        view.setGameScene();
        System.out.println("I believe");
        model.startReadingObjects();
    }

    private void getReadyToWritePoints() {
        Canvas canvas = view.getCanvas();
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                event -> {
                    //view.mousePressed(event);
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

    public void connectToTheExistingGame(String stringID) {
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
        if (!model.connect()){
            returnToMenu("Cannot connect to server?");
            return;
        }
        if (!model.sendObject(ConnectionMessage.CONN_TO_GAME)){
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
        view.setColorPickerVisible(true);
        view.setMessageText(message);
        view.setMenuScene();
        view.clearCanvas();
    }

    private void finishWritePoints() {
        Canvas canvas = view.getCanvas();
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                event -> {
                });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                event -> {
                });
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
                event -> {
                });
    }

    public void newPoint(Object obj) {
        view.newPoint(obj);
    }

    public void startGameButton() {
        model.sendObject(ConnectionMessage.START_GAME);
    }

    public void startGame() {
        if (!model.isSpectator()){
            getReadyToWritePoints();
            view.setVisibleStartGameButton(false);
            view.setColorPickerVisible(true);
        }
    }
    public void setColor(MyColor color){
        model.sendObject(color);
    }
    public void newColor(Object obj){
        view.newColor(obj);
    }
}
