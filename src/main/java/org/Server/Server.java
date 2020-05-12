package main.java.org.Server;

import main.java.org.Tools.ChatMessage;
import main.java.org.Tools.ConnectionMessage;
import main.java.org.Tools.MyColor;
import main.java.org.Tools.Point;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {

    //private static ArrayList<ConnectionThread> connections = new ArrayList<>();
    private static CopyOnWriteArrayList<Game> games = new CopyOnWriteArrayList<>();
    private static ConcurrentHashMap<Integer, Game> gameIDs = new ConcurrentHashMap<>();
    private static int freeIDs;
    private static ConnectionThread host;
    private static final Object lock = new Object();

    private static Random random;

    public static class ConnectionThread extends Thread {

        private final Socket socket;
        private final ObjectInputStream in;
        private final ObjectOutputStream out;
        private String username;
        private boolean inGame;
        Player player;
        private Game game;
        private boolean isHost;

        public ConnectionThread(Socket socket) throws IOException {
            this.socket = socket;
            this.in = new ObjectInputStream(this.socket.getInputStream());
            this.out = new ObjectOutputStream(this.socket.getOutputStream());
            inGame = false;
            isHost = false;
            game = null;
            username = null;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    if (socket.isClosed()) {
                        System.out.println("Socket is closed");
                        break;
                    }
                    try {
                        Object receivedObject = in.readObject();
                        System.out.println(receivedObject);
                        if (receivedObject == null){
                            System.out.println("Disconnected: NullMessage");
                            break;
                        }
                        if (!inGame){
                            if (!(receivedObject instanceof String))break;
                            username = (String)receivedObject;
                            sendObject(ConnectionMessage.LOGGED_IN);
                            receivedObject = in.readObject();
                            if (!(receivedObject instanceof ConnectionMessage))break;
                            if (receivedObject.equals(ConnectionMessage.CREATE_NEW_GAME)){
                                if (freeIDs == 0){
                                    System.out.println("Maximum number of lobbies exceeded");
                                    out.writeObject(ConnectionMessage.MAX_NUM_LOBBY);
                                }
                                try {
                                    Object nextEvent = in.readObject();
                                    if(!(nextEvent instanceof Boolean)) break;
                                    boolean isPrivate = (boolean)nextEvent;
                                    isHost = true;
                                    Integer ID;
                                    while (true) {
                                        ID = random.nextInt(10000);
                                        if (gameIDs.containsKey(ID))
                                            continue;
                                        break;
                                    }
                                    --freeIDs;
                                    game = new Game(ID, isPrivate);
                                    games.add(game);
                                    gameIDs.put(ID, game);
                                    game.start();
                                    System.out.println("New game started with ID: " + ID + " and isPrivate is " + game.isPrivate());

                                    player = new Player(this, game, username);
                                    game.addPlayer(player);
                                    System.out.println("Player-host " + username + " connected to game with ID: " + ID);
                                    inGame = true;
                                    out.writeObject(ID);
                                }catch (IOException | ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            else if (receivedObject.equals(ConnectionMessage.CONN_TO_GAME)) {
                                try {
                                    Object nextEvent = in.readObject();
                                    if (!(nextEvent instanceof Integer)) break;
                                    int ID = (int) nextEvent;
                                    if (gameIDs.containsKey(ID)) {
                                        Game game = gameIDs.get(ID);
                                        if (game.isStarted()) {
                                            sendObject(ConnectionMessage.GAME_ALREADY_STARTED);
                                            continue;
                                        }
                                        player = new Player(this, game, username);
                                        game.addPlayer(player);
                                        System.out.println("Player " + username + " connected to game with ID: " + ID);
                                        inGame = true;
                                        sendObject(ConnectionMessage.CONNECTED);
                                    } else {
                                        sendObject(ConnectionMessage.BAD_ID);
                                    }
                                } catch (IOException | ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            else break;

                        }
                        else {
                            if (receivedObject instanceof Point) {
                                if (player.isDrawing()) player.getGame().writeEvent(receivedObject);
                            }
                            if (receivedObject instanceof MyColor){
                                player.getGame().writeEvent(receivedObject);
                            }
                            if (receivedObject instanceof ConnectionMessage){
                                if (receivedObject.equals(ConnectionMessage.START_GAME)){
                                    synchronized (game){
                                        game.notify();
                                    }
                                }
                            }
                            if (receivedObject instanceof ChatMessage){
                                ((ChatMessage)receivedObject).setText("[" + username + "]: " + ((ChatMessage) receivedObject).getText());
                                player.getGame().writeEvent(receivedObject);
                            }
                            if (receivedObject instanceof Integer){
                                if (player.isDrawing()) player.getGame().writeEvent(receivedObject);
                            }
                            //System.out.println("We got object from: " + username);
                            //System.out.println(player.isDrawing());
                            //if (player.isDrawing())player.getGame().writeEvent(receivedObject);

                        }
                    } catch (IOException e) {
                        System.out.println("Disconnected + IOException");
                        break;
                    }
                }
            }catch(Exception notignored){
                System.out.println("Ignored " + notignored);
                notignored.printStackTrace();
            } finally{
                game.removePlayer(player);
                if (isHost){
                    game.sendAll(ConnectionMessage.GAME_ENDED);
                    games.remove(game);
                    gameIDs.remove(game.getGameID(), game);
                    ++freeIDs;
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(username + " is disconnected");
            }
        }

        public void sendObject(Object o) throws IOException {
            System.out.println("Something sent " + o);
            out.writeObject(o);
        }

        /*private void sendToAnyone(Object o) throws IOException {
            for (ConnectionThread conn : connections) {
                conn.sendObject(o);
            }
        }*/

    }

    public static void main(String[] args) throws IOException {
        random = new Random();
        /*
        if (args.length != 1) {
            System.err.println("java clientEcho <portNumber>");
            System.exit(1);
        }
        int portNumber = Integer.parseInt(args[0]);
         */
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        int portNumber = 4000;
        /*try {
            System.out.println("Enter port number");
            portNumber = Integer.parseInt(stdIn.readLine());
        } catch (IOException e){
        }*/
        freeIDs = 10;
        try (
                ServerSocket serverSocket = new ServerSocket(portNumber)
        ) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("accepted");
                ConnectionThread service = new ConnectionThread(socket);
                service.start();
            }
        } catch (IOException e) {
            System.out.println("Exception caught while listening on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }


    }
}