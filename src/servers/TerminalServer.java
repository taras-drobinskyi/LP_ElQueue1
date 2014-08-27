/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package servers;

import helpers.SocketMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
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
        /*SocketMessage message = new SocketMessage(0, 1, new Date());
        socketOrganizer.sendToAllOutputs(message);*/
    }

    private class SocketOrganizer{
        private List<SocketObject> sockets;

        public SocketOrganizer(){
            sockets = new ArrayList<>();
        }

        public synchronized void addSocketObject(Socket socket){
            SocketObject socketObject = new SocketObject(socket);
            socketObject.addSocketObjectListener(new SocketObjectListener() {
                @Override
                public void onInputMessage(SocketObject soc) {
                    switch (soc.message.operation){
                        case 0:
                            soc.id = soc.message.terminal;
                            sockets.add(soc);
                            break;
                        default:
                            break;

                    }
                }
            });
            socketObject.startInputListener();
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
            private Socket socket;
            private ObjectOutputStream out;
            private ObjectInputStream in;
            private SocketMessage message;
            private int id;

            List<SocketObjectListener> listeners;

            private InputListener inputListener;


            private SocketObject(Socket socket) {
                try {
                    this.socket = socket;
                    this.out = new ObjectOutputStream(socket.getOutputStream());
                    //this.in = new ObjectInputStream(socket.getInputStream());
                    ReadableByteChannel channel = Channels.newChannel(socket.getInputStream());
                    this.in = new ObjectInputStream(Channels.newInputStream(channel));
                    inputListener = new InputListener();

                    listeners = new ArrayList<>();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }

            private void close(){
                try {
                    inputListener.stopThread();
                    in.close();
                    out.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            private void startInputListener(){
                inputListener.start();
            }

            private void addSocketObjectListener(SocketObjectListener listener){
                listeners.add(listener);
            }

            private synchronized void transferMessage(SocketMessage message){

                this.message = message;

                for (SocketObjectListener l : listeners){
                    l.onInputMessage(this);
                }
            }

            @Override
            protected void finalize() throws Throwable {
                super.finalize();
                close();
            }

            private class InputListener extends Thread{
                private volatile Thread myThread;

                public InputListener(){
                    myThread = this;
                }

                public void stopThread() {
                    Thread tmpThread = myThread;
                    myThread = null;
                    if (tmpThread != null) {
                        tmpThread.interrupt();
                    }
                }

                @Override
                public void run() {
                    if (myThread == null) {
                        return; // stopped before started.
                    }
                    try {
                        while (true) {
                            //get object from server, will block until object arrives.
                            SocketMessage message = (SocketMessage) in.readObject();
                            System.out.println("server: received a message. Socket ID = " + id);
                            transferMessage(message);

                            Thread.yield(); // let another thread have some time perhaps to stop this one.
                            if (Thread.currentThread().isInterrupted()) {
                                throw new InterruptedException("Stopped by ifInterruptedStop()");
                            }
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();

                    }
                }
            }
        }
    }
    private interface SocketObjectListener{
        public void onInputMessage(SocketOrganizer.SocketObject socketObject);
    }
}
