package com.logosprog.elqdisplay.interfaces;

import com.logosprog.elqdisplay.fragments.ClientLayout;
import display.TerminalData;

import java.util.List;

/**
 * Created by logosprog on 16.10.2014.
 */
public interface MainActivityDelegate {
    public void onAssignClient(TerminalData terminalRowData, int restOfClients);
    public void onAcceptClient(TerminalData terminalRowData, int restOfClients);
    public void onInitTable (List<TerminalData> terminals, int restOfClients);
}
