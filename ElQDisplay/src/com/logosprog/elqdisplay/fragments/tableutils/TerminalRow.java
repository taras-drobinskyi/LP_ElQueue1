/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments.tableutils;

import display.TerminalData;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created by forando on 06.11.14.
 */
public class TerminalRow extends TerminalData implements Comparable {

    protected boolean partlyVisible;
    private int[] terminalHeightOffsets;
    protected TextDrawable[] drawables;

    private ScheduledThreadPoolExecutor blinkingScheduler;

    public TerminalRow(TerminalData terminalData, int[] terminalHeightOffsets) {
        super(terminalData.levelIndex, terminalData.clientNumber
                , terminalData.terminalNumber, terminalData.visible, terminalData.state);
        this.terminalHeightOffsets = terminalHeightOffsets;
        drawables = new TextDrawable[3];

        blinkingScheduler = new ScheduledThreadPoolExecutor(1);
        //blinkingScheduler.scheduleAtFixedRate(new Blinker(), 0, 500, TimeUnit.SECONDS);


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

    public synchronized void performAnimation(int uiPanelWidth, int uiPanelHeight, int h_percent_uiPanel){
        if (state == ACCEPTED) {
            state = CALLING;
            visible = true;
            this.listener.check_ForTerminalsOnHoldSet();
            int usedLevels = this.listener.getUsedLevels();
            //(new Timer(10, new SlidingUPTimerListener(usedLevels, uiPanelHeight, h_percent_uiPanel))).start();
        }else if (state == WAITING){
            state = ACCEPTING;
            this.listener.check_ForTerminalsOnHoldRelease();
            //(new Timer(10, new SlidingAsideTimerListener(xpos, uiPanelWidth))).start();
            //setUsedLevels(usedLevels - 1) is done in SlidingUPRowsTimerListener
        }
    }

    private void transitionCompleted(){
        this.listener.onTransitionCompleted(this);
    }

    private void redrawMyComponents() {
        this.listener.redrawComponents();
    }

    private void showMessageForm(){
        this.listener.onShowMessageForm(this);
    }

    private void disposeMessageForm(){
        this.listener.onDisposeMessageForm(this);
    }

    private class Blinker implements Runnable{

        private boolean isVisible = true;
        private final static int maxBlinking = 4;
        private int alreadyBlinked = 0;
        private boolean clientMessageFormIsShown = false;

        @Override
        public void run() {
            if (alreadyBlinked <= maxBlinking * 2) {
                alreadyBlinked++;
                partlyVisible = !isVisible;
                isVisible = !isVisible;
                if (!clientMessageFormIsShown){
                    clientMessageFormIsShown = true;
                    showMessageForm();
                }
            } else {
                alreadyBlinked = 0;
                isVisible = true;
                partlyVisible = false;
                blinkingScheduler.shutdown();
                state = WAITING;
                //saveToXML();
                clientMessageFormIsShown = false;
                disposeMessageForm();
            }
            redrawMyComponents();
        }
    }

    TerminalRowListener listener;
    public void addTerminalRowListener(TerminalRowListener listener) {
        this.listener = listener;
    }

    public interface TerminalRowListener{
        public void onTransitionCompleted(TerminalRow row);
        public void onShowMessageForm(TerminalRow row);
        public void onDisposeMessageForm(TerminalRow row);
        public void check_ForTerminalsOnHoldSet();
        public int getUsedLevels();
        public void check_ForTerminalsOnHoldRelease();
        public void redrawComponents();
        public void setUsedLevels(int levelIndex);
    }
}
