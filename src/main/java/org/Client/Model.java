package main.java.org.Client;

import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Model {
    private static Socket clientSocket;
    private static String hostName = "localhost";
    private static int portNumber = 4000;
    private static boolean inGame;
    private static boolean isSpectator;
    private static Controller controller;
    ObjectInputStream in;
    ObjectOutputStream out;
    public void setController(Controller c){
        controller = c;
    }
    public void setInGame(boolean b){
        inGame = b;
    }
    public void setIsSpectator(boolean b){
        isSpectator = b;
    }
    public boolean connect() {
        try{
            clientSocket = new Socket(hostName, portNumber);
            System.out.println(clientSocket.isConnected());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            System.out.println("Established connection");
            return true;
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("cannot connect");
        }
        return false;
    }
    public void disconnect() {
        try{
            in = null;
            out = null;
            if (clientSocket != null)clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean sendObject(Object o){
        if (clientSocket == null || out == null)return false;
        try{
            out.writeObject(o);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Object getObject() {
        if (clientSocket == null || in == null)return null;
        try{
            Object o = in.readObject();
            return o;
        } catch (IOException | ClassNotFoundException e) {
            //e.printStackTrace();
        }
        return null;
    }

    public void PointReader(){
        System.out.println(inGame);
        while (inGame) {
            //System.out.println("inGame");
            try {
                System.out.println("Something received");
                Object obj = getObject();
                if (obj instanceof Point) {
                    controller.newPoint(obj);
                } else {
                    System.out.println("KEK???");
                }

            } catch (Exception e) {
                System.out.println("SERVER DOWN");
                Platform.runLater(() -> controller.returnToMenu("SERVER DOWN"));
                //e.printStackTrace();
                //System.exit(0);
            }
        }
    }

    public void startReadingPoints() {
        new Thread(() -> PointReader()).start();
    }
}
