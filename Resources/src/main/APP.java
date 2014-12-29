package main;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by forando on 10.04.14.
 */
public class APP {
    public final static String VARIABLES_PATH = "/usr/local/share/xml/LP_ElQueue1/variables.xml";
    public final static String VIDEO_PATH = "/media/forando/DATA/Фильмы/mamont.2009.xvid.111dvdrip.(hdrip).avi";

    public final static int TERMINAL_QUANTITY = 5;
    public final static int MAX_TERMINAL_QUANTITY = 10;
    public final static int LEVEL_QUANTITY = 6;

    //System Commands:
    public final static int RESET_SYSTEM = 0;
    public final static int PRINTER_ERROR_ON = 1;
    public final static int PRINTER_ERROR_OFF = 2;
    public final static int STOP_SERVICE = 3;
    public final static int RESET_SERVICE = 4;
    public final static int TRIGGER_SERVICE = 5;
    public final static int PRINT_TICKET = 6;

    /*
    Server stuff
     */
    //public static final String IP = "localhost";
    public static final String IP = "192.168.0.181";
    //public static final String IP = "10.0.0.109";
    //public static final String IP = "10.0.2.2";

    public static final int PORT = 1337;

    /**
     * Static helper method to be used in socket communications
     * @param ip The IP to be converted in {@link java.net.InetAddress}
     * @return an IP in {@link java.net.InetAddress} representation
     * @throws NullPointerException
     */
    public static InetAddress getHostIP(String ip)throws NullPointerException{
        try {
            return InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }


}
