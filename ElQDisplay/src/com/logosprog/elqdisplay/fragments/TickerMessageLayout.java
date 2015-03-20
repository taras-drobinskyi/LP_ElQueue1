/*
 * Copyright (c) 2015. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import display.TerminalData;

import java.util.List;

/**
 * Created by forando on 17.03.15.<br>
 *     This class is a direct child to Main Activity class ({@link com.logosprog.elqdisplay.FullscreenActivity})
 */
public class TickerMessageLayout extends MainActivityFragment {

    private final String TAG = getClass().getSimpleName();

    private TickerMessageView tickerMessageView;


    public TickerMessageLayout() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        tickerMessageView = new TickerMessageView(activityContext);
        return tickerMessageView;
    }

    @Override
    public void onInitTable(List<TerminalData> terminals, int restOfClients) {
        tickerMessageView.init();
    }
}
