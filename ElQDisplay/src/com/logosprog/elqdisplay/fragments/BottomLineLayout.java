/*
 * Copyright (c) 2015. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.logosprog.elqdisplay.interfaces.MainActivityController;
import com.logosprog.elqdisplay.interfaces.MainActivityDelegate;
import display.TerminalData;

import java.util.List;

/**
 * Created by forando on 13.03.15.
 */
public class BottomLineLayout extends Fragment implements MainActivityDelegate {

    private final String TAG = getClass().getSimpleName();

    Context activityContext;

    BottomLineView bottomLineView;

    public BottomLineLayout(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityContext = this.getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bottomLineView = new BottomLineView(activityContext);
        return bottomLineView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final MainActivityController controller = (MainActivityController) getActivity();
        controller.onAttachDelegate(this);
    }

    @Override
    public void onAssignClient(TerminalData terminalRowData, int restOfClients) {

    }

    @Override
    public void onAcceptClient(TerminalData terminalRowData, int restOfClients) {

    }

    @Override
    public void onInitTable(List<TerminalData> terminals, int restOfClients) {

    }

    @Override
    public void onClientAssignAnimationStart(int terminal, int client) {

    }
}