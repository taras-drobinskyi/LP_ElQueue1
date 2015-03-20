/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay;

import android.util.Log;
import android.view.Window;
import client.ClientConnectorProvider;
import client.ClientServer;
import com.logosprog.elqdisplay.fragments.*;
import com.logosprog.elqdisplay.interfaces.MainActivityController;
import com.logosprog.elqdisplay.interfaces.MainActivityDelegate;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import main.APP;
import sockets.DisplayMessage;
import sockets.SocketMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends ActivityBase implements MainActivityController, ClientServer.ClientServerListener {

    private final String TAG = "FullscreenActivity";

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = false;


    View controlsView;

    ClientServer clientServer;

    ClientServer.ClientServerListener clientServerListener;

    private int id = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        /*Hide both the navigation bar and the status bar.
        SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        a general rule, you should design your app to hide the status bar whenever you
        hide the navigation bar.*/
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);

        createDirIfNotExists();

        setContentView(R.layout.activity_main);
        controlsView = findViewById(R.id.frame_client);

        final FragmentManager manager = getFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragmentClient = new ClientLayout();
        Fragment fragmentTable = new TableLayout();
        //Fragment fragmentVideo = VideoLayout.newInstance(false);
        Fragment fragmentSystem = new BottomLineLayout();
        Fragment fragmentTicker = new TickerMessageLayout();
        //transaction.replace(R.id.frame_video, fragmentVideo, "fragmentVideo");
        transaction.replace(R.id.frame_client, fragmentClient, "fragmentClient");
        transaction.replace(R.id.frame_table, fragmentTable, "fragmentTable");
        transaction.replace(R.id.frame_system, fragmentSystem, "fragmentSystem");
        transaction.replace(R.id.frame_messages, fragmentTicker, "fragmentTicker");
        transaction.commit();


        this.clientServerListener = this;
        startClientServer();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(200);
    }

    Handler assignClientHandler = new Handler();
    Runnable assignClientRunnable;

    private void assignClientServer(ClientServer client){
        if (assignClientRunnable != null) assignClientHandler.removeCallbacks(assignClientRunnable);
        assignClientRunnable = new AssignClientRunnable(client);
        assignClientHandler.post(assignClientRunnable);
    }

    private void startClientServer(){
        Thread clientServerConnectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ClientConnectorProvider clientConnectorProvider =
                        new ClientConnectorProvider(clientServerListener, SocketMessage.DISPLAY, id);
                try {
                    clientConnectorProvider.addClientConnectorListener(new ClientConnectorProvider.ClientConnectorListener() {
                        @Override
                        public void onClientConnected(ClientServer client) {
                            assignClientServer(client);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        clientServerConnectionThread.setName("ClientServer");
        clientServerConnectionThread.start();
    }


    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            controlsView.animate()
                    .translationY(controlsView.getHeight())
                    .setDuration(getResources().getInteger(
                            android.R.integer.config_longAnimTime));
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    List<MainActivityDelegate> delegates = new ArrayList<MainActivityDelegate>();

    @Override
    public void onAttachDelegate(MainActivityDelegate delegate) {
        delegates.add(delegate);
    }

    @Override
    public void onDetachDelegate(MainActivityDelegate delegate) {
        delegates.remove(delegate);
    }

    @Override
    public void onClientAssignAnimationStart(int terminal, int client) {
        for (MainActivityDelegate delegate : delegates) {
            delegate.onClientAssignAnimationStart(terminal, client);
        }
        controlsView.animate()
                .translationY(0)
                .setDuration(getResources().getInteger(
                        android.R.integer.config_longAnimTime));
        delayedHide(AUTO_HIDE_DELAY_MILLIS);
    }

    @Override
    public void onRegister(int id) {

    }

    @Override
    public void onInputMessage(Object object) {
        updateConversationHandler.post(new UpdateConversationRunnable(object));
    }

    @Override
    public void onCloseSocket() {

        startClientServer();
        //clientServer = null;
    }

    private class AssignClientRunnable implements Runnable{

        ClientServer client;

        public AssignClientRunnable(ClientServer client){
            this.client = client;
        }

        @Override
        public void run() {
            clientServer = client;
            id = client.id;
            clientServer.send(new DisplayMessage(id, DisplayMessage.SOCKET_READY, null, 0, new Date(), true));
        }
    }

    /**
     * We need this handler in order to perform UI initialize UI animation
     * within UI-Thread.
     */
    Handler updateConversationHandler = new Handler();

    private class UpdateConversationRunnable implements Runnable {
        Object object;
        public UpdateConversationRunnable(Object obj){
            this.object = obj;
        }

        @Override
        public void run() {
            DisplayMessage message = (DisplayMessage)object;
            switch (message.operation){
                case DisplayMessage.INIT_ROWS:
                    //System.out.println("Received message: INIT_ROWS");
                    Log.d(TAG, "Received message: INIT_ROWS");
                    for (MainActivityDelegate delegate : delegates){
                        delegate.onInitTable(message.terminals, message.restOfClients);
                    }
                    message.received = true;
                    clientServer.send(message);
                    break;
                case DisplayMessage.DELETE_ROW:
                    //System.out.println("Received message: DELETE_ROW");
                    Log.d(TAG, "Received message: DELETE_ROW terminal = " + message.terminals.get(0).terminalNumber);
                    for (MainActivityDelegate delegate : delegates){
                        delegate.onAcceptClient(message.terminals.get(0), message.restOfClients);
                    }
                    break;
                case DisplayMessage.ADD_ROW:
                    //System.out.println("Received message: ADD_ROW");
                    Log.d(TAG, "Received message: ADD_ROW terminal = " + message.terminals.get(0).terminalNumber +
                            " client = " + message.terminals.get(0).clientNumber);
                    for (MainActivityDelegate delegate : delegates){
                        delegate.onAssignClient(message.terminals.get(0), message.restOfClients);
                    }
                    break;
                case APP.PRINT_TICKET:
                    Log.d(TAG, "Received message: PRINT_TICKET restOfClients = " + message.restOfClients);
                    for (MainActivityDelegate delegate : delegates){
                        delegate.onPrintTicket(message.restOfClients);
                    }
                    break;
                case APP.PRINTER_ERROR_ON:
                    Log.d(TAG, "Received message: PRINTER_ERROR_ON");
                    for (MainActivityDelegate delegate : delegates){
                        delegate.onPrinterError(true);
                    }
                    break;
                case APP.PRINTER_ERROR_OFF:
                    Log.d(TAG, "Received message: PRINTER_ERROR_OFF");
                    for (MainActivityDelegate delegate : delegates){
                        delegate.onPrinterError(false);
                    }
                    break;
                case APP.STOP_SERVICE:
                    Log.d(TAG, "Received message: STOP_SERVICE");
                    for (MainActivityDelegate delegate : delegates){
                        delegate.onServiceChange(true);
                    }
                    break;
                case APP.RESET_SERVICE:
                    if (message.terminals != null){
                        Log.d(TAG, "Received message: REINIT_ROWS");
                        for (MainActivityDelegate delegate : delegates){
                            delegate.onInitTable(message.terminals, message.restOfClients);
                        }
                    }else{
                        Log.d(TAG, "Received message: RESET_SERVICE");
                        for (MainActivityDelegate delegate : delegates){
                            delegate.onServiceChange(false);
                        }
                    }
                    break;
                default:
                    Log.e(TAG, "updateConversationHandler: Client server has received a message, " +
                            "but message.operation has not been recognized. message.operation = " + message.operation);
                    break;
            }
        }
    }
}
