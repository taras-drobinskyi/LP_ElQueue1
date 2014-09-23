/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by forando on 11.09.14.
 */
public class DisplayMessage extends SocketMessage{

    public static final int INIT_ROWS = 200;
    public static final int DELETE_ROW = 201;
    public static final int ADD_ROW = 202;

    /*public static final int RESET_SYSTEM = 210;
    public static final int PRINTER_ERROR_ON = 211;
    public static final int PRINTER_ERROR_OFF = 212;
    public static final int STOP_SERVICE = 213;
    public static final int RESET_SERVICE = 214;
    public static final int TRIGGER_SERVICE = 215;*/

    List<HashMap<String, Integer>> dataList;

    /**
     * @param id           The id index
     * @param operation    The command to be executed
     * @param date         Current time stamp
     * @param transferable See {@link #transferable}
     */
    public DisplayMessage(int id, int operation, List<HashMap<String, Integer>> dataList,
                          Date date, boolean transferable) {
        super(id, operation, date, transferable);
        this.dataList = dataList;
    }
}
