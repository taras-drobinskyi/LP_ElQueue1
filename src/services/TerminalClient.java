/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package services;

import helpers.APP;
import helpers.SocketMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by forando on 27.08.14.
 */
public class TerminalClient {

    private final String hostName;
    private final int port;
    private final int terminalIndex;

    private Validator validator;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    InputListener inputListener;
    OutputWriter outputWriter;

    public SocketMessage message;

    private boolean registered = false;

    private List<TerminalClientListener> listeners;

    public static void main(String[] args) {
        new TerminalClient(APP.IP, APP.PORT, 4).startClient();
    }

    public void addTerminalClientListener(TerminalClientListener listener){
        listeners.add(listener);
    }

    public TerminalClient(String hostName, int port, int terminalIndex){
        this.hostName = hostName;
        this.port = port;
        this.terminalIndex = terminalIndex;
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
            out.close();
            socket.close();
            System.out.println("Socket with ID = " + terminalIndex + " has been closed");
            for (TerminalClientListener l : listeners) {
                l.onCloseSocket();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void validate(SocketMessage message){
        this.message = message;
        registered = true;
        for (TerminalClientListener l : listeners) {
            l.onRegister();
        }
        inputListener = new InputListener();
        inputListener.start();
        outputWriter = new OutputWriter();
        //outputWriter.start();
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
                socket = new Socket(hostName, port);
                out = new ObjectOutputStream(socket.getOutputStream());
                System.out.println("client: connected!");

                byte[] buffer = {0x01, (byte)terminalIndex};
                out.write(buffer);
                out.flush();

                // get input stream to read from server, wrap in object stream
                ReadableByteChannel channel = Channels.newChannel(socket.getInputStream());
                in = new ObjectInputStream(Channels.newInputStream(channel));

                // get object from server, will block until object arrives.
                SocketMessage message = (SocketMessage) in.readObject();
                System.out.println("Validator received message OPERATION = " + message.operation);
                if (message.operation == SocketMessage.OPEN_TERMINAL && message.received){
                    validate(message);
                }else {
                    close();
                }

            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    private synchronized void transferMessage(){

        for (TerminalClientListener l : listeners){
            l.onInputMessage();
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
                    System.out.println("server: received a message. OPERATION = " + message.operation
                    + " VALUE = " + message.value);
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

    public synchronized void send(){
        outputWriter.stopThread();
        outputWriter = new OutputWriter();
        outputWriter.start();
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

    public interface TerminalClientListener{
        public void onRegister();
        public void onInputMessage();
        public void onCloseSocket();
    }
}
