package main.java.org.Server;

import main.java.org.Tools.ConnectionMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class Server {
    private static final ConcurrentHashMap<String, Lobby> lobbyIDs = new ConcurrentHashMap<>();
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
                if (!(obj instanceof String))return;
                String username = (String)obj;
                if (usernames.contains(username)){
                    return; // TODO: 13.05.2020
                }
                usernames.add(username);
                player = new Player(this, username);
                sendObject(ConnectionMessage.CONNECTED);
                while (true) {
                    obj = readObject();
                    System.out.println("received " + obj);
                    if (obj.equals(ConnectionMessage.RETURN_TO_MENU)){
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
                            Server.createNewLobby(player);
                        }
                        if (msg.equals(ConnectionMessage.CONNECT_TO_LOBBY)){
                            obj = readObject();
                            if (!(obj instanceof String)){
                                System.out.println("not get String");
                                break;
                            }
                            String ID = (String)obj;
                            if (!lobbyIDs.contains(ID)){
                                sendObject(ConnectionMessage.BAD_ID);
                                continue;
                            }
                            lobbyIDs.get(ID).addPlayer(player);
                        }
                        if (msg.equals(ConnectionMessage.BROWSE_GAMES)){
                            // TODO: 13.05.2020 assign to Zub
                            int x = 0;
                        }
                    }else {
                        player.getLobby().handleMessage(obj, player);
                    }
                }
            } catch(IOException e){
                System.out.println("client disconnected");
            } finally{
                if (player != null){
                    if (player.inLobby()){
                        player.getLobby().removePlayer(player);
                        if (player.getLobby().empty()){
                            lobbyIDs.remove(player.getLobby());
                        }
                    }
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
        String ID = String.format("%06d", random.nextInt(10000000));
        while(lobbyIDs.contains(ID)){
            ID = String.format("%06d", random.nextInt(10000000));
        }
        Lobby lobby = new Lobby(ID, player);
        lobbyIDs.put(ID, lobby);
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