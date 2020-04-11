package org;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    public static Socket clientSocket;
    public static PrintWriter out;
    public static BufferedReader in, stdIn;

    public static String username;

    public static boolean connected;

    private static void readFromServer(){
        while(true){
            try {
                String s = in.readLine();
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

    private static void writeToServer(){
        while(connected){
            try {
                String s = stdIn.readLine();
                System.out.println("wtf: " + s);
                out.println(s);
            } catch(IOException e){
                System.err.println("Can't read from stdIn");
            }
        }
    }

    public static void main(String[] args){
        if (args.length != 2){
            System.err.println("java serverEcho <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        try {
            clientSocket = new Socket(hostName, portNumber);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            connected = true;
            /*Runtime.getRuntime().addShutdownHook(new Thread(){
                public void run(){
                    try {
                        clientSocket.close();
                        System.out.println("socket is shut down");
                    } catch (IOException e){
                        System.out.println(e.getMessage());
                    }
                }
            });*/

            System.out.println("Established connection");
            System.out.println("Enter your username:");
            username = stdIn.readLine();
            out.println(username);

            new Thread(){
                @Override
                public void run(){
                    readFromServer();
                }
            }.start();
            writeToServer();
            System.out.println("Server closed");

        } catch (UnknownHostException e){
            System.err.println("Don't know about host on da port " + hostName + ":" + portNumber);
            System.exit(1);
        } catch (IOException e){
            System.err.println("I/O connection error to the " + hostName + ":" + portNumber);
            System.exit(1);
        } finally {
            out.close();
            try {
                clientSocket.close();
            } catch (IOException e){
                System.err.println(e.getMessage());
            }
        }



    }


}
