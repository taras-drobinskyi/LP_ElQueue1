/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package services.interfaces;

import helpers.SocketMessage;

/**
 * Created by forando on 27.08.14.
 */
public interface TerminalServerListener {
    public void onTerminalMessage(SocketMessage message);
}
