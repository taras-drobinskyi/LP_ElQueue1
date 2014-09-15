/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
public class HostServer {

    private ServerAccepter serverAccepter;

    public SocketOrganizer socketOrganizer;

    private List<HostServerListener> hostServerListeners;

    public static void main(String[] args) {
            new HostServer().start();
    }

    public HostServer(){
        socketOrganizer = new SocketOrganizer();
        hostServerListeners = new ArrayList<>();
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
        private List<SocketObject> displays;
        private List<SocketObject> terminals;
        private List<SocketObject> printers;
        private List<SocketObject> invalidSockets;

        private volatile boolean isOnHoldTerminals = false;

        public SocketOrganizer(){
            displays = new ArrayList<>();
            terminals = new ArrayList<>();
            printers = new ArrayList<>();
            invalidSockets = new ArrayList<>();
        }

        public synchronized void addSocketObject(Socket socket){
            SocketObject socketObject = new SocketObject(socket);
            socketObject.addSocketObjectListener(new SocketObjectListener() {
                @Override
                public void onRegister(SocketObject soc) {
                    switch (soc.type){
                        case SocketMessage.DISPLAY:
                            invalidSockets.remove(soc);
                            displays.add(soc);
                            soc.registered = true;
                            if (soc.id < 0 ){
                                soc.id = displays.size() - 1;
                            }
                            System.out.println("onRegister socket.type = DISPLAY");
                            break;
                        case SocketMessage.TERMINAL:
                            invalidSockets.remove(soc);
                            terminals.add(soc);
                            soc.registered = true;
                            System.out.println("onRegister socket.type = TERMINAL");
                            break;
                        case SocketMessage.PRINTER:
                            invalidSockets.remove(soc);
                            printers.add(soc);
                            soc.registered = true;
                            if (soc.id < 0 ){
                                soc.id = printers.size() - 1;
                            }
                            System.out.println("onRegister socket.type = TERMINAL");
                            break;
                        default:
                            soc.close();
                            invalidSockets.remove(soc);
                            break;
                    }
                }

                @Override
                public void onInputMessage(SocketObject soc) {
                    switch (soc.message.operation){
                        case SocketMessage.REGISTER_SOCKET:
                            /*soc.id = soc.message.terminal;
                            System.out.println("Received message statusReceived: " + String.valueOf(soc.message.received));
                            soc.message.received = true;*/
                            invalidSockets.remove(soc);
                            displays.add(soc);
                            soc.registered = true;
                            break;
                        case SocketMessage.CLOSE_TERMINAL:
                            break;
                        case SocketMessage.REQUEST_CLIENT:
                            for (HostServerListener l : hostServerListeners){
                                l.onTerminalServerMessage(soc);
                            }
                            break;
                        case SocketMessage.ACCEPT_CLIENT:
                            for (HostServerListener l : hostServerListeners){
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
                        displays.remove(socketObject);
                    }else {
                        invalidSockets.remove(socketObject);
                    }
                    System.out.println("SocketObject with ID = " + socketObject.id + " has been removed from stack");
                }
            });
            socketObject.startValidator();
            invalidSockets.add(socketObject);
        }

        public synchronized void removeSocketObject(SocketObject soc){
            switch (soc.type){
                case SocketMessage.DISPLAY:
                    displays.remove(soc);
                    System.out.println("removeSocketObject socket.type = DISPLAY");
                    break;
                case SocketMessage.TERMINAL:
                    terminals.remove(soc);
                    System.out.println("removeSocketObject socket.type = TERMINAL");
                    break;
                case SocketMessage.PRINTER:
                    printers.remove(soc);
                    System.out.println("removeSocketObject socket.type = PRINTER");
                    break;
                default:
                    invalidSockets.remove(soc);
                    System.out.println("removeSocketObject socket.type = invalidSockets");
                    break;
            }
        }

        /**
         * Sends message to sockets which IDs are specified in <b>terminals</b> list.
         * @param terminals Socket IDs for message to be sent.
         * @param operation see {@link SocketMessage#operation}.
         * @param val see {@link SocketMessage#value}.
         */
        public synchronized void send(int[] terminals, int operation, int val){
            int itemsInArray = displays.size();
            for (int terminal : terminals) {
                for (int i=0; i<itemsInArray; i++) {
                    SocketObject soc = displays.get(i);
                    if (soc.id == terminal) {
                        soc.message.operation = operation;
                        soc.message.value = val;
                        soc.message.received = true;
                        soc.send();
                        i = itemsInArray;
                    }
                }
            }
            if (operation==SocketMessage.HOLD_TERMINAL){
                if (val == 1) {
                    isOnHoldTerminals = true;
                    System.out.println("server isOnHoldTerminals = " + isOnHoldTerminals);
                }else{
                    isOnHoldTerminals = false;
                    System.out.println("server isOnHoldTerminals = " + isOnHoldTerminals);
                }
            }
        }

        // Sends a message to all of the outgoing streams
        // Writing rarely blocks, so doing this on the swing thread is ok,
        // although could fork off a worker to do it.
        public synchronized void sendToAllOutputs(SocketMessage message) {
            System.out.println("server: send " + message);
            Iterator it = displays.iterator();
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
            for (SocketObject soc : displays){
                soc.close();
            }
            for (SocketObject soc : invalidSockets){
                soc.close();
            }
        }

        public class SocketObject{

            /**
             * Describes the type of a client, that is connected to this socket<br>
             *     can be: <ul>
             *         <li>{@link SocketMessage#DISPLAY}</li>
             *         <li>{@link SocketMessage#TERMINAL}</li>
             *         <li>{@link SocketMessage#PRINTER}</li>
             *     </ul>
             */
            private int type;

            private Socket socket;
            private ObjectOutputStream out;
            private ObjectInputStream in;
            public SocketMessage message;
            private boolean registered;
            private int id = -1;

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
             * This method registers the current object with an <b>ID</b>.
             * After that, the object becomes valid to {@link HostServer.SocketOrganizer}.
             * @param clientTalksWithObject Can be <b>1</b> - if a client talks to this socket
             *                         using {@link SocketMessage} object, or <b>0</b> - if it doesn't.
             * @param type The client type (see {@link HostServer.SocketOrganizer.SocketObject#type}).
             * @param id An ID to register this object with. If it's == -1, than id must be provided by server.
             */
            private void validate(byte clientTalksWithObject, byte type, byte id){
                /*switch (clientTalksWithObject){
                    case 0x01:
                        message = new SocketMessage(id,SocketMessage.REGISTER_SOCKET,0, new Date(),true);
                        break;
                    default:
                        message = new SocketMessage(id,SocketMessage.REGISTER_SOCKET,0, new Date(),false);
                        break;
                }*/
                if (id>=0){
                    this.id = id;
                }
                register();
                inputListener = new InputListener();
                inputListener.start();
                /*outputWriter = new OutputWriter();
                message.received = true;
                System.out.println("validate isOnHoldTerminals = " + isOnHoldTerminals);
                if (isOnHoldTerminals){
                    message.operation = SocketMessage.HOLD_TERMINAL;
                    message.value = 1;
                }
                outputWriter.start();*/
            }

            private void register(){

                for (SocketObjectListener l : listeners){
                    l.onRegister(this);
                }
            }

            private synchronized void transferMessage(){

                for (SocketObjectListener l : listeners){
                    l.onInputMessage(this);
                }
            }

            public synchronized void send(){
                System.out.println("Socket send OPERATION = " + message.operation);
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
             * using {@link SocketMessage} object or raw byteArray.<br>
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
                        byte[] b = new byte[5];
                        ReadableByteChannel channel = Channels.newChannel(socket.getInputStream());
                        int val = Channels.newInputStream(channel).read(b);
                        //int val = socket.getInputStream().read(b);
                        System.out.println("val = " + val);
                        int bytesNumber = b[1];
                        if (val > 0 && bytesNumber == 3){
                            validate(b[2], b[3], b[4]);
                            byte[] buffer = {0x01, (byte)id};
                            OutputStream outStream = socket.getOutputStream();
                            outStream.write(buffer);
                            outStream.flush();
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
                            System.out.println("Socket OUT write message with OPERATION = " + message.operation +
                            " and VALUE = " + message.value);
                            //out.writeObject(message);
                            out.writeUnshared(message);
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

    public void addHostServerListener(HostServerListener listener){
        hostServerListeners.add(listener);
    }

    private interface SocketObjectListener{
        public void onRegister(SocketOrganizer.SocketObject socketObject);
        public void onInputMessage(SocketOrganizer.SocketObject socketObject);
        public void onCloseSocket(SocketOrganizer.SocketObject socketObject);
    }
}
