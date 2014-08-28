/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package services;

import helpers.APP;
import helpers.SocketMessage;
import services.interfaces.TerminalServerListener;

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

    private List<TerminalServerListener> listeners;

    public static void main(String[] args) {
            new TerminalServer().start();
    }

    public TerminalServer(){
        socketOrganizer = new SocketOrganizer();
        listeners = new ArrayList<>();
    }

    // Server thread accepts incoming client connections
    class ServerAccepter extends Thread {
        int port;
        private volatile Thread myThread;
        ServerAccepter(int port) {
            this.port = port;
            myThread = this;
        }

        public void stopThread() {
            Thread tmpThread = myThread;
            myThread = null;
            if (tmpThread != null) {
                tmpThread.interrupt();
            }
        }

        public void run() {
            if (myThread == null) {
                return; // stopped before started.
            }
            try{
                ServerSocket serverSocket = new ServerSocket(port);
                while (true){
                    Socket socket = null;
                    // this blocks, waiting for a Socket to the client
                    socket = serverSocket.accept();
                    System.out.println("server: got client");

                    socketOrganizer.addSocketObject(socket);

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

    // Runs the sever accepter to catch incoming client connections
    public void start() {
        System.out.println("server: Running on port " + APP.PORT);
        serverAccepter = new ServerAccepter(APP.PORT);
        serverAccepter.start();
    }

    public void stop(){
        socketOrganizer.closeAll();
        serverAccepter.stopThread();
        System.out.println("server: stopped");
    }

    // Sends a message remotely (must be on swing thread)
    public void doSend(SocketMessage message) {
        //SocketMessage message = new SocketMessage(0, 1, new Date());
        socketOrganizer.sendToAllOutputs(message);
    }

    public class SocketOrganizer{
        private List<SocketObject> validSockets;
        private List<SocketObject> invalidSockets;

        public SocketOrganizer(){
            validSockets = new ArrayList<>();
            invalidSockets = new ArrayList<>();
        }

        public synchronized void addSocketObject(Socket socket){
            SocketObject socketObject = new SocketObject(socket);
            socketObject.addSocketObjectListener(new SocketObjectListener() {
                @Override
                public void onInputMessage(SocketObject soc) {
                    switch (soc.message.operation){
                        case SocketMessage.OPEN_TERMINAL:
                            soc.id = soc.message.terminal;
                            System.out.println("Received message statusReceived: " + String.valueOf(soc.message.received));
                            soc.message.received = true;
                            invalidSockets.remove(soc);
                            validSockets.add(soc);
                            soc.registered = true;
                            try {
                                soc.out.writeObject(soc.message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case SocketMessage.CLOSE_TERMINAL:
                            break;
                        case SocketMessage.REQUEST_CLIENT:
                            break;
                        case SocketMessage.ACCEPT_CLIENT:
                            for (TerminalServerListener l : listeners){
                                l.onTerminalMessage(soc);
                            }
                            break;
                        default:
                            break;

                    }
                }

                @Override
                public void onCloseSocket(SocketObject socketObject) {
                    if (socketObject.registered){
                        validSockets.remove(socketObject);
                    }else {
                        invalidSockets.remove(socketObject);
                    }
                    System.out.println("SocketObject with ID = " + socketObject.id + " has been removed from stack");
                }
            });
            socketObject.startInputListener();
            invalidSockets.add(socketObject);
        }

        public synchronized void removeSocketObject(int id){
            for (SocketObject soc : validSockets){
                if (soc.id == id){
                    validSockets.remove(soc);
                    return;
                }
            }
        }

        // Sends a message to all of the outgoing streams
        // Writing rarely blocks, so doing this on the swing thread is ok,
        // although could fork off a worker to do it.
        public synchronized void sendToAllOutputs(SocketMessage message) {
            System.out.println("server: send " + message);
            Iterator it = validSockets.iterator();
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

        public void closeAll(){
            for (SocketObject soc : validSockets){
                soc.close();
            }
            for (SocketObject soc : invalidSockets){
                soc.close();
            }
        }

        public class SocketObject{
            private Socket socket;
            private ObjectOutputStream out;
            private ObjectInputStream in;
            public SocketMessage message;
            private boolean registered;
            private int id;

            List<SocketObjectListener> listeners;

            private Validator validator;
            private InputListener inputListener;
            private OutputWriter outputWriter;


            private SocketObject(Socket socket) {
                try {
                    this.socket = socket;
                    this.out = new ObjectOutputStream(socket.getOutputStream());
                    //this.in = new ObjectInputStream(socket.getInputStream());
                    ReadableByteChannel channel = Channels.newChannel(socket.getInputStream());
                    this.in = new ObjectInputStream(Channels.newInputStream(channel));
                    this.registered = false;
                    validator = new Validator();

                    listeners = new ArrayList<>();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }

            private void close(){
                if (registered) {
                    inputListener.stopThread();
                    outputWriter.stopThread();
                }else{
                    validator.stopThread();
                }
                try {
                    in.close();
                    out.close();
                    socket.close();
                    System.out.println("Socket with ID = " + id + " has been closed");
                    for (SocketObjectListener l : listeners) {
                        l.onCloseSocket(this);
                    }
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

            private void validate(){
                inputListener = new InputListener();
                outputWriter = new OutputWriter();
            }

            private synchronized void transferMessage(){

                for (SocketObjectListener l : listeners){
                    l.onInputMessage(this);
                }
            }

            public synchronized void send(){
                outputWriter.stopThread();
                outputWriter = new OutputWriter();
                outputWriter.start();
            }

            @Override
            protected void finalize() throws Throwable {
                super.finalize();
                close();
            }

            private class Validator extends Thread{
                private volatile Thread myThread;

                public Validator(){
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
                        byte[] b = new byte[2];
                        int val = socket.getInputStream().read(b);
                        if (val > 0){
                            System.out.println("server: received a message. Socket ID = " + id);
                            if (b[0] == 0){

                            }else{

                            }
                            validate();
                        }else{
                            close();
                        }

                    }catch (Exception ex){
                        ex.printStackTrace();
                        close();

                    }
                }
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
                            message = (SocketMessage) in.readObject();
                            System.out.println("server: received a message. Socket ID = " + id);
                            transferMessage();

                            Thread.yield(); // let another thread have some time perhaps to stop this one.
                            if (Thread.currentThread().isInterrupted()) {
                                throw new InterruptedException("Stopped by ifInterruptedStop()");
                            }
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                        close();

                    }
                }
            }

            private class OutputWriter extends Thread{
                private volatile Thread myThread;

                public OutputWriter(){
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
                        if (message.transferable) {
                            out.writeObject(message);
                            out.flush();
                        }else{
                            //TODO: call to convertToByteArray method
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public void addTerminalServerListener(TerminalServerListener listener){
        listeners.add(listener);
    }

    private interface SocketObjectListener{
        public void onInputMessage(SocketOrganizer.SocketObject socketObject);
        public void onCloseSocket(SocketOrganizer.SocketObject socketObject);
    }
}
