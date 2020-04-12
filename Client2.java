package org;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class Client2 extends Application{

    private static Socket clientSocket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    public static boolean connected;

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
    public static void main(String[] args){
        String hostName = "localhost";
        int portNumber = 21;
        try {
            clientSocket = new Socket(hostName, portNumber);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            connected = true;
            System.out.println("Established connection");
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
        }



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
    @Override
    public void start(Stage stage) throws Exception {
        stage.setResizable(false);
        Canvas canvas = new Canvas(400, 400);
        gc = canvas.getGraphicsContext2D();
        initDraw(gc);
        //--Write to server--//
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                event -> {
                    gc.beginPath();
                    prevX = event.getX();
                    prevY = event.getY();
                    drawPoint(prevX, prevY);
                    gc.moveTo(prevX, prevY);
                    gc.stroke();
                    try{
                        out.writeObject(new Point(prevX, prevY, true));
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                event -> {
                    x = event.getX();
                    y = event.getY();
                    gc.lineTo(x, y);
                    gc.stroke();
                    try{
                        out.writeObject(new Point(x, y, false));
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
                event -> {

                });
        //-------------//
        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, 400, 400);
        stage.setTitle("Charades");
        stage.setScene(scene);
        stage.show();
        new Thread(){
            @Override
            public void run(){
                while(true){
                    try{
                        Object obj = in.readObject();
                        System.out.println("WE GOT SOMETHING");
                        if(obj instanceof Point){
                            System.out.print("ITS POINT!!!");
                            Point p = (Point)obj;
                            if(p.single){
                                prevX = p.x;
                                prevY = p.y;
                                drawPoint(p.x, p.y);
                            }
                            else{
                                x = p.x;
                                y = p.y;
                                drawLine(x, y);
                            }
                        }
                        else{
                            System.out.println("KEK???");
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        System.exit(0);
                    }
                }
            }
        }.start();
    }
}