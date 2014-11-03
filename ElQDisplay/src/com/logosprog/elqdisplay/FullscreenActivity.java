/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay;

import android.content.Context;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import client.ClientConnectorProvider;
import client.ClientServer;
import com.logosprog.elqdisplay.fragments.ClientLayout;
import com.logosprog.elqdisplay.fragments.TableLayout;
import com.logosprog.elqdisplay.fragments.VideoLayout;
import com.logosprog.elqdisplay.interfaces.MainActivityController;
import com.logosprog.elqdisplay.interfaces.MainActivityDelegate;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import sockets.DisplayMessage;
import sockets.SocketMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends ActivityBase implements MainActivityController, ClientServer.ClientServerListener {
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

    int counter = 0;

    ClientServer clientServer;

    ClientServer.ClientServerListener clientServerListeners;

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

        /*try {
            Runtime.getRuntime().exec(new String[]{"su","-c","service call activity 79 s16 com.android.systemui"});
            Log.e(TAG, "--------Working!!!--------");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "ERRRRRRROOORRRRR!!!");
        }*/

        createDirIfNotExists();

        setContentView(R.layout.activity_main);
        //setContentView(new ClientView(this));

        /*final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);*/
        controlsView = findViewById(R.id.frame_client);
        final View contentView = findViewById(R.id.frame_video);

        final FragmentManager manager = getFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragmentClient = new ClientLayout();
        Fragment fragmentTable = new TableLayout();
        Fragment fragmentVideo = VideoLayout.newInstance(false);
        //transaction.replace(R.id.frame_video, fragmentVideo, "fragmentVideo");
        transaction.replace(R.id.frame_client, fragmentClient, "fragmentClient");
        transaction.replace(R.id.frame_table, fragmentTable, "fragmentTable");
        transaction.commit();

        /*VideoView video = (VideoView) findViewById(R.id.frame_video);
        Uri vid_Uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.test);
        video.setVideoURI(vid_Uri);
        try{
            video.start();
        }catch (Exception ex){
            ex.printStackTrace();
        }*/


        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.



        /*mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_longAnimTime);
                            }
                            if (!visible){
                                counter++;
                                for (MainActivityDelegate delegate : delegates){
                                    delegate.onAssignClient(counter, counter);
                                }
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });*/

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                    counter++;
                    for (MainActivityDelegate delegate : delegates){
                        delegate.onAssignClient(counter, counter);
                    }
                }*/
                counter++;

                for (MainActivityDelegate delegate : delegates){
                    delegate.onAssignClient(counter, counter);
                }

                controlsView.animate()
                        .translationY(0)
                        .setDuration(getResources().getInteger(
                                android.R.integer.config_longAnimTime));
                delayedHide(AUTO_HIDE_DELAY_MILLIS);


                /*Animation a = new TranslateAnimation(0.0f, 0.0f, target.getHeight() + targetParent.getPaddingBottom(),
                        -(targetParent.getHeight() - target.getHeight() -
                                targetParent.getPaddingTop() - targetParent.getPaddingBottom()));
                a.setDuration(1000);
                a.setStartOffset(300);

                a.setInterpolator(AnimationUtils.loadInterpolator(context, android.R.anim.decelerate_interpolator));
                target.startAnimation(a);*/
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        this.clientServerListeners = this;
        startClientServer();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    //private int clientViewHeight;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);

        /*Animation b = new TranslateAnimation(0.0f, 0.0f, targetParent.getHeight() - target.getHeight() -
        targetParent.getPaddingTop() - targetParent.getPaddingBottom(), 0.0f);

        Animation a = new TranslateAnimation(0.0f,
                targetParent.getWidth() - target.getWidth() - targetParent.getPaddingLeft() -
                        targetParent.getPaddingRight(), 0.0f, 0.0f);

        a.setDuration(1000);
        a.setStartOffset(300);
        a.setRepeatMode(Animation.RESTART);
        a.setRepeatCount(Animation.INFINITE);
        a.setInterpolator(AnimationUtils.loadInterpolator(this,
                android.R.anim.decelerate_interpolator));
        target.startAnimation(a);*/



    }

    private void assignClientServer(ClientServer client){
        this.clientServer = client;
        this.id = client.id;
        this.clientServer.send(new DisplayMessage(id, DisplayMessage.SOCKET_READY, null, 0, new Date(), true));
    }

    private void startClientServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ClientConnectorProvider clientConnectorProvider = new ClientConnectorProvider(clientServerListeners, SocketMessage.DISPLAY, id);
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
        }).start();
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    /*View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };*/
    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            //mSystemUiHider.hide();
            controlsView.animate()
                    .translationY(controlsView.getHeight())
                    .setDuration(getResources().getInteger(
                            android.R.integer.config_longAnimTime));
            for (MainActivityDelegate delegate : delegates){
                delegate.onDetachClient(counter, counter);
            }
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
    public void onRegister(int id) {

    }

    @Override
    public void onInputMessage(Object object) {

    }

    @Override
    public void onCloseSocket() {
        startClientServer();
    }
}
