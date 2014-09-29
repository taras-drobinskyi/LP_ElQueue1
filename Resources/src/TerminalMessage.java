/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import java.io.Serializable;
import java.util.Date;

/**
 * Created by forando on 18.09.14.
 */
public class TerminalMessage extends SocketMessage implements Serializable {
    public final static int REQUEST_CLIENT = 100;
    public final static int ACCEPT_CLIENT = 101;
    /**
     * Defines whether id is allowed to request a new client.<br>
     * if {@link #value} = 0 - it's allowed, if {@link #value} = 1 - it's not
     */
    public final static int HOLD_TERMINAL = 102;

    /**
     * A value for the {@link #operation} to be provided.
     */
    public int value;

    /**
     * @param terminal     The id index
     * @param operation    The command to be executed
     * @param value        The operation value
     * @param date         Current time stamp
     * @param transferable See {@link #transferable}
     */
    public TerminalMessage(int terminal, int operation, int value, Date date, boolean transferable) {
        super(terminal, operation, date, transferable);
        this.value = value;
    }
}
