/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments;

import android.animation.*;
import android.content.Context;
import android.util.Log;
import android.view.View;
import com.logosprog.elqdisplay.fragments.tableutils.DeleteRowQueue;
import com.logosprog.elqdisplay.fragments.tableutils.TerminalRow;
import com.logosprog.elqdisplay.fragments.tableutils.TextDrawable;
import display.TerminalData;
import main.APP;
import sockets.DisplayMessage;
import sockets.SocketMessage;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by forando on 08.11.14.
 */
public class TableView extends View implements ValueAnimator.AnimatorUpdateListener,
        DeleteRowQueue.DeleteRowQueueListener{

    private static final String TAG = "TableView";

    private int id;

    private List<TerminalRow> table;

    private int USEDLevels;
    private int levelsToBeUSED = 0;

    protected int restOfClients = 0;

    private final static int[] terminalHeightOffsets = {27, 44, 61, 78, 95};
    private final static int[] widthOffsets = {30, 60, 85};

    private boolean tableIsValid = false;
    private boolean isRowsSliding = false;
    private boolean isOnHoldTerminals = false;

    private int panelWidth;
    private int panelHeight;
    private int onePercentWidth;
    private int onePercentHeight;

    private float mDensity;

    private TextDrawable l_clientTitle;
    private TextDrawable l_terminalTitle;

    AnimatorSet deleteRowAnim;
    AnimatorSet addRowAnim;

    private volatile DeleteRowQueue deleteRowQueue;

    private ScheduledThreadPoolExecutor blinkingScheduler;

    public TableView(Context context, int id) {
        super(context);

        this.id = id;

        blinkingScheduler = new ScheduledThreadPoolExecutor(APP.TERMINAL_QUANTITY);

        this.mDensity = getContext().getResources().getDisplayMetrics().density;
        this.panelWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        this.panelHeight = getContext().getResources().getDisplayMetrics().heightPixels;
        Log.d(TAG, "THE REAL panelHeight = " + panelHeight);
        onePercentWidth = panelWidth / 100;
        onePercentHeight = panelHeight / 100;

        deleteRowQueue = new DeleteRowQueue();
        deleteRowQueue.addDeleteRowQueueListener(this);
    }

    protected void initTable(List<TerminalData> terminalRows, int restOfClients){
        this.restOfClients = restOfClients;
        l_clientTitle = new TextDrawable("Талон");
        l_terminalTitle = new TextDrawable("Касса");
        relocateTitles();
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
                    redraw();
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
        relocateResizedTerminalRows();
        this.listener.relocateBottomPanelChildren();
    }

    protected void addRow(TerminalData terminalRowData){
        TerminalRow row = getTerminalRow(terminalRowData.terminalNumber);
        if (row.state != TerminalRow.ACCEPTED){
            row.state = TerminalRow.ACCEPTED;
            System.err.println("TerminalRow with terminalNumber=" + row.terminalNumber +
                    "was asked to perform SlideUp animation. But its state was not equal to: ACCEPTED. " +
                    "We've set this value manually" );

        }
        row.clientNumber = terminalRowData.clientNumber;
        relocateTerminalRows();
        row.performAnimation(panelWidth, panelHeight, onePercentHeight);
        this.listener.relocateBottomPanelChildren();
        this.listener.playNotificationSound();
    }

    protected void deleteRow(TerminalData terminalRowData){
        TerminalRow row = getTerminalRow(terminalRowData.terminalNumber);
        if (row.state != TerminalRow.WAITING){
            row.state = TerminalRow.WAITING;
            System.err.println("TerminalRow with terminalNumber=" + row.terminalNumber +
                    "was asked to perform SlideAside animation. But its state was not equal to: WAITING. " +
                    "We've set this value manually" );

        }
        //row.performAnimation(panelWidth, panelHeight, onePercentHeight);

        AnimatorSet deleteAnimation = new AnimatorSet();
        deleteAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                TerminalRow row = deleteRowQueue.poll();
                if (row != null){
                    deleteRow(row);
                }
            }
        });

        TextDrawable drawable = row.drawables[0];
        ObjectAnimator clientToDelete = ObjectAnimator.ofFloat(drawable, "x", drawable.getX(),
                panelWidth).setDuration(1000);
        drawable = row.drawables[1];
        ObjectAnimator arrowToDelete = ObjectAnimator.ofFloat(drawable, "x", drawable.getX(),
                panelWidth).setDuration(1000);
        drawable = row.drawables[2];
        ObjectAnimator terminalToDelete = ObjectAnimator.ofFloat(drawable, "x", drawable.getX(),
                panelWidth).setDuration(1000);
        AnimatorSet slideAside = new AnimatorSet();
        slideAside.playTogether(clientToDelete, arrowToDelete, terminalToDelete);

        List<TerminalRow> slideUPRows = new ArrayList<>();
        for (TerminalRow r : table){
            if (r.levelIndex > row.levelIndex){
                slideUPRows.add(r);
            }
        }

        if (slideUPRows.size()>0){
            Collections.sort(slideUPRows);//This line is principle for next line correct work
            List<ObjectAnimator> animations = new ArrayList<>();
            AnimatorSet slideUP = new AnimatorSet();
            for (TerminalRow r : slideUPRows){
                float yDestination = terminalHeightOffsets[r.levelIndex - 1] * onePercentHeight;
                drawable = r.drawables[0];
                animations.add(ObjectAnimator.ofFloat(drawable, "y", drawable.getY(),
                        yDestination).setDuration(500));
                drawable = r.drawables[1];
                animations.add(ObjectAnimator.ofFloat(drawable, "y", drawable.getY(),
                        yDestination).setDuration(500));
                drawable = r.drawables[2];
                animations.add(ObjectAnimator.ofFloat(drawable, "y", drawable.getY(),
                        yDestination).setDuration(500));
            }
            AnimatorSet.Builder builder = slideUP.play(animations.get(0));
            for (int i=1; i<animations.size(); ++i){
                builder.with(animations.get(i));
            }
            deleteAnimation.playSequentially(slideAside, slideUP);
        }else{
            deleteAnimation.play(slideAside);
            row.levelIndex = -1;
            setUSEDLevels(getUSEDLevels() - 1);
            releaseSlidingRequest();
        }

        deleteAnimation.start();

    }

    public void assignTerminal(int terminalIndex) {
        TerminalRow row = getTerminalRow(terminalIndex);
        System.out.println("assignTerminal keyCode = " + terminalIndex);

        if (row.state == TerminalRow.ACCEPTED) {
            List<TerminalData> terminals = new ArrayList<>();
            terminals.add(new TerminalData(row.levelIndex, row.clientNumber,
                    row.terminalNumber, row.visible, row.state));
            this.listener.sendToServer(new DisplayMessage(this.id, DisplayMessage.ADD_ROW,
                    terminals, 0, new Date(), true));
        }else if (row.state == TerminalRow.WAITING){
            List<TerminalData> terminals = new ArrayList<>();
            terminals.add(new TerminalData(row.levelIndex, row.clientNumber,
                    row.terminalNumber, row.visible, row.state));
            this.listener.sendToServer(new DisplayMessage(this.id, DisplayMessage.DELETE_ROW,
                    terminals, 0, new Date(), true));
        }
    }

    public void reAssignTerminals(List<TerminalData> terminalRows){
        USEDLevels = 0;
        for (TerminalRow row : table){
            int terminal = row.terminalNumber;
            TerminalData terminalData = terminalRows.get(terminal);
            row.levelIndex = terminalData.levelIndex;
            row.clientNumber = terminalData.clientNumber;
            row.terminalNumber = terminalData.terminalNumber;
            row.visible = terminalData.visible;
            if (terminalData.visible) USEDLevels++;
            row.state = terminalData.state;
        }
        relocateResizedTerminalRows();
    }

    protected void relocateTitles(){
        int w_loc;
        int h_loc;
        int titleHeight = onePercentHeight * 8;
        l_clientTitle.setFontSize(titleHeight);
        int stringWidth = l_clientTitle.getTextWidth();
        w_loc = (onePercentWidth * widthOffsets[0]) - (stringWidth / 2);
        h_loc = onePercentHeight;
        l_clientTitle.setX(w_loc);
        l_clientTitle.setY(h_loc);

        l_terminalTitle.setFontSize(titleHeight);
        stringWidth = l_terminalTitle.getTextWidth();
        w_loc = (onePercentWidth * widthOffsets[0]) - (stringWidth / 2);
        h_loc = onePercentHeight;
        l_terminalTitle.setX(w_loc);
        l_terminalTitle.setY(h_loc);

    }

    private void relocateTerminalRows(){
        int fontHeight = onePercentHeight * 16;

        for (TerminalRow r : table){
            for (int i=0; i<3; ++i){
                TextDrawable drawable = r.drawables[i];
                int stringWidth = drawable.getTextWidth();
                drawable.setFontSize(fontHeight);
                drawable.setX((onePercentWidth * widthOffsets[i]) - (stringWidth / 2));
            }
        }

        invalidate();
    }

    protected void relocateResizedTerminalRows(){
        int fontHeight = onePercentHeight * 16;

        for (TerminalRow r : table){
            if (r.visible){
                int h_offset = terminalHeightOffsets[r.levelIndex];
                r.drawables[0].setY(onePercentHeight * h_offset);
                r.drawables[1].setY(onePercentHeight * h_offset);
                r.drawables[2].setY(onePercentHeight * h_offset);
            }

            for (int i=0; i<3; ++i){
                TextDrawable drawable = r.drawables[i];
                int stringWidth = drawable.getTextWidth();
                drawable.setFontSize(fontHeight);
                drawable.setX((onePercentWidth * widthOffsets[i]) - (stringWidth / 2));
            }
        }

        invalidate();
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
            this.listener.sendToServer(new DisplayMessage(this.id, SocketMessage.HOLD_CLIENT,
                    listToSend, 0, new Date(), true));
        }else{
            this.listener.sendToServer(new DisplayMessage(this.id, SocketMessage.RELEASE_CLIENT,
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

    private void redraw(){
        invalidate();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        redraw();
    }

    @Override
    public void onDeleteRowQueueInit() {
        TerminalRow row = deleteRowQueue.poll();
        if (row != null){
            deleteRow(row);
        }
    }

    TablePanelListener listener;

    public void addTablePanelListener(TablePanelListener listener) throws Exception {
        if (this.listener != null) throw new Exception("TablePanelListener has been already assigned");
        this.listener = listener;
    }

    public interface TablePanelListener{
        public void relocateBottomPanelChildren();
        public void sendToServer(DisplayMessage message);
        //public Dimension getMediaContentPanelSize();
        public void submitAction(int keyCode);
        public void playNotificationSound();
        //public List<JLabel> getTableTitleLabels();
    }
}
