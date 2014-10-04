package sockets;/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import java.io.Serializable;
import java.util.Date;

/**
 * Created by forando on 21.09.14.
 */
public class PrinterMessage extends SocketMessage implements Serializable {
    public static final int PRINT_TICKET = 0;


    public int val;
    /**
     * @param id           The id index
     * @param operation    The command to be executed
     * @param date         Current time stamp
     * @param transferable See {@link #transferable}
     */
    public PrinterMessage(int id, int operation, int val, Date date, boolean transferable) {
        super(id, operation, date, transferable);
        this.val = val;
    }
}
