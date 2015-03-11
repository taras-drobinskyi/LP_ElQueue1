/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments;

import android.animation.*;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import com.logosprog.elqdisplay.fragments.tableutils.*;
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
        TableAnimationQueue.TableAnimationQueueListener, ViewTreeObserver.OnGlobalLayoutListener{

    private final String TAG = getClass().getSimpleName();

    private int id;

    private List<TerminalRow> table;
    private List<TerminalData> tempTerminalRows;

    /**
     * Indicates how many Table Levels/Rows is being shown on a screen.
     */
    private int USEDLevels;
    /**
     * Additional to {@link #USEDLevels} helper field.<br>
     *     It's used to coordinate two different type of animation:
     *     {@link #deleteRow} and {@link #addRow} that may happen at a same time.<br>
     *     The value of this field is changed in those methods right <b>before</b> their animation
     *     begins and indicates how many Table Levels/Rows <b>is going to be</b> shown on a screen
     *     <b>after</b> the animation.<br>
     *     Here is the difference with {@link #USEDLevels}, cause the last one is
     *     changed only <b>after</b> animation is done.
     */
    private int levelsToBeUSED = 0;

    protected int restOfClients = 0;

    private final static int[] terminalHeightOffsets = {27, 44, 61, 78, 95};
    //private final static int[] widthOffsets = {30, 60, 85};
    private final static int[] widthOffsets = {0, 30, 55};

    /**
     * Flag that indicates whether or not the {@link #initTable(java.util.List, int)}
     * method has been called.
     */
    private boolean requestedINIT = false;

    /**
     * Indicates whether animation for adding row has been started and not
     * been finished yet.
     */
    private int addRowAnimationsInProgress = 0;
    /**
     * Indicates whether animation for deleting row has been started and not
     * been finished yet.
     */
    private boolean animationIsInProgress = false;
    /**
     * Flag that indicates if the width and height of this View has been already
     * specified.
     */
    private boolean layoutDimensionsAreValid = false;
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

    //private DeleteRowQueue deleteRowQueue;
    private TableAnimationQueue tableAnimationQueue;

    AnimatorSet deleteRowAnim;
    AnimatorSet addRowAnim;

    Paint paint;

    private ScheduledThreadPoolExecutor blinkingScheduler;

    public TableView(Context context, int id) {
        super(context);

        this.id = id;

        /*deleteRowQueue = new DeleteRowQueue();
        deleteRowQueue.addDeleteRowQueueListener(this);*/

        tableAnimationQueue = new TableAnimationQueue();
        tableAnimationQueue.addTableAnimationQueueListener(this);

        blinkingScheduler = new ScheduledThreadPoolExecutor(APP.TERMINAL_QUANTITY);

        /*this.mDensity = getContext().getResources().getDisplayMetrics().density;
        this.panelWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        this.panelHeight = getContext().getResources().getDisplayMetrics().heightPixels;*/

        /*
        Register for measuring layout height and width
         */
        getViewTreeObserver().addOnGlobalLayoutListener(this);

        //setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.paint = new Paint();
        this.paint.setColor(Color.WHITE);
        this.paint.setStrokeWidth(5f);
    }

    protected void initTable(List<TerminalData> terminalRows, int restOfClients){
        requestedINIT = true;
        this.restOfClients = restOfClients;
        l_clientTitle = new TextDrawable("Талон");
        l_terminalTitle = new TextDrawable("Касса");

        if (layoutDimensionsAreValid) {
            relocateTitles();
            initClients(terminalRows);
        }else{
            this.tempTerminalRows = terminalRows;
            Log.e(TAG, "Fail to init table. Layout width and height has not been got yet.");
        }
    }

    private void initClients(List<TerminalData> terminalRows){
        table = new ArrayList<>();
        USEDLevels = 0;
        for(int i=0; i< APP.TERMINAL_QUANTITY; ++i){
            TerminalData terminalData = terminalRows.get(i);
            TerminalRow terminalRow = new TerminalRow(terminalData);
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
            if (terminalRow.levelIndex>=0) incrementUSEDLevels();
        }
        initialTerminalAssignmentCheck();
        tableIsValid = true;
        Log.d(TAG, "USED LEVELS = " + getUSEDLevels());
        relocateResizedTerminalRows();
        /*this.listener.relocateBottomPanelChildren();*/
    }

    protected void addRow(int terminalNumber, int clientNumber){
        /*
        fixme: works incorrectly if deleteRow method is in action
         */
        /*
        fixme: when two or more requests come simultaneously sometimes
        some of the requested rows is not added
         */
        TerminalRow row = getTerminalRow(terminalNumber);
        if (row.state != TerminalRow.ACCEPTED){
            row.state = TerminalRow.ACCEPTED;
            System.err.println("TerminalRow with terminalNumber=" + row.terminalNumber +
                    "was asked to perform SlideUp animation. But its state was not equal to: ACCEPTED. " +
                    "We've set this value manually" );

        }
        row.clientNumber = clientNumber;
        //row.levelIndex = getUSEDLevels();
        row.levelIndex = setLevelsToBeUsed(false);
        long duration = 1000*(APP.LEVEL_QUANTITY - row.levelIndex +2)/APP.LEVEL_QUANTITY;
        float yDestination = terminalHeightOffsets[row.levelIndex] * onePercentHeight;
        float yStartingPoint = panelHeight + (onePercentWidth * 17);
        relocateTerminalRows();
        row.setVisible(true);

        TextDrawable drawable = row.drawables[0];
        ObjectAnimator clientToDelete = ObjectAnimator.ofFloat(drawable, "y", yStartingPoint,
                yDestination).setDuration(duration);
        drawable = row.drawables[1];
        ObjectAnimator arrowToDelete = ObjectAnimator.ofFloat(drawable, "y", yStartingPoint,
                yDestination).setDuration(duration);
        drawable = row.drawables[2];
        ObjectAnimator terminalToDelete = ObjectAnimator.ofFloat(drawable, "y", yStartingPoint,
                yDestination).setDuration(duration);
        clientToDelete.addUpdateListener(this);
        AnimatorSet addRowAnimation = new AnimatorSet();
        addRowAnimation.playTogether(clientToDelete, arrowToDelete, terminalToDelete);
        addRowAnimation.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                Log.d(TAG, "addRow: addRow Animation Started");
                ++addRowAnimationsInProgress;
                animationIsInProgress = true;
                setLevelsToBeUsed(true, true);
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //System.out.println("Delete Animation Finished.");
                --addRowAnimationsInProgress;
                incrementUSEDLevels();
                Log.d(TAG, "addRow: addRow Animation Finished. USEDLevels = " + getUSEDLevels());
                animationIsInProgress = false;
                performNextAnimation();
            }
        });
        addRowAnimation.start();

        /*row.performAnimation(panelWidth, panelHeight, onePercentHeight);*/
        this.listener.relocateBottomPanelChildren();
        this.listener.playNotificationSound();
    }

    protected void deleteRow(int terminalNumber){
        /*
        fixme: when two or more requests come simultaneously sometimes
        some of the requested rows is not deleted
         */
        TerminalRow row = getTerminalRow(terminalNumber);
        if (row == null) return;
        if (row.state != TerminalRow.WAITING){
            /*
            fixme: On a server side this state is not set properly
             */
            row.state = TerminalRow.WAITING;
            System.err.println("TerminalRow with terminalNumber=" + row.terminalNumber +
                    "was asked to perform SlideAside animation. But its state was not equal to: WAITING. " +
                    "We've set this value manually" );

        }
        //row.performAnimation(panelWidth, panelHeight, onePercentHeight);

        /*DeleteAnimatorSet deleteAnimation = new DeleteAnimatorSet(row);
        deleteAnimation.addDeleteAnimatorSetListener(new DeleteAnimatorSet.DeleteAnimatorSetListener() {
            @Override
            public void onAnimationEnd(Object object) {
                System.out.println("Delete Animation Finished.");
                deleteRowQueue.onAnimationEnd();
                TerminalRow r = (TerminalRow)object;
                r.setVisible(false);
                performNextAnimation();
            }
        });*/
        TextDrawable drawable = row.drawables[0];
        int duration = 500;
        ObjectAnimator clientToDelete = ObjectAnimator.ofFloat(drawable, "x", drawable.getX(),
                panelWidth + (widthOffsets[0] * onePercentWidth)).setDuration(duration);
        drawable = row.drawables[1];
        ObjectAnimator arrowToDelete = ObjectAnimator.ofFloat(drawable, "x", drawable.getX(),
                panelWidth + (widthOffsets[1] * onePercentWidth)).setDuration(duration);
        drawable = row.drawables[2];
        ObjectAnimator terminalToDelete = ObjectAnimator.ofFloat(drawable, "x", drawable.getX(),
                panelWidth + (widthOffsets[2] * onePercentWidth)).setDuration(duration);
        clientToDelete.addUpdateListener(this);
        SlideAsideAnimatorSetAdapter slideAside = new SlideAsideAnimatorSetAdapter(new AnimatorSet(), row);
        slideAside.playTogether(clientToDelete, arrowToDelete, terminalToDelete);
        slideAside.addSlideAsideListener(new SlideAsideAnimatorSetAdapter.SlideAsideListener() {
            @Override
            public void onAnimationStart(TerminalRow row) {
                Log.d(TAG, "deleteRow: Slide Aside Animation Started");
                animationIsInProgress = true;
                setLevelsToBeUsed(true, false);
            }

            @Override
            public void onAnimationEnd(TerminalRow row) {
                decrementUSEDLevels();
                Log.d(TAG, "deleteRow: Slide Aside Animation Finished. USEDLevels = " + getUSEDLevels());
                int levelIndex = row.levelIndex;
                row.levelIndex = -1;
                row.state = TerminalData.ACCEPTED;
                row.setVisible(false);
                slideUpRows(levelIndex);
            }
        });

        slideAside.start();



        /*AnimatorSet deleteAnimation = new AnimatorSet();



        deleteAnimation.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                Log.d(TAG, "deleteRow: Delete Animation Started");
                animationIsInProgress = true;
                setLevelsToBeUsed(true, false);
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //System.out.println("Delete Animation Finished.");
                Log.d(TAG, "deleteRow: Delete Animation Finished.");
                animationIsInProgress = false;
                performNextAnimation();
            }
        });
        deleteAnimation.start();*/

    }

    private void slideUpRows(int levelIndex){
        List<TerminalRow> slideUPRows = new ArrayList<>();
        for (TerminalRow r : table){
            if (r.levelIndex > levelIndex){
                slideUPRows.add(r);
            }
        }

        if (slideUPRows.size()>0){
            Collections.sort(slideUPRows);//This line is principle for next line correct work
            List<ObjectAnimator> animations = new ArrayList<>();
            for (TerminalRow r : slideUPRows){
                int level = r.levelIndex;
                r.levelIndex = level - 1;
                float yDestination = terminalHeightOffsets[level - 1] * onePercentHeight;
                TextDrawable drawable = r.drawables[0];
                animations.add(ObjectAnimator.ofFloat(drawable, "y", drawable.getY(),
                        yDestination).setDuration(500));
                drawable = r.drawables[1];
                animations.add(ObjectAnimator.ofFloat(drawable, "y", drawable.getY(),
                        yDestination).setDuration(500));
                drawable = r.drawables[2];
                animations.add(ObjectAnimator.ofFloat(drawable, "y", drawable.getY(),
                        yDestination).setDuration(500));
            }

            ObjectAnimator animator = animations.get(0);
            animator.addUpdateListener(this);
            AnimatorSet slideUP = new AnimatorSet();
            AnimatorSet.Builder builder = slideUP.play(animator);
            for (int i=1; i<animations.size(); ++i){
                builder.with(animations.get(i));
            }
            slideUP.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    Log.d(TAG, "slideUpRows: slideUpRows Animation Started");
                    /*animationIsInProgress = true;
                    setLevelsToBeUsed(true, false);*/
                    super.onAnimationStart(animation);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    //System.out.println("Delete Animation Finished.");
                    Log.d(TAG, "slideUpRows: slideUpRows Animation Finished.");
                    //animationIsInProgress = false;
                    animationIsInProgress = false;
                    performNextAnimation();
                }
            });
            slideUP.start();
        }else{
            animationIsInProgress = false;
            performNextAnimation();
        }
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
        h_loc = onePercentHeight*8;
        l_clientTitle.setX(w_loc);
        l_clientTitle.setY(h_loc);

        l_terminalTitle.setFontSize(titleHeight);
        stringWidth = l_terminalTitle.getTextWidth();
        w_loc = (onePercentWidth * widthOffsets[2]) - (stringWidth / 2);
        h_loc = onePercentHeight*8;
        l_terminalTitle.setX(w_loc);
        l_terminalTitle.setY(h_loc);

    }

    private void relocateTerminalRows(){
        int fontHeight = onePercentHeight * 16;

        for (TerminalRow r : table){
            for (int i=0; i<3; ++i){
                TextDrawable drawable = r.drawables[i];
                drawable.setFontSize(fontHeight);
                int stringWidth = drawable.getTextWidth();
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
                drawable.setFontSize(fontHeight);
                int stringWidth = drawable.getTextWidth();
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

    public synchronized void incrementUSEDLevels(){
        ++USEDLevels;
    }

    public synchronized void decrementUSEDLevels()throws RuntimeException{
        if (USEDLevels>0) {
            --USEDLevels;
        }else{
            throw new RuntimeException("Trying to assign to USEDLevels negative value");
        }
    }

    public synchronized void setUSEDLevels(int val){
        USEDLevels = val;
    }

    public synchronized int getUSEDLevels(){
        return USEDLevels;
    }

    /**
     * This method operates with {@link #levelsToBeUSED}. Set FALSE to the first param
     * if you want just get it's value.
     * @param set Specifies whether this method is called to override {@link #levelsToBeUSED} (TRUE)
     *            or just return it's current value (FALSE).
     * @param forAddingRows Optional. Additional flag, that defines in which direction {@link #levelsToBeUSED}
     *                      has to be changed: <ul>
     *                      <li>for adding a row (+) = true</li>
     *                      <li>for deleting a row (-) = false</li>
     *                      </ul>
     *                      Must be set only if the first parameter = true.
     * @return The value of {@link #levelsToBeUSED}.
     * @throws RuntimeException if the second param is null when the first is TRUE
     */
    public synchronized int setLevelsToBeUsed(boolean set, boolean... forAddingRows)throws RuntimeException{
        if (!set) return levelsToBeUSED;
        if (forAddingRows == null) throw new RuntimeException(
                "NullPointerException In method setLevelsToBeUsed (second argument)");
        int baseValue = (animationIsInProgress || addRowAnimationsInProgress>0) ? levelsToBeUSED : getUSEDLevels();
        if (forAddingRows[0]) {
            levelsToBeUSED = baseValue + 1;
        }else{
            levelsToBeUSED = baseValue - 1;
        }
        Log.d(TAG, "setLevelsToBeUsed: levelsToBeUSED = " + levelsToBeUSED);
        return levelsToBeUSED;
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

    protected void assignClient(int terminalNumber, int clientNumber){
        //addRow(terminalNumber, clientNumber);
        tableAnimationQueue.offer(TableAnimationQueue.OPERATION_ADD, terminalNumber, clientNumber);
        Log.d(TAG, "assignClient: tableAnimationQueue size = " + tableAnimationQueue.getSize());
    }

    protected void acceptClient(int terminal){
        /*deleteRowQueue.offer(terminal);
        Log.d(TAG, "acceptClient: deleteRowQueue size = " + deleteRowQueue.getSize());*/
        tableAnimationQueue.offer(TableAnimationQueue.OPERATION_DELETE, terminal);
        Log.d(TAG, "acceptClient: tableAnimationQueue size = " + tableAnimationQueue.getSize());
    }

    protected void performNextAnimation(){
        if (!animationIsInProgress) {
            //int terminalNumber = deleteRowQueue.poll();
            HashMap<String, Integer> rowData = tableAnimationQueue.poll();
            if (rowData != null) {
                int terminalNumber;
                switch (rowData.get(TableAnimationQueue.KEY_ANIMATION)){
                    case TableAnimationQueue.OPERATION_DELETE:
                        //System.out.println("Trying to delete row " + terminalNumber);
                        terminalNumber = rowData.get(TableAnimationQueue.KEY_TERMINAL);
                        Log.d(TAG, "performNextAnimation: Trying to DELETE row = " + terminalNumber);
                        deleteRow(terminalNumber);
                        break;
                    case TableAnimationQueue.OPERATION_ADD:
                        terminalNumber = rowData.get(TableAnimationQueue.KEY_TERMINAL);
                        int clientNumber = rowData.get(TableAnimationQueue.KEY_CLIENT);
                        Log.d(TAG, "performNextAnimation: Trying to ADD row = " +
                                terminalNumber + " client = " + clientNumber);
                        addRow(terminalNumber, clientNumber);
                        break;
                    default:
                        Log.e(TAG, "rowData.get(TableAnimationQueue.KEY_ANIMATION) returned unknown value: "
                                + rowData.get(TableAnimationQueue.KEY_ANIMATION));
                        break;
                }
            } else {
                Log.d(TAG, "performNextAnimation: tableAnimationQueue size = " + tableAnimationQueue.getSize());
            }
        }
    }

    private void redraw() {
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (tableIsValid) {
            /*canvas.drawColor(Color.DKGRAY);
            canvas.drawLine(0f, 0f, panelWidth, panelHeight, paint);*/
        /*
        Shifting the canvas so that next drawing object's location (x and y)
         is on screen's x=0, y=0 coordinates
        */
            canvas.translate(l_clientTitle.getX(), l_clientTitle.getY());
        /*
            draw the object with object.x and object.y arguments = 0
             */
            l_clientTitle.draw(canvas);
            canvas.restore();
            canvas.save();

            canvas.translate(l_terminalTitle.getX(), l_terminalTitle.getY());
            l_terminalTitle.draw(canvas);
            canvas.restore();
            canvas.save();

            for (TerminalRow row : table) {
                for (int i = 0; i < 3; ++i) {
                    TextDrawable drawable = row.drawables[i];
                    canvas.translate(drawable.getX(), drawable.getY());
                    drawable.draw(canvas);
                    canvas.restore();
                    canvas.save();
                }
            }
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        redraw();
    }

    @Override
    public void onGlobalLayout() {
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
        panelWidth = getMeasuredWidth();
        panelHeight = getMeasuredHeight();
        Log.d(TAG, "THE REAL panelHeight = " + panelHeight +
                " panelWidth = " + panelWidth);
        onePercentWidth = panelWidth / 100;
        onePercentHeight = panelHeight / 100;
        layoutDimensionsAreValid = true;

        if (requestedINIT){
            relocateTitles();
            initClients(tempTerminalRows);
            //tempTerminalRows = null;
        }
    }

    TableViewlListener listener;

    public void addTableViewListener(TableViewlListener listener) throws Exception {
        if (this.listener != null)
            throw new Exception("TablePanelListener has been already assigned");
        this.listener = listener;
    }

    /*@Override
    public void onDeleteRowQueueInit() {
        performNextAnimation();
    }*/

    @Override
    public void onTableAnimationQueueInit() {
        if (!animationIsInProgress) performNextAnimation();
    }

    public interface TableViewlListener{
        public void relocateBottomPanelChildren();
        public void sendToServer(DisplayMessage message);
        //public Dimension getMediaContentPanelSize();
        public void submitAction(int keyCode);
        public void playNotificationSound();
        //public List<JLabel> getTableTitleLabels();
    }
}
