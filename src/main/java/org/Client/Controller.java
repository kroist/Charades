package main.java.org.Client;


import javafx.scene.input.MouseEvent;
import javafx.scene.canvas.Canvas;
import main.java.org.Tools.Point;


public class Controller {
    private  View view;
    private Model model;
    public Controller(View view, Model model) {
         this.view = view;
         this.model = model;
    }
    public void createNewGame(){
        System.out.println(Thread.currentThread());
        //System.out.println("button pressed");
        if (!model.connect()){
            returnToMenu("cannot connect");
            return;
        }
        if (!model.sendObject("create new game")){
            returnToMenu("cannot send");
            return;
        }
        Object o = model.getObject();
        if (o instanceof String){
            if (o.equals("maxnumlobb")){
                System.out.println("Maximum number of lobbies exceeded");
                returnToMenu("Maximum number of lobbies exceeded");
            }
        }
        int ID;
        if (o instanceof Integer){
            ID = (int)o;
            System.out.println("your game ID is: " + ID);
            view.setGameID("" + ID);
        }
        //getReadyToWritePoints();
        model.setInGame(true);
        model.setIsSpectator(false);
        view.setVisibleStartGameButton(true);
        view.setGameScene();
        System.out.println("i believe");
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
                returnToMenu("game ID should be an integer between 0 and 9999");
                return;
            }
            ID = Integer.parseInt(stringID);
        } catch (NumberFormatException e) {
            returnToMenu("game ID should be an integer between 0 and 9999");
            return;
        }
        if (!model.connect()){
            returnToMenu("cannot connect");
            return;
        }
        if (!model.sendObject("connect to the existing game")){
            returnToMenu("cannot send");
            return;
        }
        if (!model.sendObject(ID)){
            returnToMenu("cannot send");
            return;
        }
        Object msg = model.getObject();
        System.out.println(msg);
        if (msg == null || (!(msg instanceof String))){
            returnToMenu("is should not happened, wow, you found the bug");
            return;
        }
        if (!msg.equals("connected")){
            returnToMenu((String)msg);
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
        System.out.println("i returend to menu");
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
        model.sendObject("start game");
    }

    public void startGame() {
        if (!model.isSpectator()){
            getReadyToWritePoints();
            view.setVisibleStartGameButton(false);
        }

    }
}
