package sockets;/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import java.io.ObjectOutputStream;

/**
 * Created by forando on 01.10.14.
 */
public class OutPut  extends Thread {
    private volatile Thread myThread;
    ObjectOutputStream out;
    private int id;
    private Object messageObject;

    public OutPut(ObjectOutputStream out, int id, Object messageObject){
        myThread = this;
        this.out = out;
        this.id = id;
        this.messageObject = messageObject;
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
            System.out.println("Socket: onSendMessage socketID = " + id +
                    " operation = " + ((SocketMessage) messageObject).operation);
            out.writeUnshared(messageObject);
            out.flush();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
