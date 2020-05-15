package main.java.org.Server;

import main.java.org.Tools.ConnectionMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class Server {
    public static final ConcurrentHashMap<String, Lobby> lobbyIDs = new ConcurrentHashMap<>();
    private static final CopyOnWriteArraySet<String> usernames = new CopyOnWriteArraySet<>();

    private static Random random;

    public static class ConnectionThread extends Thread {
        private final Socket socket;
        private final ObjectInputStream in;
        private final ObjectOutputStream out;
        private Player player;

        public ConnectionThread(Socket socket) throws IOException {
            this.socket = socket;
            this.in = new ObjectInputStream(this.socket.getInputStream());
            this.out = new ObjectOutputStream(this.socket.getOutputStream());
        }

        @Override
        public void run() {
            Object obj;
            try {
                obj  = readObject();
                System.out.println(obj);
                if (!(obj instanceof String))return;
                String username = (String)obj;
                if (usernames.contains(username)){
                    sendObject(ConnectionMessage.BUSY_NICKNAME);
                    return;
                }
                usernames.add(username);
                player = new Player(this, username);
                sendObject(ConnectionMessage.CONNECTED);
                //System.out.println("i am here");
                while (true) {
                    obj = readObject();
                    System.out.println("received " + obj);
                    if (obj.equals(ConnectionMessage.RETURN_TO_MENU)){
                        sendObject(ConnectionMessage.STOP_READING);
                        player.reset();
                        continue;
                    }
                    if (!player.inLobby()){
                        if (!(obj instanceof ConnectionMessage)){
                            System.out.println("not get ConnectionMessage");
                            break;
                        }
                        ConnectionMessage msg = (ConnectionMessage)obj;
                        if (msg.equals(ConnectionMessage.CREATE_NEW_LOBBY)){
                            System.out.println("i am here");
                            Server.createNewLobby(player);
                            continue;
                        }
                        if (msg.equals(ConnectionMessage.CONNECT_TO_LOBBY)){
                            obj = readObject();
                            if (!(obj instanceof String)){
                                System.out.println("not get String");
                                break;
                            }
                            String ID = (String)obj;
                            System.out.println(ID);
                            if (!lobbyIDs.containsKey(ID)){
                                sendObject(ConnectionMessage.BAD_ID);
                                continue;
                            }
                            lobbyIDs.get(ID).addPlayer(player);
                        }
                        if (msg.equals(ConnectionMessage.LOBBY_LIST)){
                            sendObject(lobbyIDs.size());
                            for (Lobby lobby : lobbyIDs.values()){
                                sendObject(lobby.getMetadata());
                            }
                        }
                    }else {
                        player.getLobby().handleMessage(obj, player);
                    }
                }
            } catch(IOException e){
                System.out.println("client disconnected");
            } finally{
                if (player != null){
                    //System.out.println("CLOSING LOBBY " + player.getLobby().empty());
                    if (player.inLobby()){
                        player.getLobby().removePlayer(player);
                        if (player.getLobby().empty()){
                            lobbyIDs.remove(player.getLobby().getID());
                        }
                    }
                    usernames.remove(player.getUsername());
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(player + " is disconnected");
            }
        }

        public void sendObject(Object o) throws IOException {
            System.out.println("Something sent " + o);
            out.writeObject(o);
        }
        public Object readObject() throws IOException {
            try {
                return in.readObject();
            } catch (IOException e) {
                throw e;
            } catch (ClassNotFoundException e) {
                System.out.println("bad class impossible");
                e.printStackTrace();
                return null;
            }
        }

        /*private void sendToAnyone(Object o) throws IOException {
            for (ConnectionThread conn : connections) {
                conn.sendObject(o);
            }
        }*/

    }

    private static void createNewLobby(Player player) throws IOException{
        String ID = String.format("%04d", random.nextInt(10000));
        while(lobbyIDs.containsKey(ID)){
            ID = String.format("%04d", random.nextInt(10000));
        }
        //player.getConn().sendObject(ID);
        lobbyIDs.put(ID, new Lobby(ID, player));
        //lobby.addPlayer(player);
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
        try (
                ServerSocket serverSocket = new ServerSocket(portNumber)
        ) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("accepted");
                 new ConnectionThread(socket).start();
            }
        } catch (IOException e) {
            System.out.println("Exception caught while listening on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }


    }
}