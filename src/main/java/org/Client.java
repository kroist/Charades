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

    private static void readFromServer(){
        while(true){
            try {
                String s = in.readLine();
                System.out.println("message: " + s);
            } catch(IOException e){
                System.err.println("something wrong in readFromServer");
            }
        }
    }

    private static void writeToServer(){
        while(true){
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
            System.out.println("Established connection");
            System.out.println("Enter your username:");
            username = stdIn.readLine();
            out.println(username);

            new Thread(){
                @Override
                public void run(){
                    writeToServer();
                }
            }.start();
                /*new Thread(){
                    @Override
                    public void run(){
                        readFromServer();
                    }
                }.start();*/

        } catch (UnknownHostException e){
            System.err.println("Don't know about host on da port " + hostName + ":" + portNumber);
            System.exit(1);
        } catch (IOException e){
            System.err.println("I/O connection error to the " + hostName + ":" + portNumber);
            System.exit(1);
        } finally {
            out.close();
        }



    }


}
