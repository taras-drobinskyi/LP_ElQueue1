/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by forando on 30.10.14.
 */
public class TablePanel extends SurfaceView implements Runnable {

    SurfaceHolder surfaceHolder;
    Thread thread = null;
    boolean isRunning = false;

    public TablePanel(Context context) {
        super(context);
        surfaceHolder = getHolder();
    }

    public void pause() {
        isRunning = false;
        while(true){
            try {
                thread.join();//killing the current thread
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            break;
        }
        thread = null;
    }

    public void resume() {
        isRunning = true;
        /*
        Basically we saying that our Thread will be assigned to run() method below
         */
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while (isRunning) {
            if (!surfaceHolder.getSurface().isValid()) {
                /*
                continue is like break in switch-case structure. It allows us
                continue our while loop without touching next lines
                 */
                continue;
            }
            /*
            next line means that we assign the surface to this canvas and right after
            that we locking the "door" to this surface, so that no one else can
            draw on it except this canvas.
             */
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawRGB(02, 02, 150);
            if(x != 0 && y != 0){
                canvas.drawBitmap(test, x-(test.getWidth()/2), y-(test.getHeight()/2), null);
            }
            if(sX != 0 && sY != 0){
                canvas.drawBitmap(plus, sX-(plus.getWidth()/2), sY-(plus.getHeight()/2), null);
            }
            if(fX != 0 && fY != 0){
                canvas.drawBitmap(test, fX-(test.getWidth()/2)-aniX, fY-(test.getHeight()/2)-aniY, null);
                canvas.drawBitmap(plus, fX-(plus.getWidth()/2), fY-(plus.getHeight()/2), null);
            }
            aniX = aniX + scaledX;
            aniY = aniY + scaledY;
            /*
            here we unlocking the "door" to the surface
            and display the canvas to the user
             */
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }
}
