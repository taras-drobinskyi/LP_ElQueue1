/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package display;

import java.util.HashMap;

/**
 * Created by forando on 26.09.14.
 */
public class TerminalData implements Comparable {
    public static final int ACCEPTED = 0;
    public static final int CALLING = 1;
    public static final int WAITING = 2;
    public static final int ACCEPTING = 3;

    public int levelIndex;
    public int clientNumber;
    public int terminalNumber;
    public boolean visible;
    public int state;

    public TerminalData(int levelIndex, int clientNumber, int terminalNumber, int visibility, int state) {
        this.levelIndex = levelIndex;
        this.clientNumber = clientNumber;
        this.terminalNumber = terminalNumber;
        this.visible = visibility == 1;
        this.state = state;
    }

    @Override
    public int compareTo(Object obj) {
        TerminalData dataToCompare = (TerminalData)obj;
        int retVal=0;
        if (levelIndex<dataToCompare.levelIndex){
            retVal = -1;
        }else if (levelIndex>dataToCompare.levelIndex){
            retVal = 1;
        }
        return retVal;
    }
}
