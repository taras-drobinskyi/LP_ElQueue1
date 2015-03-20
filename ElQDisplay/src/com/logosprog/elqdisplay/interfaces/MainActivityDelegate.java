package com.logosprog.elqdisplay.interfaces;

import com.logosprog.elqdisplay.fragments.ClientLayout;
import display.TerminalData;

import java.util.List;

/**
 * Created by forando on 16.10.2014.
 * This interface is implemented by all children of Main Activity
 */
public interface MainActivityDelegate {
    /**
     *
     * Indicates that ClientServer has received a message to assign a new client.
     * @param terminalRowData All meta data for the terminal row
     * @param restOfClients Indicates how many clients are waiting for being serviced.
     */
    public void onAssignClient(TerminalData terminalRowData, int restOfClients);
    /**
     *
     * Indicates that ClientServer has received a message to accept a client
     * (means to delete a correspondent row from the table).
     * @param terminalRowData All meta data for the terminal row
     * @param restOfClients Indicates how many clients are waiting for being serviced.
     */
    public void onAcceptClient(TerminalData terminalRowData, int restOfClients);

    /**
     * Indicates that ClientServer has received a message to initialize/reinitialize
     * the whole table.
     * @param terminals A list of table rows with all meta data to be initialized.
     * @param restOfClients Indicates how many clients are waiting for being serviced.
     */
    public void onInitTable (List<TerminalData> terminals, int restOfClients);

    /**
     * Indicates that ClientServer has received a message to update <b>restOfClients</b>
     * value on a screen.
     * @param restOfClients Indicates how many clients are waiting for being serviced.
     */
    public void onPrintTicket(int restOfClients);

    /**
     * Indicates that ClientServer has received a message to display printer error message.
     * @param printerError True if there is a printer error, false - if there is not.
     */
    public void onPrinterError(boolean printerError);

    /**
     * Indicates that ClientServer has received a message to change the mode of functioning.
     * @param stopService If true - the service must be stopped, if false - than it must be reset.
     */
    public void onServiceChange(boolean stopService);

    /**
     *
     * Indicates that addRowAnimation in {@link com.logosprog.elqdisplay.fragments.TableView},
     * with assigned client, is about to begin.
     * @param terminal The terminal number
     * @param client The client number
     */
    public void onClientAssignAnimationStart(int terminal, int client);
}
