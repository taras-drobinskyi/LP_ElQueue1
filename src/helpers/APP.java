package helpers;/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

/**
 * Created by forando on 10.04.14.
 */
public class APP {
    public final static String VARIABLES_PATH = "/usr/local/share/xml/LP_ElQueue1/variables.xml";
    public final static String VIDEO_PATH = "/media/forando/DATA/Фильмы/mamont.2009.xvid.111dvdrip.(hdrip).avi";

    public final static int TERMINAL_QUANTITY = 5;
    public final static int MAX_TERMINAL_QUANTITY = 10;
    public final static int LEVEL_QUANTITY = 2;

    //System Commands:
    public final static int RESET_SYSTEM = 112;//F1
    public final static int PRINTER_ERROR_ON = 113;//F2
    public final static int PRINTER_ERROR_OFF = 114;//F3
    public final static int STOP_SERVICE = 115;//F4
    public final static int RESET_SERVICE = 116;//F5
    public final static int TRIGGER_SERVICE = 117;//F6
    public final static int PRINT_TICKET = 36;//HOME

    /*
    Server stuff
     */
    //public static final String IP = "192.168.0.181";
    public static final String IP = "192.168.1.160";
    public static final int PORT = 8000;
}
