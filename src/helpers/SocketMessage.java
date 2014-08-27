/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package helpers;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by forando on 26.08.14.
 * Struct object just used for communcation -- sent on the object stream.<br>
 * The contained int and Date objects are both Serializable, otherwise
 * the serialization would fail.
 */
public class SocketMessage implements Serializable {
    public final static int ID_SENDING = 0;

    public int terminal;
    public int operation;
    public int value;
    public Date date;

    public SocketMessage(int terminal, int operation, int value, Date date) {
        this.terminal = terminal;
        this.operation = operation;
        this.value = value;
        this.date = date;
    }
}
