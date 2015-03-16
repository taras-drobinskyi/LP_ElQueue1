/*
 * Copyright (c) 2015. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import display.TerminalData;

import java.util.List;

/**
 * Created by forando on 13.03.15.<br>
 * This class is a direct child to Main Activity class ({@link com.logosprog.elqdisplay.FullscreenActivity})
 */
public class BottomLineLayout extends MainActivityFragment {

    private final String TAG = getClass().getSimpleName();

    /**
     * Indicates that the text line shows restOfClients value
     */
    public static final int STATE_TEXT_REST_OF_CLIENTS = 0;
    /**
     * Indicates that the text line shows "take ticket" message
     */
    public static final int STATE_TEXT_TAKE_TICKET = 1;
    /**
     * Indicates that the text line shows "Insert Paper" message
     */
    public static final int STATE_TEXT_PRINTER_ERROR = 2;

    private int textState = STATE_TEXT_REST_OF_CLIENTS;
    private int restOfClients = 0;

    BottomLineView bottomLineView;

    public BottomLineLayout(){
        // Required empty public constructor
    }

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new NormalWorkRunnable();

    private void setTextMessage(String text){
        if (bottomLineView != null){
            bottomLineView.setText(text);
        }else {
            Log.e(TAG, "Cannot set restOfClients value. bottomLineView is NULL.");
        }
    }

    private void reInitAnimation(){
        timerHandler.removeCallbacks(timerRunnable);
        textState = STATE_TEXT_TAKE_TICKET;
        timerHandler.postDelayed(timerRunnable, 0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bottomLineView = new BottomLineView(activityContext);
        return bottomLineView;
    }

    @Override
    public void onInitTable(List<TerminalData> terminals, int restOfClients) {
        setTextMessage("Всего в очереди: " + restOfClients);
        this.restOfClients = restOfClients;
        reInitAnimation();
    }

    @Override
    public void onPrintTicket(int restOfClients) {
        setTextMessage("Всего в очереди: " + restOfClients);
        this.restOfClients = restOfClients;
        reInitAnimation();
    }

    private class NormalWorkRunnable implements Runnable{

        @Override
        public void run() {
            switch (textState){
                case STATE_TEXT_REST_OF_CLIENTS:
                    textState = STATE_TEXT_TAKE_TICKET;
                    setTextMessage("Возьмите Талон");
                    break;
                case STATE_TEXT_TAKE_TICKET:
                    textState = STATE_TEXT_REST_OF_CLIENTS;
                    setTextMessage("Всего в очереди: " + restOfClients);
                    break;
                default:
                    break;
            }
            timerHandler.postDelayed(this, 2000);
        }
    }

    private class PrinterErrorRunnable implements Runnable{

        @Override
        public void run() {
            switch (textState){
                case STATE_TEXT_REST_OF_CLIENTS:
                    textState = STATE_TEXT_PRINTER_ERROR;
                    setTextMessage("Вставте Бумагу!");
                    break;
                case STATE_TEXT_PRINTER_ERROR:
                    textState = STATE_TEXT_REST_OF_CLIENTS;
                    setTextMessage("Всего в очереди: " + restOfClients);
                    break;
                default:
                    break;
            }
            timerHandler.postDelayed(this, 2000);
        }
    }
}