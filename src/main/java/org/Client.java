package main.java.org;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.ContentHandler;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Time;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Client extends Application{

    private static Socket clientSocket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    public static boolean connected;

    private static boolean isSpectator;

    private static String hostName;
    private static int portNumber;

    ///useless method
    private static void readFromServer(){
        while(true){
            try {
                String s = in.readLine(); // maybe readUTF
                if (s == null){
                    System.err.println("disconnected (null string)");
                    connected = false;
                    return;
                }
                System.out.println("message: " + s);
            } catch(IOException e){
                System.err.println("something wrong in readFromServer");
                connected = false;
                return;
            }
        }
    }
    static BufferedReader stdIn;
    public static void main(String[] args){
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        //System.out.println("enter host ip:");
        //hostName = stdIn.readLine();
        //System.out.println("enter host port:");
        //portNumber = Integer.parseInt(stdIn.readLine());

        hostName = "localhost";
        portNumber = 4000;
            /*
            System.out.println("Are you going to be a host(H) or a spectator(S)? Enter the corresponding letter");
            String hostOrSpectator = stdIn.readLine();
            if (hostOrSpectator.equals("H")){
                isSpectator = false;
            }
            else if (hostOrSpectator.equals("S")){
                isSpectator = true;
            }
            else {
                System.out.println("Please enter correct value!");
                System.exit(1);
            }*/

        /*
        try {
            clientSocket = new Socket(hostName, portNumber);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            connected = true;
            System.out.println("Established connection");
            if (!isSpectator) {
                out.writeObject("create new game");
                try {
                    Object obj = in.readObject();
                    if (obj instanceof String) {
                        if (obj.equals("maxnumlobb")) {
                            System.out.println("Maximum number of lobbies exceeded");
                            System.exit(1);
                        }
                    }
                    if (obj instanceof Integer) {
                        System.out.println("your game ID is: " + obj);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else {
                out.writeObject("connect to the existing game");
                System.out.println("enter ID of the game you want to join: ");
                stdIn = new BufferedReader(new InputStreamReader(System.in));
                int id = Integer.parseInt(stdIn.readLine());
                out.writeObject(id);
            }
            launch(args);
            System.out.println("Server closed");

        } catch (UnknownHostException e){
            System.err.println("Don't know about host on da port " + hostName + ":" + portNumber);
            System.exit(1);
        } catch (IOException e){
            System.err.println("I/O connection error to the " + hostName + ":" + portNumber);
            System.exit(1);
        } finally {
            try {
                out.close();
                clientSocket.close();
            } catch (IOException e){
                System.err.println(e.getMessage());
            }
        }*/
        launch(args);

    }
    private int lineWidth = 3;
    private void initDraw(GraphicsContext gc){
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();

        gc.setFill(Color.WHITE);

        gc.fill();
        gc.strokeRect(
                0,              //x of the upper left corner
                0,              //y of the upper left corner
                canvasWidth,    //width of the rectangle
                canvasHeight);  //height of the rectangle
        gc.setFill(Color.RED);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(lineWidth);

    }
    private void drawPoint(double x, double y){
        x = x - lineWidth / 2.0;
        y = y - lineWidth / 2.0;
        gc.setFill(Color.BLACK);
        gc.fillOval(x, y, lineWidth, lineWidth);
    }
    private void drawLine(double x, double y){
        gc.setLineWidth(lineWidth);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.strokeLine(prevX, prevY, x, y);
        prevX = x;
        prevY = y;
    }
    private double prevX, prevY, x, y;
    private static GraphicsContext gc;
    private Stage stage;
    private Scene menuScene;
    private Scene gameScene;
    private Text messageText;
    private boolean inGame = false;
    Canvas canvas;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setMinHeight(400);
        stage.setMinWidth(400);
        stage.setResizable(false);
        canvas = new Canvas(400, 400);
        gc = canvas.getGraphicsContext2D();
        initDraw(gc);
        //--Write to server--//
        new Thread(()-> {
            while (!inGame || isSpectator) {
                synchronized (this){
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        returnToMenu("cannot wait");
                    }
                }
                if (!inGame || isSpectator)continue;
                //System.out.println("i need to send messages");
                canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                        event -> {
                    gc.beginPath();
                    prevX = event.getX();
                    prevY = event.getY();
                    drawPoint(prevX, prevY);
                    gc.moveTo(prevX, prevY);
                    gc.stroke();
                    try {
                        out.writeObject(new Point(prevX, prevY, true));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                        event -> {
                    x = event.getX();
                    y = event.getY();
                    gc.lineTo(x, y);
                    gc.stroke();
                    try {
                        out.writeObject(new Point(x, y, false));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
                        event -> {
                });
                break;
            }
        }).start();
        //-------------//
        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        gameScene = new Scene(root, 400, 400);
        stage.setTitle("Charades");
        //stage.setScene(gameScene);



        VBox menu = new VBox();
        Button button1 = new Button("Create new game");
        Button button2 = new Button("Connect to the existing game");
        TextField textField = new TextField("Enter your game ID here");
        
        messageText = new Text();
        button1.setOnMouseClicked(mouseEvent -> createNewGame());
        System.out.println(textField.getCharacters().toString());
        button2.setOnMouseClicked(mouseEvent -> connectToTheExistingGame(textField.getCharacters().toString()));
        
        menu.getChildren().addAll(textField, button1, button2, messageText);
        menuScene = new Scene(menu, 400, 400);
        stage.setScene(menuScene);

        stage.setOnCloseRequest(windowEvent -> System.exit(0));
        stage.show();
        inGame = false;

        new Thread(() -> {
            while (!inGame){
                synchronized (this){
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        returnToMenu("cannot wait");
                    }
                }
                if (!inGame)continue;
                //System.out.println("i waited");
                while (inGame) {
                    try {
                        //System.out.println(in.available());
                        //if (in.available() == 0) continue;
                        //System.out.println("i received something");
                        Object obj = in.readObject();
                        if (obj instanceof Point) {
                            System.out.println("POINT!!!");
                            Point p = (Point) obj;
                            if (p.single) {
                                prevX = p.x;
                                prevY = p.y;
                                drawPoint(p.x, p.y);
                            } else {
                                x = p.x;
                                y = p.y;
                                drawLine(x, y);
                            }
                        } else {
                            System.out.println("KEK???");
                        }
                    } catch (Exception e) {
                        System.out.println("SERVER DOWN");
                        returnToMenu("SERVER DOWN");
                        e.printStackTrace();
                        //System.exit(0);
                    }
                }
            }
        }).start();
    }

    private void connectToTheExistingGame(String stringID) {
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
        try {
            clientSocket = new Socket(hostName, portNumber);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            connected = true;
            System.out.println("Established connection");

            out.writeObject("connect to the existing game");
            out.writeObject(ID);
            inGame = true;
            isSpectator = true;
            System.out.println("i am here");
            synchronized (this){
                notifyAll();
            }
            stage.setScene(gameScene);

        } catch (UnknownHostException e){
            System.err.println("Don't know about host on da port " + hostName + ":" + portNumber);
            returnToMenu("Don't know about host on da port " + hostName + ":" + portNumber);
            //System.exit(1);
        } catch (IOException e){
            System.err.println("I/O connection error to the " + hostName + ":" + portNumber);
            returnToMenu("I/O connection error to the " + hostName + ":" + portNumber);
            //System.exit(1);
        } finally {
            //inGame = false;
            /*try {
                out.close();
                clientSocket.close();
            } catch (IOException e){
                System.err.println(e.getMessage());
            }*/
            //returnToMenu("");
        }
    }

    private void createNewGame() {
        try {
            clientSocket = new Socket(hostName, portNumber);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            connected = true;
            //if (in.available() == 0) System.out.println(0);else System.out.println("many");
            System.out.println("Established connection");
            out.writeObject("create new game");
            try {
                Object obj = in.readObject();
                if (obj instanceof String) {
                    if (obj.equals("maxnumlobb")) {
                        System.out.println("Maximum number of lobbies exceeded");
                        //System.exit(1);
                    }
                }
                int ID;
                if (obj instanceof Integer) {
                    System.out.println("your game ID is: " + obj);
                    ID = (int) obj;
                }
                inGame = true;
                isSpectator = false;
                System.out.println("i am here");
                stage.setScene(gameScene);
                synchronized (this){
                    notifyAll();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (UnknownHostException e){
            System.err.println("Don't know about host on da port " + hostName + ":" + portNumber);
            returnToMenu("Don't know about host on da port " + hostName + ":" + portNumber);
        } catch (IOException e){
            System.err.println("I/O connection error to the " + hostName + ":" + portNumber);
            returnToMenu("I/O connection error to the " + hostName + ":" + portNumber);
        } finally {
            //inGame = false;
            /*try {
                out.close();
                clientSocket.close();
                System.out.println("HAHA spijmav");
            } catch (IOException e){
                System.err.println(e.getMessage());
            }*/
            //returnToMenu("");
        }
    }

    private void returnToMenu(String message) {
        try {
            if (clientSocket != null)clientSocket.close();
        } catch (IOException e){
            System.err.println(e.getMessage());
        }
        System.out.println("i returned to menu?");
        clearEventHAndlers(canvas);
        inGame = false;
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        messageText.setText(message);
        stage.setScene(menuScene);
    }

    private void clearEventHAndlers(Canvas canvas) {
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
}