package main.java.org.Client;

import javafx.application.Platform;
import javafx.util.Pair;
import main.java.org.Tools.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class Model {
    private static Socket clientSocket;
    private static String hostName = "192.168.137.1";
    private static int portNumber = 4000;
    private static boolean inLobby;
    private static boolean gameStarted;
    private static boolean isDrawer;
    private static Controller controller;
    ObjectInputStream in;
    ObjectOutputStream out;
    public void setController(Controller c){
        controller = c;
    }
    public void setInLobby(boolean b){
        inLobby = b;
    }
    public void setIsSpectator(boolean b){
        isDrawer = b;
    }
    public int connect(String nickname) {
        try{
            clientSocket = new Socket(hostName, portNumber);
            System.out.println(clientSocket.isConnected());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            sendObject(nickname);
            Object o = getObject();
            System.out.println("Object is " + o);
            if (o instanceof ConnectionMessage && o.equals(ConnectionMessage.CONNECTED)){
                System.out.println("Established connection");
                return 1;
            }else {
                disconnect();
                return -1;
            }
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("cannot connect");
            return -2;
        }
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
            synchronized (out){
                out.writeObject(o);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Object getObject() {
        if (clientSocket == null || in == null)return null;
        try{
            Object o;
            synchronized (in) {
                o = in.readObject();
                return o;
            }
            /*while (ConnectionMessage.STOP_READING.equals(o)){
                synchronized (in) {
                    o = in.readObject();
                }
            }*/
        } catch (IOException | ClassNotFoundException e) {
            //e.printStackTrace();
            controller.returnToLogin("cannot receive object");
            //System.exit(0);
        }
        return null;
    }

    public Object getObjectForReader() {
        if (clientSocket == null || in == null) return null;
        try {
            synchronized (in){
                return in.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            controller.returnToLogin("cannot receive object");
            //System.exit(0);
        }
        return null;
    }

    public void setIsDrawer(boolean isDrawer) {
        Model.isDrawer = isDrawer;
        if (isDrawer)controller.setDrawer();
    }

    public boolean inLobby() {
        return inLobby;
    }


    public class ObjectReader extends Thread {
        @Override
        public void run() {
            System.out.println(inLobby);
            System.out.println("STARTED READING");
            while (true) {
                try {
                    System.out.println("start receiving");
                    //Object obj = getObjectForReader();
                    Object obj;
                    synchronized (in){
                        obj = getObjectForReader();
                    }
                    System.out.println("really received " + obj);
                    if (obj instanceof ConnectionMessage) {
                        if (obj.equals(ConnectionMessage.STOP_READING)) break;
                        if (obj.equals(ConnectionMessage.NEW_DRAWER))
                            setIsDrawer(true);
                        if (obj.equals(ConnectionMessage.GAME_STARTED)) {
                            controller.startGame();
                        }
                        if (obj.equals(ConnectionMessage.GAME_ENDED)) {
                            controller.returnToLobby();
                        }
                        if (obj.equals(ConnectionMessage.CLEAR_CANVAS))controller.clearCanvas();
                    } else if (obj instanceof GameResult){
                        controller.newGameResult(obj);
                    } else if (obj instanceof GameWord){
                        controller.newWord(obj);
                    } else if (obj instanceof GameTime){
                        controller.newTime(obj);
                    } else if (obj instanceof Point) {
                        controller.newPoint(obj);
                    } else if (obj instanceof MyColor) {
                        controller.newColor(obj);
                    } else if (obj instanceof ChatMessage) {
                        controller.newChatMessage(obj);
                    } else if (obj instanceof ArrayList) {
                        ArrayList<Pair<String, Integer>> arr = new ArrayList<>();
                        ArrayList<String> arr2 = (ArrayList<String>) obj;
                        for (String string : arr2){
                            String[] strings = string.split(":");
                            arr.add(new Pair<String, Integer>(strings[0], Integer.parseInt(strings[1])));
                        }
                        controller.newLeaderBoard(arr);
                    } else if (obj instanceof Integer) {
                        controller.newLineWidth(obj);
                    } else if (obj instanceof HashSet) {
                        controller.newWaitingList(obj);
                    } else {
                        System.out.println(obj);
                        System.out.println("IMPOSSIBLE");
                    }
                } catch (Exception e) {
                    System.out.println("SERVER DOWN");
                    Platform.runLater(() -> controller.returnToMenu("SERVER DOWN"));
                }
            }
            System.out.println("STOPPED READING");
        }
    }
    private ObjectReader reader = null;

    public void startReadingObjects() {
        reader = new ObjectReader();
        reader.start();
    }


    public boolean isDrawer() {
        return isDrawer;
    }

    public void stopReading() {
        if (reader != null && !reader.isInterrupted()) {
            reader.interrupt();
            System.out.println("reader not received STOP_READING");
        }
        //System.out.println("interrupted");
    }

    public boolean isGameStarted() {
        return gameStarted;
    }
    public void setGameStarted(boolean b) {
        gameStarted = b;
    }
}
