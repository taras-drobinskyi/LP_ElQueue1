/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import java.io.*;
import java.net.Socket;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by forando on 27.08.14.
 */
public class ClientServer {

    private final String hostName;
    private final int port;
    private int id;
    private int type;

    private Validator validator;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    /*InputListener input;
    OutputWriter output;*/
    InPut inPut;
    OutPut output;

    int counter = 0;

    //public SocketMessage message;

    //public Object object;

    private boolean registered = false;

    private List<ClientServerListener> listeners;

    public static void main(String[] args) {
        new ClientServer(APP.IP, APP.PORT, SocketMessage.DISPLAY, 0).startClient();
    }

    public void addClientServerListener(ClientServerListener listener){
        listeners.add(listener);
    }

    public ClientServer(String hostName, int port, int type, int id){
        this.hostName = hostName;
        this.port = port;
        this.type = type;
        this.id = id;
        listeners = new ArrayList<>();
        validator = new Validator();
    }

    // Runs a client handler to connect to a server
    public void startClient() {
        validator.start();
    }

    public void stopClient(){close();}

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
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
            //out.close();
            socket.close();
            System.out.println("Socket with ID = " + id + " has been closed");
            for (ClientServerListener l : listeners) {
                l.onCloseSocket();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void register(int id){
        registered = true;
        if (this.id != id){
            this.id = id;
        }
        for (ClientServerListener l : listeners) {
            l.onRegister(id);
        }
    }

    private void validate(Socket soc){
        this.socket = soc;
        try {
            /*ReadableByteChannel channel = Channels.newChannel(this.socket.getInputStream());
            this.in = new ObjectInputStream(Channels.newInputStream(channel));*/
            inPut = new InPut(socket, id);
            inPut.addInputListener(new InPut.InputListener() {
                @Override
                public void onMessage(Object messageObject) {
                    transferMessage(messageObject);
                }

                @Override
                public void onClose() {
                    close();
                }
            });
            inPut.start();
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                // make connection to the server hostName/port
                Socket socket = new Socket(hostName, port);

                System.out.println("client: connected!");

                byte[] outBuffer = {0x01, (byte)type, (byte) id};

                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                output.write(outBuffer);
                output.flush();

                ReadableByteChannel channel = Channels.newChannel(socket.getInputStream());
                ObjectInputStream input = new ObjectInputStream(Channels.newInputStream(channel));

                byte[] inputBuffer = new byte[2];
                int val = input.read(inputBuffer);
                if (val > 0 && inputBuffer[0]==0x01 && inputBuffer[1]>=0){
                    register(inputBuffer[1]);
                    System.out.println("Validator: client registered with ID = " + inputBuffer[1]);
                    validate(socket);
                }else{
                    close();
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    private synchronized void transferMessage(Object object){

        for (ClientServerListener l : listeners){
            l.onInputMessage(object);
        }
    }

    private synchronized void transferMessage1(Object object){
        counter++;
        if (counter<10) {
            send(new DisplayMessage(0, 201, null, 0, new Date(), true));
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
                    Object object = in.readObject();
                    System.out.println("ClientServer: onInputMessage socketID = " + id +
                            " operation = " + ((SocketMessage) object).operation);
                    transferMessage(object);

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

    public synchronized void send(Object messageObject){
        if (output != null){
            output.stopThread();
            output = null;
        }
        output = new OutPut(out, id, messageObject);
        output.start();
    }

    private class OutputWriter extends Thread{
        private volatile Thread myThread;
        private Object object;

        public OutputWriter(Object object){
            myThread = this;
            this.object = object;
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
                System.out.println("ClientServer: onSendMessage socketID = " + id +
                        " operation = " + ((SocketMessage) object).operation);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeUnshared(this.object);
                out.flush();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public interface ClientServerListener {
        public void onRegister(int id);
        public void onInputMessage(Object object);
        public void onCloseSocket();
    }
}
