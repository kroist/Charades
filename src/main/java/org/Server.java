package org;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Server {

    private static ArrayList<ConnectionThread> connections = new ArrayList<ConnectionThread>();

    public static class ConnectionThread extends Thread{

        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String username;

        public ConnectionThread(Socket socket){
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                username = in.readLine();
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
        }

        @Override
        public void run(){
            while(true){
                try {
                    String recievedMessage = in.readLine();
                    System.out.println(recievedMessage);
                    if (recievedMessage != null) {
                        System.out.println(username + " wants to say: " + recievedMessage);
                        this.sendToAnyone(username + " says: " + recievedMessage);
                    }
                    TimeUnit.SECONDS.sleep(1);
                } catch (IOException e){
                    System.out.println(e.getMessage());
                } catch(InterruptedException e){
                    System.out.println(e.getMessage());
                }
                finally {
                    out.close();
                }
            }
        }

        protected void sendMessage(String message){
            out.println(message);
        }

        private void sendToAnyone(String message){
            for (ConnectionThread conn : connections){
                conn.sendMessage(message);
            }
        }

    }

    public static void main(String[] args) throws IOException {

        if (args.length != 1){
            System.err.println("java clientEcho <portNumber>");
            System.exit(1);
        }
        int portNumber = Integer.parseInt(args[0]);

        try(
                ServerSocket serverSocket = new ServerSocket(portNumber);
        ){
            while(true){
                Socket socket = serverSocket.accept();
                ConnectionThread service = new ConnectionThread(socket);
                System.out.println("Connected " + service.username);
                connections.add(service);
                service.start();
            }
        }
        catch(IOException e){
            System.out.println("Exception caught while listening on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }


    }


}
