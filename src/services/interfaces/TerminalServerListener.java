/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package services.interfaces;

import helpers.SocketMessage;
import services.TerminalServer;

/**
 * Created by forando on 27.08.14.
 */
public interface TerminalServerListener {
    public void onTerminalServerMessage(TerminalServer.SocketOrganizer.SocketObject soc);
}
