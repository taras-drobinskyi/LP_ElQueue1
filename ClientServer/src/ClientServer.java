/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import java.io.*;
import java.net.Socket;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
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
    //private ObjectOutputStream out;

    InputListener inputListener;
    OutputWriter outputWriter;

    //public SocketMessage message;

    //public Object object;

    private boolean registered = false;

    private List<ClientServerListener> listeners;

    public static void main(String[] args) {
        new ClientServer(APP.IP, APP.PORT, 0, 2).startClient();
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
            inputListener.stopThread();
            outputWriter.stopThread();
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


    public void validate(int id){
        registered = true;
        if (this.id != id){
            this.id = id;
        }
        for (ClientServerListener l : listeners) {
            l.onRegister(id);
        }
    }

    private void initStreams(Socket soc){
        this.socket = soc;
        try {
            ReadableByteChannel channel = Channels.newChannel(this.socket.getInputStream());
            this.in = new ObjectInputStream(Channels.newInputStream(channel));
            inputListener = new InputListener();
            inputListener.start();
        } catch (IOException e) {
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

                byte[] inputBuffer = new byte[10];
                int val = input.read(inputBuffer);
                if (val > 0 && inputBuffer[0]==0x01 && inputBuffer[1]>=0){
                    validate(inputBuffer[1]);
                    System.out.println("Validator: client registered with ID = " + inputBuffer[1]);
                    initStreams(socket);
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

    public synchronized void send(Object object){
        outputWriter.stopThread();
        outputWriter = new OutputWriter(object);
        outputWriter.start();
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
