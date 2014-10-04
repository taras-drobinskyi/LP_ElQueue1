package sockets;/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by forando on 01.10.14.
 */
public class InPut extends Thread {
    private volatile Thread myThread;
    private Socket socket;
    private ObjectInputStream in;
    private int id;

    List<InputListener> listeners;

    public InPut(Socket socket, int id) {
        myThread = this;
        this.socket = socket;
        this.id = id;
        listeners = new ArrayList<>();
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
            //this.in = new ObjectInputStream(this.socket.getInputStream());
            ReadableByteChannel channel = Channels.newChannel(socket.getInputStream());
            this.in = new ObjectInputStream(Channels.newInputStream(channel));
            while (true) {
                //get object from server, will block until object arrives.
                Object messageObject = in.readObject();
                System.out.println("Socket: onInputMessage socketID = " + id +
                        " operation = " + ((SocketMessage) messageObject).operation);
                for (InputListener l : listeners){
                    l.onMessage(messageObject);
                }

                Thread.yield(); // let another thread have some time perhaps to stop this one.
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException("Socket: Stopped by ifInterruptedStop()");
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            for (InputListener l : listeners){
                l.onClose();
            }

        }
    }

    public void addInputListener(InputListener listener){
        listeners.add(listener);
    }

    public interface InputListener{
        void onMessage(Object messageObject);
        void onClose();
    }
}
