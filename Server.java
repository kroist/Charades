package org;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class Server {

    private static ArrayList<ConnectionThread> connections = new ArrayList<>();
    private static ConnectionThread host;
    private static final Object lock = new Object();
    public static class ConnectionThread extends Thread {

        private final Socket socket;
        private final ObjectInputStream in;
        private final ObjectOutputStream out;
        private static String username;

        public ConnectionThread(Socket socket) throws IOException {
            this.socket = socket;
            this.in = new ObjectInputStream(this.socket.getInputStream());
            this.out = new ObjectOutputStream(this.socket.getOutputStream());
            connections.add(this);
        }

        @Override
        public void run() {
            try {
                synchronized (lock) {
                    if (host == null) {
                        host = this;
                    }
                }
                while (true) {
                    if (socket.isClosed()) {
                        break;
                    }
                    try {
                        Object recievedEvent = in.readObject();
                        System.out.println(recievedEvent);
                        if (recievedEvent != null) {
                            //System.out.println("We got object from: " + username);
                            if (this == host) {
                                this.sendToAnyone(recievedEvent);
                            }
                        } else {
                            System.out.println("Disconnected + NullMessage");
                            break;
                        }
                    } catch (IOException e) {
                        System.out.println("Disconnected + IOException");
                        break;
                    }
                }
            }catch(Exception ignored){
            } finally{
                System.out.println(username + " disconnected");
                connections.remove(this);
            }
        }

        protected void sendObject(Object o) throws IOException {
            out.writeObject(o);
        }

        private void sendToAnyone(Object o) throws IOException {
            for (ConnectionThread conn : connections) {
                conn.sendObject(o);
            }
        }

    }

    public static void main(String[] args) throws IOException {
        /*
        if (args.length != 1) {
            System.err.println("java clientEcho <portNumber>");
            System.exit(1);
        }
        int portNumber = Integer.parseInt(args[0]);
         */
        int portNumber = 21;
        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
        ) {
            while (true) {
                Socket socket = serverSocket.accept();
                ConnectionThread service = new ConnectionThread(socket);
                service.start();
            }
        } catch (IOException e) {
            System.out.println("Exception caught while listening on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }


    }
}