/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import sockets.SocketMessage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by forando on 03.10.14.
 */
public class ClientConnector implements ClientServer.ClientServerListener {
    ClientServer client;

    private volatile Thread myThread;
    ClientServer.ClientServerListener myListener;
    ClientServer.ClientServerListener clientListener;
    int type;
    int id;
    ClientConnectorListener listener;

    private int restartsQuant = 0;
    private int delay = 500;
    private Timer restartTimer;


    public ClientConnector(ClientServer.ClientServerListener clientListener, int type, int id) {
        this.clientListener = clientListener;
        this.type = type;
        this.id = id;
        //this.myThread = this;
        this.myListener = this;
        startClientServer();
        restartsQuant++;
    }

    private void startClientServer(){
        //if (client != null) client = null;
        System.out.println(restartsQuant + " Attempt to get connected to the Server!!!");
        client = new ClientServer(APP.IP, APP.PORT, type, id);
        client.addClientServerListener(myListener);
        //client.addClientServerListener(clientListener);
        client.startClient();
    }

    private void restartClientServer(){
        restartsQuant++;
        delay = delay*2;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                    startClientServer();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void stopThread() {
        Thread tmpThread = myThread;
        myThread = null;
        if (tmpThread != null) {
            tmpThread.interrupt();
        }
    }

    /*@Override
    public void run() {
        if (myThread == null) {
            return; // stopped before started.
        }
        startClientServer();
        restartsQuant++;
    }*/

    @Override
    public void onRegister(int id) {
        client.removeClientServerListener(myListener);
        client.addClientServerListener(clientListener);
        this.listener.onClientConnected(this.client);
    }

    @Override
    public void onInputMessage(Object object) {

    }

    @Override
    public void onCloseSocket() {
        restartClientServer();

    }

    public void addClientConnectorListener(ClientConnectorListener listener) throws Exception {
        if (this.listener != null) {
            throw new Exception("The ClientConnectorListener is already assigned!");
        }
        this.listener = listener;
    }

    public interface ClientConnectorListener{
        public void onClientConnected(ClientServer client);
    }
}
