/*
 * Copyright (c) 2015. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments;

import android.location.Location;
import android.os.Bundle;
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

    BottomLineView bottomLineView;

    public BottomLineLayout(){
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bottomLineView = new BottomLineView(activityContext);
        return bottomLineView;
    }

    @Override
    public void onInitTable(List<TerminalData> terminals, int restOfClients) {
        if (bottomLineView != null){
            bottomLineView.setRestOfClients(restOfClients);
        }else {
            Log.e(TAG, "Cannot set restOfClients value. bottomLineView is NULL.");
        }
    }

    @Override
    public void onPrintTicket(int restOfClients) {
        if (bottomLineView != null){
            bottomLineView.setRestOfClients(restOfClients);
        }else {
            Log.e(TAG, "Cannot set restOfClients value. bottomLineView is NULL.");
        }
    }
}