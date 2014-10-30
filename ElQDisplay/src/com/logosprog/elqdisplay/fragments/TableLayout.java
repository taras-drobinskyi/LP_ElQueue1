/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
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

/**
 * Created by forando on 30.10.14.
 */
public class TableLayout extends Fragment implements MainActivityDelegate {

    Context activityContext;

    TableView tableView;

    public TableLayout(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityContext = this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        tableView = new TableView(activityContext);

        return tableView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //tableView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //tableView.stop();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final MainActivityController controller = (MainActivityController) getActivity();
        controller.onAttachDelegate(this);
    }

    @Override
    public void onAssignClient(int terminal, int client) {

    }

    @Override
    public void onDetachClient(int terminal, int client) {

    }
}