package client;/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import main.APP;

/**
 * Created by forando on 03.10.14.
 * The class provides an app with a {@link ClientServer} object to be able to talk to HostServer
 */
public class ClientConnectorProvider implements ClientServer.ClientServerListener {
    ClientServer client;

    private volatile Thread myThread;
    ClientServer.ClientServerListener thisThreadClientServerListener;
    ClientServer.ClientServerListener clientListener;
    int type;
    int id;
    ClientConnectorListener listener;
    /**
     * The quantity of restart attempts after socket closed event.
     */
    private int restartsQuant = 0;
    /**
     * delay between two separate attempts to to obtain {@link ClientServer} object
     */
    private int delay = 2000;


    public ClientConnectorProvider(ClientServer.ClientServerListener clientListener, int type, int id) {
        this.clientListener = clientListener;
        this.type = type;
        this.id = id;
        this.thisThreadClientServerListener = this;
        startClientServer();
        restartsQuant++;
    }

    private void startClientServer(){
        System.out.println(restartsQuant + " Attempt to get connected to the Server!!!");
        client = new ClientServer(APP.IP, APP.PORT, type, id);
        client.addClientServerListener(thisThreadClientServerListener);
        client.startClient();
    }

    private void restartClientServer(){
        restartsQuant++;
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

    @Override
    public void onRegister(int id) {
        client.removeClientServerListener(thisThreadClientServerListener);
        client.addClientServerListener(clientListener);
        this.listener.onClientConnected(client);
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
