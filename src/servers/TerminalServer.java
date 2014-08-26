/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package servers;

import helpers.SocketMessage;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by forando on 26.08.14.
 */
public class TerminalServer {

    private ServerAccepter serverAccepter;

    private SocketOrganizer socketOrganizer;

    public TerminalServer(){
    }

    // Server thread accepts incoming client connections
    class ServerAccepter extends Thread {
        int port;
        ServerAccepter(int port) {
            this.port = port;
        }

        public void run() {
            try{
                ServerSocket serverSocket = new ServerSocket(port);
                while (true){
                    Socket socket = null;
                    // this blocks, waiting for a Socket to the client
                    socket = serverSocket.accept();
                    System.out.println("server: got client");

                    socketOrganizer.addSocketObject(socket);
                }
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }

    // Runs the sever accepter to catch incoming client connections
    public void doServer() {
        System.out.println("server: start");
        serverAccepter = new ServerAccepter(8000);
        serverAccepter.start();
    }

    // Sends a message remotely (must be on swing thread)
    public void doSend() {
        SocketMessage message = new SocketMessage(0, 1, new Date());
        socketOrganizer.sendToAllOutputs(message);
    }

    private class SocketOrganizer{
        private List<SocketObject> sockets;
        private int lastID = 0;

        public SocketOrganizer(){
            sockets = new ArrayList<>();
        }

        public synchronized void addSocketObject(Socket socket){
            lastID++;
            sockets.add(new SocketObject(socket, lastID));
        }

        public synchronized void removeSocketObject(int id){
            for (SocketObject soc : sockets){
                if (soc.id == id){
                    sockets.remove(soc);
                    return;
                }
            }
        }

        // Sends a message to all of the outgoing streams
        // Writing rarely blocks, so doing this on the swing thread is ok,
        // although could fork off a worker to do it.
        public synchronized void sendToAllOutputs(SocketMessage message) {
            System.out.println("server: send " + message);
            Iterator it = sockets.iterator();
            while (it.hasNext()) {
                SocketObject socketObj = (SocketObject) it.next();
                try {
                    // writeUnshared() is like writeObject(), but always writes a new copy of the object.
                    socketObj.out.writeUnshared(message);
                    //The flush forces the bytes out now
                    socketObj.out.flush();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    it.remove(); // cute! -- drop that socket if have probs with it
                }
            }
        }

        private class SocketObject{
            private ObjectOutputStream out;
            private ObjectInputStream in;
            private int id;


            private SocketObject(Socket socket, int id) {
                try {
                    this.out = new ObjectOutputStream(socket.getOutputStream());
                    this.in = new ObjectInputStream(socket.getInputStream());
                    this.id = id;
                }catch(IOException ex){
                    ex.printStackTrace();
                }
            }

            private class inputListener extends Thread{
                @Override
                public void run() {
                    try {
                        while (true) {
                            // get object from server, will block until object arrives.
                            SocketMessage message = (SocketMessage) in.readObject();
                            System.out.println("server: received a message. Socket ID = " + id);
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}
