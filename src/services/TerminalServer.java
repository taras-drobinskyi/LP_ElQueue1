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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by forando on 26.08.14.
 */
public class TerminalServer {

    private ServerAccepter serverAccepter;

    public SocketOrganizer socketOrganizer;

    private List<TerminalServerListener> terminalServerListeners;

    public static void main(String[] args) {
            new TerminalServer().start();
    }

    public TerminalServer(){
        socketOrganizer = new SocketOrganizer();
        terminalServerListeners = new ArrayList<>();
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
                            break;
                        case SocketMessage.CLOSE_TERMINAL:
                            break;
                        case SocketMessage.REQUEST_CLIENT:
                            for (TerminalServerListener l : terminalServerListeners){
                                l.onTerminalServerMessage(soc);
                            }
                            break;
                        case SocketMessage.ACCEPT_CLIENT:
                            for (TerminalServerListener l : terminalServerListeners){
                                l.onTerminalServerMessage(soc);
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
            socketObject.startValidator();
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

        /**
         * Sends message to sockets which IDs are specified in <b>terminals</b> list.
         * @param terminals Socket IDs for message to be sent.
         * @param operation see {@link helpers.SocketMessage#operation}.
         * @param val see {@link helpers.SocketMessage#value}.
         */
        public synchronized void send(int[] terminals, int operation, int val){
            int itemsInArray = validSockets.size();
            for (int terminal : terminals) {
                for (int i=0; i<itemsInArray; i++) {
                    SocketObject soc = validSockets.get(i);
                    if (soc.id == terminal) {
                        soc.message.operation = operation;
                        soc.message.value = val;
                        soc.message.received = true;
                        soc.send();
                        i = itemsInArray;
                    }
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

            private void addSocketObjectListener(SocketObjectListener listener){
                listeners.add(listener);
            }

            /**
             * This method registers the current object with an <b>ID</b> (equal to terminal index).
             * After that, the object becomes valid to {@link services.TerminalServer.SocketOrganizer}.
             * @param clientTalksWithObject Can be <b>1</b> - if a client talks to this socket
             *                         using {@link helpers.SocketMessage} object, or <b>0</b> - if it doesn't.
             * @param id An ID to register this object with.
             */
            private void validate(byte clientTalksWithObject, byte id){
                switch (clientTalksWithObject){
                    case 0x01:
                        message = new SocketMessage(id,SocketMessage.OPEN_TERMINAL,0, new Date(),true);
                        break;
                    default:
                        message = new SocketMessage(id,SocketMessage.OPEN_TERMINAL,0, new Date(),false);
                        break;
                }
                transferMessage();
                inputListener = new InputListener();
                inputListener.start();
                outputWriter = new OutputWriter();
                message.received = true;
                outputWriter.start();
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

            public void startValidator() {
                validator.start();
            }

            /**
             * The object of this class receives the very first <b>message</b> from a client (socket).<br>
             * The goal of this <b>message</b> is to notify the server whether the client is talking
             * using {@link helpers.SocketMessage} object or raw byteArray.<br>
             * It also provides {@link SocketObject} with an ID, thus
             * making it completely registered to the server.<br>
             * Once these two points done, the object becomes useless.
             */
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
                        byte[] b = new byte[100];
                        int val = socket.getInputStream().read(b);
                        System.out.println("val = " + val);
                        if (val > 0){
                            System.out.println("b.length = " + b.length);
                            System.out.println("b[0] = " + (int)b[0]);
                            System.out.println("b[1] = " + (int)b[1]);
                            validate(b[2], b[3]);
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

                private byte[] convertToByteArray(){
                    byte[] rawMessage = new byte[4];
                    rawMessage[0] = (byte)message.terminal;
                    rawMessage[1] = (byte)message.operation;
                    rawMessage[2] = (byte)message.value;
                    if(message.received){
                        rawMessage[3] = 0x01;
                    }else {
                        rawMessage[3] = 0;
                    }
                    return rawMessage;
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
                            byte[] buffer = convertToByteArray();
                            out.write(buffer);
                            out.flush();
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public void addTerminalServerListener(TerminalServerListener listener){
        terminalServerListeners.add(listener);
    }

    private interface SocketObjectListener{
        public void onInputMessage(SocketOrganizer.SocketObject socketObject);
        public void onCloseSocket(SocketOrganizer.SocketObject socketObject);
    }
}
