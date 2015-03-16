/*
 * Copyright (c) 2015. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import com.logosprog.elqdisplay.interfaces.MainActivityController;
import com.logosprog.elqdisplay.interfaces.MainActivityDelegate;
import display.TerminalData;

import java.util.List;

/**
 * Created by forando on 13.03.15.
 */
public abstract class MainActivityFragment extends Fragment implements MainActivityDelegate {

    protected Context activityContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityContext = this.getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        this.onActivityCreated(savedInstanceState, true);
    }

    /**
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     * @param registerDelegate The flag to register this delegate to a Main Activity Controller.
     */
    protected void onActivityCreated(Bundle savedInstanceState, boolean registerDelegate){
        super.onActivityCreated(savedInstanceState);
        if (registerDelegate){
            final MainActivityController controller = (MainActivityController) getActivity();
            controller.onAttachDelegate(this);
        }
    }


    @Override
    public void onAssignClient(TerminalData terminalRowData, int restOfClients) {
        //dummy
    }

    @Override
    public void onAcceptClient(TerminalData terminalRowData, int restOfClients) {
        //dummy
    }

    @Override
    public void onInitTable(List<TerminalData> terminals, int restOfClients) {
        //dummy
    }

    @Override
    public void onClientAssignAnimationStart(int terminal, int client) {
        //dummy
    }
}
