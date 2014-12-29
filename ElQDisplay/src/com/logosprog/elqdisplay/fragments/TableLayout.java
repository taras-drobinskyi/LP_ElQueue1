/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.logosprog.elqdisplay.fragments.tableutils.DeleteRowQueue;
import com.logosprog.elqdisplay.fragments.tableutils.TerminalRow;
import com.logosprog.elqdisplay.interfaces.MainActivityController;
import com.logosprog.elqdisplay.interfaces.MainActivityDelegate;
import display.TerminalData;
import sockets.DisplayMessage;

import java.util.List;

/**
 * Created by forando on 30.10.14.
 */
public class TableLayout extends Fragment implements MainActivityDelegate,
        TableView.TableViewlListener{

    private final String TAG = getClass().getSimpleName();

    Context activityContext;

    //MyAnimationView tableView;
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

        tableView = new TableView(activityContext, 0);
        try {
            tableView.addTableViewListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //tableView = new MyAnimationView(activityContext);

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
        final MainActivityController controller =
                (MainActivityController) getActivity();
        controller.onAttachDelegate(this);
    }

    @Override
    public void onAssignClient(TerminalData terminalRowData, int restOfClients) {
        int terminalNumber = terminalRowData.terminalNumber;
        tableView.assignClient(terminalNumber);
    }

    @Override
    public void onAcceptClient(TerminalData terminalRowData, int restOfClients) {
        int terminalNumber = terminalRowData.terminalNumber;
        tableView.acceptClient(terminalNumber);
    }

    @Override
    public void onInitTable(List<TerminalData> terminals, int restOfClients) {
        tableView.initTable(terminals, restOfClients);
    }

    @Override
    public void relocateBottomPanelChildren() {

    }

    @Override
    public void sendToServer(DisplayMessage message) {

    }

    @Override
    public void submitAction(int keyCode) {

    }

    @Override
    public void playNotificationSound() {

    }
}