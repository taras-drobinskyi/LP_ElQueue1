/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import display.TerminalData;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by forando on 05.10.14.
 */
public class TerminalRow extends TerminalData implements Comparable {
    protected boolean partlyVisible;
    protected int ypos;
    protected int[] xpos;
    private Timer timerBlinking;

    private int[] terminalHeightOffsets;

    public TerminalRow(TerminalData terminalData, int[] terminalHeightOffsets) {
        super(terminalData.levelIndex, terminalData.clientNumber
                , terminalData.terminalNumber, terminalData.visible, terminalData.state);
        this.terminalHeightOffsets = terminalHeightOffsets;
        this.xpos = new int[3];
        this.xpos[0] = 0;
        this.xpos[1] = 0;
        this.xpos[2] = 0;
        this.ypos = 0;
        //todo: write functionality to store blinkRate locally
        timerBlinking = new Timer(500, new BlinkingTimerListener());
    }

    private void transitionCompleted(){
        this.listener.onTransitionCompleted(this);
    }

    private void showMessageForm(){
        this.listener.onShowMessageForm(this);
    }

    private void disposeMessageForm(){
        this.listener.onDisposeMessageForm(this);
    }

    public synchronized void performAnimation(int uiPanelWidth, int uiPanelHeight, int h_percent_uiPanel){
        if (state == ACCEPTED) {
            state = CALLING;
            visible = true;
            this.listener.check_ForTerminalsOnHoldSet();
            int usedLevels = this.listener.getUsedLevels();
            (new Timer(10, new SlidingUPTimerListener(usedLevels, uiPanelHeight, h_percent_uiPanel))).start();
        }else if (state == WAITING){
            state = ACCEPTING;
            this.listener.check_ForTerminalsOnHoldRelease();
            (new Timer(10, new SlidingAsideTimerListener(xpos, uiPanelWidth))).start();
            //setUsedLevels(usedLevels - 1) is done in SlidingUPRowsTimerListener
        }
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
    private void redrawMyComponents() {
        this.listener.redrawComponents();
    }

    private class BlinkingTimerListener implements ActionListener {

        private boolean isForeground = true;
        private final static int maxBlinking = 4;
        private int alreadyBlinked = 0;
        private boolean clientMessageFormIsShown = false;


        @Override
        public void actionPerformed(ActionEvent e) {
            if (alreadyBlinked <= maxBlinking * 2) {
                alreadyBlinked++;
                partlyVisible = !isForeground;
                isForeground = !isForeground;
                if (!clientMessageFormIsShown){
                    clientMessageFormIsShown = true;
                    showMessageForm();
                }
            } else {
                alreadyBlinked = 0;
                isForeground = true;
                partlyVisible = false;
                ((Timer)e.getSource()).stop();
                state = WAITING;
                //saveToXML();
                clientMessageFormIsShown = false;
                disposeMessageForm();
            }
            redrawMyComponents();
        }
    }

    private class SlidingAsideTimerListener implements ActionListener{
        private int[] initialXoffsets;
        private int uiPanelWidth;

        private SlidingAsideTimerListener(int[] initialXoffsets, int uiPanelWidth) {
            this.initialXoffsets = initialXoffsets;
            this.uiPanelWidth = uiPanelWidth;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (xpos[0]> uiPanelWidth + 40){
                xpos = initialXoffsets;
                visible = false;
                state = ACCEPTED;
                ((Timer)e.getSource()).stop();
                transitionCompleted();
            }else{
                xpos[0] += 10;
                xpos[1] += 10;
                xpos[2] += 10;
            }
            redrawMyComponents();
        }
    }

    private class SlidingUPTimerListener implements ActionListener{
        private int Ydestination;

        private SlidingUPTimerListener(int levelDestination, int uiPanelHeight, int h_percent_uiPanel) {
            ypos = uiPanelHeight + 40;
            levelIndex = levelDestination;
            listener.setUsedLevels(levelIndex + 1);
            System.out.println("Destination Level = " + levelDestination);
            this.Ydestination = h_percent_uiPanel * terminalHeightOffsets[levelDestination];
            System.out.println("levelIndex = " + levelIndex);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ypos -= 10;
            //System.out.println("levelIndex = " + levelIndex);
            if (ypos < Ydestination + 20){
                ypos = Ydestination;
                //levelIndex = levelDestination;
                //setUsedLevels(levelIndex + 1);
                System.out.println("USED levels" + listener.getUsedLevels());
                ((Timer) e.getSource()).stop();
                timerBlinking.start();
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
