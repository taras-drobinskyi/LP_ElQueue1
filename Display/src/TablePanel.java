/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import display.TerminalData;
import interfaces.ClientMessageFormListener;
import sockets.DisplayMessage;
import sockets.SocketMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by forando on 05.10.14.
 */
public class TablePanel extends JPanel {
    private List<TerminalRow> table;
    private boolean tableIsValid = false;

    private int USEDLevels;
    private int levelsToBeUSED = 0;

    private int restOfClients = 0;

    private int h_percent_uiPanel;

    private int[] terminalHeightOffsets;

    private boolean isRowsSliding = false;
    private boolean isOnHoldTerminals = false;

    Font TABLE_FONT;

    private Point hor_line1_p1 = new Point(100, 100);
    private Point hor_line1_p2 = new Point(200, 200);

    private ClientMessageForm form;

    public TablePanel(int[] terminalHeightOffsets, Font tableFont){
        this.terminalHeightOffsets = terminalHeightOffsets;
        this.TABLE_FONT = tableFont;
    }

    private void initTable(List<TerminalData> terminalRows, int restOfClients){
        this.restOfClients = restOfClients;
        initClients(terminalRows);
    }

    private void initClients(List<TerminalData> terminalRows){
        table = new ArrayList<>();
        USEDLevels = 0;
        for(int i=0; i< APP.TERMINAL_QUANTITY; i++){
            TerminalData terminalData = terminalRows.get(i);
            TerminalRow terminalRow = new TerminalRow(terminalData, terminalHeightOffsets);
            terminalRow.addTerminalRowListener(new TerminalRow.TerminalRowListener() {
                @Override
                public void onTransitionCompleted(TerminalRow row) {
                    (new RowsSlideUPRunner(row)).start();
                    /*for (DisplayForm.DisplayFormListener l : displayFormListeners){
                        l.onAcceptClient(row.terminalNumber, row.clientNumber);
                        System.out.println("MainFormListener onAcceptClient");
                    }*/
                }

                @Override
                public void onShowMessageForm(TerminalRow row) {
                    Dimension mediaContentPanelSize = getMediaContentPanelSize();
                    int width = mediaContentPanelSize.width;
                    int height = mediaContentPanelSize.height;
                    if (form == null) {
                        form = new ClientMessageForm(width, height, row.clientNumber, row.terminalNumber + 1);
                        form.addClientMessageFormListener(new ClientMessageFormListener() {
                            @Override
                            public void onClose() {
                                form.dispose();
                                form = null;
                            }

                            @Override
                            public void onKeyPressed(int keyCode) {
                                if(keyCode>=49 && keyCode<=57){
                                    int terminalNumber = keyCode - DisplayForm.TERMINAL_BASE + 1;
                                    if (terminalNumber <= APP.TERMINAL_QUANTITY) {
                                        keyCode = keyCode - DisplayForm.TERMINAL_BASE;
                                    }
                                }
                                submitEvent(keyCode);
                            }
                        });
                    }else {
                        form.addMessage(row.clientNumber, row.terminalNumber + 1);
                    }
                    /*for (DisplayForm.DisplayFormListener l : displayFormListeners){
                        l.onAssignClient(row.terminalNumber, row.clientNumber);
                        System.out.println("MainFormListener onAssignClient");
                    }*/
                }

                @Override
                public void onDisposeMessageForm(TerminalRow row) {
                    if (form != null) {
                        form.removeMessage(row.terminalNumber + 1);
                    }
                }

                @Override
                public void check_ForTerminalsOnHoldSet() {
                    checkForTerminalsOnHoldSet();
                }

                @Override
                public int getUsedLevels() {
                    return getUSEDLevels();
                }

                @Override
                public void check_ForTerminalsOnHoldRelease() {
                    checkForTerminalsOnHoldRelease();
                }

                @Override
                public void redrawComponents() {
                    redrawMyComponents();
                }

                @Override
                public void setUsedLevels(int levels) {
                    setUSEDLevels(levels);
                }
            });
            table.add(terminalRow);
            if (terminalRow.levelIndex>=0) USEDLevels++;
        }
        initialTerminalAssignmentCheck();
        tableIsValid = true;
        System.out.println("USED LEVELS = " + getUSEDLevels());
        this.listener.relocateResizedTerminalRorws();
        this.listener.relocateBottomComponents();
    }

    public void reAssignTerminals(List<TerminalData> terminalRows){
        for (TerminalRow row : table){
            int terminal = row.terminalNumber;
            TerminalData terminalData = terminalRows.get(terminal);
            row.levelIndex = terminalData.levelIndex;
            row.clientNumber = terminalData.clientNumber;
            row.terminalNumber = terminalData.terminalNumber;
            row.visible = terminalData.visible;
            row.state = terminalData.state;
        }
    }

    public List<TerminalRow> getTable(){return table;}

    public TerminalRow getTerminalRow(int terminal){
        for (TerminalRow row : table){
            if (row.terminalNumber == terminal) return row;
        }
        return null;
    }

    public synchronized void setUSEDLevels(int val){
        USEDLevels = val;
    }

    public synchronized int getUSEDLevels(){
        return USEDLevels;
    }

    public void initialTerminalAssignmentCheck(){
        if (USEDLevels >= APP.LEVEL_QUANTITY){
            sendOnHoldTerminals(1);
        }
        levelsToBeUSED = USEDLevels;
    }

    private synchronized void checkForTerminalsOnHoldSet(){
        levelsToBeUSED++;
        System.out.println("check_ForTerminalsOnHoldSet levelsToBeUSED = " + levelsToBeUSED +
                " LEVEL_QUANTITY = " + APP.LEVEL_QUANTITY);
        System.out.println("isOnHoldTerminals = " + isOnHoldTerminals);
        if (levelsToBeUSED >= APP.LEVEL_QUANTITY && !isOnHoldTerminals) {
            isOnHoldTerminals = true;
            System.out.println("check_ForTerminalsOnHoldSet sendOnHoldTerminals val = " + 1);
            sendOnHoldTerminals(1);
        }
    }

    public synchronized void checkForTerminalsOnHoldRelease(){
        levelsToBeUSED--;
        System.out.println("check_ForTerminalsOnHoldRelease levelsToBeUSED = " + levelsToBeUSED +
                " LEVEL_QUANTITY = " + APP.LEVEL_QUANTITY);
        System.out.println("isOnHoldTerminals = " + isOnHoldTerminals);
        if (levelsToBeUSED < APP.LEVEL_QUANTITY && isOnHoldTerminals) {
            isOnHoldTerminals = false;
            System.out.println("check_ForTerminalsOnHoldRelease sendOnHoldTerminals val = " + 0);
            sendOnHoldTerminals(0);
        }
    }

    private void sendOnHoldTerminals(int val){
        List<TerminalData> listToSend = new ArrayList<>();
        for (TerminalRow row : table){
            if (row.state != TerminalRow.CALLING && row.state != TerminalRow.WAITING){
                listToSend.add(new TerminalData(row.levelIndex, row.clientNumber,
                        row.terminalNumber, row.visible, row.state));
            }
        }
        //int[] terminals = new int[list.size()];
        System.out.println("sendOnHoldTerminals table.size() = " + table.size());
        System.out.println("sendOnHoldTerminals list.size() = " + listToSend.size());
            /*for (int i=0; i<list.size(); i++){
                terminals[i] = list.get(i);
            }*/
            /*for (MainFormListener l : mainFormListeners){
                l.onHoldTerminals(terminals, val);
            }*/
        if (val ==1) {
            this.listener.sendToServer(new DisplayMessage(0, SocketMessage.HOLD_CLIENT,
                    listToSend, 0, new Date(), true));
        }else{
            this.listener.sendToServer(new DisplayMessage(0, SocketMessage.RELEASE_CLIENT,
                    listToSend, 0, new Date(), true));
        }
    }

    /**
     * Provides a handler for different RowSets to perform a transition action.
     * @return Permission value for the specific rowSet to do sliding up transition
     */
    public synchronized boolean requestSliding(){
        if (isRowsSliding){
            return false;
        }else{
            isRowsSliding = true;
            return true;
        }
    }

    /**
     * Releases a handler to perform a sliding up transition.
     */
    public void releaseSlidingRequest(){
        isRowsSliding = false;
        System.out.println("USED LEVELS = " + getUSEDLevels());
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        repaintMyRows(graphics);
    }

    private void redrawMyComponents(){
        this.repaint();
    }

    private synchronized void repaintMyRows(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.white);
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(8,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        Line2D h_lin1 = new Line2D.Float(hor_line1_p1.x, hor_line1_p1.y, hor_line1_p2.x, hor_line1_p2.y);
        g2.draw(h_lin1);


        if (tableIsValid) {
            g.setFont(TABLE_FONT);
            for (TerminalRow row : table) {
                if (row.visible) {
                    int[] xoffsets = row.xpos;
                    if (!row.partlyVisible) {
                        g.setColor(Color.YELLOW);
                        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                        g.drawString(String.valueOf(row.clientNumber), xoffsets[0], row.ypos);
                        g.drawString(">", xoffsets[1], row.ypos);
                    }
                    g.setColor(Color.WHITE);
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g.drawString(String.valueOf(row.terminalNumber + 1), xoffsets[2], row.ypos);
                }
            }
        }
    }

    public void reassignLines(Point hor_line1_p1, Point hor_line1_p2){
        this.hor_line1_p1 = hor_line1_p1;
        this.hor_line1_p2 = hor_line1_p2;
    }

    public void reassign_h_percent_uiPanel(int h_percent_uiPanel){
        this.h_percent_uiPanel = h_percent_uiPanel;
    }

    private class RowsSlideUPRunner extends Thread {
        TerminalRow row;
        //MainUIPanel uiPanel;

        private RowsSlideUPRunner(TerminalRow row) {
            this.row = row;
            //this.uiPanel = uiPanel;
        }

        @Override
        public void run() {
            while (!requestSliding()){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            List<TerminalRow> slideUPRows = new ArrayList<>();
            for (TerminalRow r : table){
                if (r.levelIndex > row.levelIndex){
                    slideUPRows.add(r);
                }
            }

            if (slideUPRows.size()>0){
                Collections.sort(slideUPRows);//This line is principle for next line correct work

                (new Timer(10, new SlidingUPRowsTimerListener(slideUPRows, row))).start();
            }else{
                row.levelIndex = -1;
                setUSEDLevels(getUSEDLevels() - 1);
                releaseSlidingRequest();
            }
        }
    }

    private class SlidingUPRowsTimerListener implements ActionListener {
        List<TerminalRow> slideUPRows;
        TerminalRow rowThatGone;

        private SlidingUPRowsTimerListener(List<TerminalRow> slideUPRows, TerminalRow rowThatGone) {
            this.slideUPRows = slideUPRows;
            this.rowThatGone = rowThatGone;
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            for (TerminalRow r : slideUPRows){
                r.ypos -= 10;
            }
                /*
                Before instantiate this listener we've sorted slideUPRows List against levelIndex values.
                That's why at the next line slideUPRows.get(0) returns TerminalRow with the smallest levelIndex
                 */
            int tableUPpos = slideUPRows.get(0).ypos;
            int rowThatGonePos = rowThatGone.ypos + 20;
            if (tableUPpos < rowThatGonePos){
                ((Timer) e.getSource()).stop();
                for (TerminalRow row : slideUPRows) {
                    int level = row.levelIndex;
                    row.ypos = h_percent_uiPanel * terminalHeightOffsets[level - 1];
                    row.levelIndex = level - 1;
                    //row.saveToXML();
                }
                rowThatGone.levelIndex = -1;
                setUSEDLevels(getUSEDLevels() - 1);
                //rowThatGone.saveToXML();
                releaseSlidingRequest();
            }
            repaint();
        }
    }

    private Dimension getMediaContentPanelSize(){
        return this.listener.getMediaContentPanelSize();
    }

    private void submitEvent(int keyCode){
        this.listener.submitEvent(keyCode);
    }

    TablePanelListener listener;

    public void addTablePanelListener(TablePanelListener listener){
        this.listener = listener;
    }

    public interface TablePanelListener{
        public void relocateResizedTerminalRorws();
        public void relocateBottomComponents();
        public void sendToServer(DisplayMessage message);
        public Dimension getMediaContentPanelSize();
        public void submitEvent(int keyCode);
    }
}
