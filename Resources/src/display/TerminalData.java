/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package display;

import java.io.Serializable;

/**
 * Created by forando on 26.09.14.
 * Base class to be instantiated both on Display and Server sides
 */
public class TerminalData implements Serializable {
    public static final int ACCEPTED = 0;
    public static final int CALLING = 1;
    public static final int WAITING = 2;
    public static final int ACCEPTING = 3;

    public int levelIndex;
    public int clientNumber;
    public int terminalNumber;
    public boolean visible;
    public int state;

    public TerminalData(int levelIndex, int clientNumber, int terminalNumber, boolean visible, int state) {
        this.levelIndex = levelIndex;
        this.clientNumber = clientNumber;
        this.terminalNumber = terminalNumber;
        this.visible = visible;
        this.state = state;
    }
}
