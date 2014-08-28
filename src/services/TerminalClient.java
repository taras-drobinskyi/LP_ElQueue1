/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package services;

import helpers.APP;
import helpers.SocketMessage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Date;

/**
 * Created by forando on 27.08.14.
 */
public class TerminalClient {
    private ClientHandler clientHandler;

    public static void main(String[] args) {
        new TerminalClient().startClient();
    }

    // Runs a client handler to connect to a server
    public void startClient() {
        clientHandler = new ClientHandler(APP.IP, APP.PORT);
        clientHandler.start();
    }

    // Client runs this to handle incoming messages
    // (our client only uses the inputstream of the connection)
    private class ClientHandler extends Thread {
        String name;
        int port;
        private volatile Thread myThread;

        Socket socket;
        ObjectInputStream in;
        ObjectOutputStream out;
        Thread worker;

        ClientHandler(String name, int port) {
            this.name = name;
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

        // Connect to the server, loop getting messages
        public void run() {
            if (myThread == null) {
                System.out.println("Thread has been stopped");
                return; // stopped before started.
            }
            try {
                // make connection to the server name/port
                socket = new Socket(name, port);

                // get input stream to read from server, wrap in object stream
                ReadableByteChannel channel = Channels.newChannel(socket.getInputStream());
                in = new ObjectInputStream(Channels.newInputStream(channel));
                //in = new ObjectInputStream(socket.getInputStream());

                out = new ObjectOutputStream(socket.getOutputStream());
                System.out.println("client: connected!");

                //while (true) {

                    SocketMessage message = new SocketMessage(1,SocketMessage.OPEN_TERMINAL, 1, new Date(), true);

                    out.writeUnshared(message);
                    out.flush();

                    // get object from server, will block until object arrives.
                    message = (SocketMessage) in.readObject();
                    System.out.println("client: read statusReceived: " + String.valueOf(message.received));


                if (message.received){
                    message.operation = SocketMessage.ACCEPT_CLIENT;
                    message.value = 1;
                    /*out.writeObject(message);
                    out.flush();*/
                }

                    // post this data to the UI
                    //threadIn(message);

                    Thread.yield(); // let another thread have some time perhaps to stop this one.
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException("Stopped by ifInterruptedStop()");
                    }
               // }
            }
            catch (Exception ex) { // IOException and ClassNotFoundException
                ex.printStackTrace();

            }
            // could null out client ptr
            // note that exception breaks out of the while loop, thus ending the thread
        }
    }
}
