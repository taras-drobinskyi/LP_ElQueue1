/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import java.io.*;
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
                    addSocketObject(soc);
                }

                @Override
                public void onInputMessage(SocketObject soc) {
                    transferMessage(soc);
                }

                @Override
                public void onCloseSocket(SocketObject soc) {
                    removeSocketObject(soc);
                }
            });
            socketObject.startValidator();
            invalidSockets.add(socketObject);
        }

        private synchronized void addSocketObject(SocketObject soc){
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
                    System.out.println("onRegister socket.type = PRINTER");
                    for (HostServerListener l : hostServerListeners){
                        l.onPrinterAvailable(soc);
                    }
                    break;
                default:
                    soc.close();
                    invalidSockets.remove(soc);
                    break;
            }
        }

        private void transferMessage(SocketObject soc){
            switch (soc.type){
                case SocketMessage.DISPLAY:
                    for (HostServerListener l : hostServerListeners){
                                l.onDisplayMessage(soc);
                    }
                    break;
                case SocketMessage.TERMINAL:
                            /*for (HostServerListener l : hostServerListeners){
                                l.onTerminalServerMessage(soc);
                            }*/
                    break;
                case SocketMessage.PRINTER:
                            /*for (HostServerListener l : hostServerListeners){
                                l.onTerminalServerMessage(soc);
                            }*/
                    break;
                default:
                    break;

            }
        }

        private synchronized void removeSocketObject(SocketObject soc){
            switch (soc.type){
                case SocketMessage.DISPLAY:
                    displays.remove(soc);
                    System.out.println("SocketObject TYPE =" +
                            " DISPLAY with ID = " + soc.id + " has been removed from stack");
                    break;
                case SocketMessage.TERMINAL:
                    terminals.remove(soc);
                    System.out.println("SocketObject TYPE =" +
                            " TERMINAL with ID = " + soc.id + " has been removed from stack");
                    break;
                case SocketMessage.PRINTER:
                    printers.remove(soc);
                    System.out.println("SocketObject TYPE =" +
                            " PRINTER with ID = " + soc.id + " has been removed from stack");
                    break;
                default:
                    invalidSockets.remove(soc);
                    System.out.println("SocketObject TYPE = invalidSockets has been removed from stack");
                    break;
            }
        }

        /**
         * Sends message to sockets <b>type</b>={@link SocketMessage#TERMINAL} which
         * IDs are specified in <b>idArr</b> list.
         * @param idArr Socket IDs for message to be sent.
         * @param message {@link java.lang.Object}
         */
        public synchronized void sendTerminals(int[] idArr, Object message){
            int itemsInArray = terminals.size();
            for (int terminal : idArr) {
                for (int i=0; i<itemsInArray; i++) {
                    SocketObject soc = terminals.get(i);
                    if (soc.id == terminal) {
                        //todo:
                        //soc.send(message);
                        i = itemsInArray;//exiting the loop
                    }
                }
            }
            /*if (operation==SocketMessage.HOLD_TERMINAL){
                if (val == 1) {
                    isOnHoldTerminals = true;
                    System.out.println("server isOnHoldTerminals = " + isOnHoldTerminals);
                }else{
                    isOnHoldTerminals = false;
                    System.out.println("server isOnHoldTerminals = " + isOnHoldTerminals);
                }
            }*/
        }

        /**
         * Sends message to sockets <b>type</b>={@link SocketMessage#DISPLAY} which
         * IDs are specified in <b>idArr</b> list.
         * @param idArr Socket IDs for message to be sent.
         * @param message {@link java.lang.Object}
         */
        public synchronized void sendDisplays(int[] idArr, Object message){
            int itemsInArray = displays.size();
            for (int terminal : idArr) {
                for (int i=0; i<itemsInArray; i++) {
                    SocketObject soc = displays.get(i);
                    if (soc.id == terminal) {
                        //todo:
                        soc.send(message);
                        i = itemsInArray;//exiting the loop
                    }
                }
            }
        }

        /**
         * Sends message to sockets <b>type</b>={@link SocketMessage#PRINTER} which
         * IDs are specified in <b>idArr</b> list.
         * @param idArr Socket IDs for message to be sent.
         * @param message {@link java.lang.Object}
         */
        public synchronized void sendPrinters(int[] idArr, Object message){
            int itemsInArray = printers.size();
            for (int terminal : idArr) {
                for (int i=0; i<itemsInArray; i++) {
                    SocketObject soc = printers.get(i);
                    if (soc.id == terminal) {
                        //todo:
                        //soc.send(message);
                        i = itemsInArray;//exiting the loop
                    }
                }
            }
        }

        // Sends a message to all of the outgoing streams
        // Writing rarely blocks, so doing this on the swing thread is ok,
        // although could fork off a worker to do it.
        public synchronized void sendToAllOutputs(Object object) {
            Iterator it = displays.iterator();
            while (it.hasNext()) {
                SocketObject socketObj = (SocketObject) it.next();
                try {
                    //todo:
                    //socketObj.send(object);
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

        public int[] getDisplaysIDs(){
            int[] idList;
            if(displays != null && displays.size()>0){
                idList = new int[displays.size()];
                for (int i=0; i<displays.size(); i++){
                    idList[i] = displays.get(i).id;
                }
                return idList;
            }else {
                return null;
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
            public int type;
            public Object message;
            public int id = -1;

            private Socket socket;
            //private InputStream iStream;
            private ObjectOutputStream out;
            private ObjectInputStream in;
            //public SocketMessage message;
            private boolean registered;

            List<SocketObjectListener> listeners;

            private Validator validator;
            private InPut inPut;
            private OutPut output;
            int counter = 0;


            private SocketObject(Socket socket) {
                try {
                    this.socket = socket;
                    //this.iStream = socket.getInputStream();
                    this.registered = false;
                    validator = new Validator();
                    listeners = new ArrayList<>();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }

            private void close(){
                if (registered) {
                    inPut.stopThread();
                    output.stopThread();
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
             * This method notifies the {@link Host} that the socket is now available.<br>
             *     It also inits input listener for the socket
             * @param clientTalksWithObject Can be <b>1</b> - if a client talks to this
             *                              socket using {@link SocketMessage} object, or <b>0</b> - if it doesn't.
             */
            private void validate(byte clientTalksWithObject){
                /*switch (clientTalksWithObject){
                    case 0x01:
                        message = new SocketMessage(id,SocketMessage.REGISTER_SOCKET,0, new Date(),true);
                        break;
                    default:
                        message = new SocketMessage(id,SocketMessage.REGISTER_SOCKET,0, new Date(),false);
                        break;
                }*/

                try {
                    //this.in = new ObjectInputStream(socket.getInputStream());
                    /*input = new InputListener(this.socket);
                    input.start();*/
                    inPut = new InPut(socket, id);
                    inPut.addInputListener(new InPut.InputListener() {
                        @Override
                        public void onMessage(Object messageObject) {
                            message = messageObject;
                            transferMessage();
                        }

                        @Override
                        public void onClose() {
                            close();
                        }
                    });
                    inPut.start();
                    out = new ObjectOutputStream(socket.getOutputStream());
                    for (HostServerListener l : hostServerListeners){
                        l.onDisplayAvailable(this);
                    }
                    //send(new DisplayMessage(0, 202, null, 0, new Date(), true));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                /*System.out.println("validate isOnHoldTerminals = " + isOnHoldTerminals);
                if (isOnHoldTerminals){
                    message.operation = SocketMessage.HOLD_TERMINAL;
                    message.value = 1;
                }*/
            }

            /**
             * This method registers the current object with an <b>ID</b>.
             * After that, the object becomes valid to {@link HostServer.SocketOrganizer}.
             * @param type The client type (see {@link HostServer.SocketOrganizer.SocketObject#type}).
             * @param id An ID to register this object with. If it's == -1, than id must be provided by server.
             */
            private void register(byte type, byte id){

                this.type = type;
                if (id>=0){
                    this.id = id;
                }
                for (SocketObjectListener l : listeners){
                    l.onRegister(this);
                }
            }

            private synchronized void transferMessage(){

                for (SocketObjectListener l : listeners){
                    l.onInputMessage(this);
                }
            }

            private synchronized void transferMessage1(){
                counter++;
                if (counter<10) {
                    send(new DisplayMessage(0, 202, null, 0, new Date(), true));
                }
            }

            public synchronized void send(Object messageObject){
                if (output != null){
                    output.stopThread();
                }
                output = new OutPut(out, id, messageObject);
                output.start();
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
                        byte[] b = new byte[3];
                        //ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                        ReadableByteChannel channel = Channels.newChannel(socket.getInputStream());
                        ObjectInputStream input = new ObjectInputStream(Channels.newInputStream(channel));
                        int val = input.read(b);
                        //int val = socket.getInputStream().read(b);
                        if (val > 0){
                            register(b[1], b[2]);
                            byte[] buffer = {0x01, (byte)id};
                            //byte[] buffer = {0x01, 0x00};
                            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                            output.write(buffer);
                            output.flush();

                            validate(b[0]);
                        }else{
                            close();
                        }

                    }catch (Exception ex){
                        ex.printStackTrace();
                        close();

                    }
                }
            }
        }
    }

    private interface SocketObjectListener{
        public void onRegister(SocketOrganizer.SocketObject socketObject);
        public void onInputMessage(SocketOrganizer.SocketObject socketObject);
        public void onCloseSocket(SocketOrganizer.SocketObject socketObject);
    }

    public void addHostServerListener(HostServerListener listener){
        hostServerListeners.add(listener);
    }

    public interface HostServerListener{
        public void onPrinterAvailable(SocketOrganizer.SocketObject soc);
        public void onPrinterMessage(SocketOrganizer.SocketObject soc);
        public void onTerminalMessage(SocketOrganizer.SocketObject soc);
        public void onDisplayAvailable(SocketOrganizer.SocketObject soc);
        public void onDisplayMessage(SocketOrganizer.SocketObject soc);
    }


}
