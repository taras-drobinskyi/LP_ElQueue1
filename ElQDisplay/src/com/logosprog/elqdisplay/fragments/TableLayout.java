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
        DeleteRowQueue.DeleteRowQueueListener, TableView.TableViewlListener
{

    Context activityContext;

    protected DeleteRowQueue deleteRowQueue;

    //MyAnimationView tableView;
    TableView tableView;

    public TableLayout(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityContext = this.getActivity();
        deleteRowQueue = new DeleteRowQueue();
        deleteRowQueue.addDeleteRowQueueListener(this);

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

    int i = 0;

    @Override
    public void onAssignClient(int terminal, int client) {
        deleteRowQueue.offer(terminal);
        ++i;
    }

    @Override
    public void onAcceptClient(TerminalData terminalRowData, int restOfClients) {
        /*int terminalNumber = terminalRowData.terminalNumber;
        deleteRowQueue.offer(terminalNumber);*/
    }

    @Override
    public void onInitTable(List<TerminalData> terminals, int restOfClients) {
        tableView.initTable(terminals, restOfClients);
    }

    private void deleteNextRow(){
        int terminalNumber = deleteRowQueue.poll();
        if (terminalNumber >= 0){
            System.out.println("Trying to delete row " + terminalNumber);
            tableView.deleteRow(terminalNumber);
        }
    }

    @Override
    public void onDeleteRowQueueInit() {
        deleteNextRow();
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

    @Override
    public void onDeleteAnimationFinished() {
        deleteNextRow();
    }
}