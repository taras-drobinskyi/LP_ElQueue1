/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by forando on 30.10.14.
 */
public class TableSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    TableViewDrawer tableViewDrawer;

    Typeface font;

    public TableSurfaceView(Context context) {
        super(context);
        //font=Typeface.createFromAsset(context.getAssets(), "G-Unit.ttf");
        getHolder().addCallback(this);
    }

    public void stop() {
        boolean retry = true;
        tableViewDrawer.setRunning(false);
        while(retry){
            try {
                tableViewDrawer.join();//killing the current thread
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void resume() {
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        tableViewDrawer = new TableViewDrawer(surfaceHolder);
        tableViewDrawer.setRunning(true);
        tableViewDrawer.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        stop();
    }

    private class TableViewDrawer extends Thread{

        static final String TAG = "TableViewDrawer";

        SurfaceHolder surfaceHolder;
        Thread thread = null;
        boolean isRunning = false;

        private long prevTime;

        int canvasWidth = 0;
        int canvasHeight = 0;

        private TableViewDrawer(SurfaceHolder surfaceHolder){
            this.surfaceHolder = surfaceHolder;
            prevTime = System.currentTimeMillis();

            Canvas canvas = null;
            try {
                // получаем объект Canvas и выполняем отрисовку
                canvas = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder) {
                    canvasWidth = canvas.getWidth();
                    canvasHeight = canvas.getHeight();
                }
            }
            finally {
                if (canvas != null) {
                    // отрисовка выполнена. выводим результат на экран
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }

        private void setRunning (boolean val){
            this.isRunning = val;
        }

        private void pause() {

            thread = null;
        }

        private void restart() {
            isRunning = true;
        /*
        Basically we saying that our Thread will be assigned to run() method below
         */
            thread = new Thread(this);
            thread.start();
        }

        @Override
        public void run() {
            int yPos = canvasHeight;

            Paint textPaint =new Paint();
            //textPaint.setARGB(50, 254, 10, 50);
            textPaint.setColor(Color.WHITE);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(100);
            textPaint.setTypeface(font);

            long startTime = System.currentTimeMillis();
            long delay = 33;
            int shift = 5;
            long estTime = ((canvasHeight/shift)*delay);
            Log.e(TAG, "Estimated time = " + estTime + "s");

            int i = 0;

            while (isRunning) {
                /*if (!surfaceHolder.getSurface().isValid()) {
                *//*
                continue is like break in switch-case structure. It allows us
                continue our while loop without touching next lines
                 *//*
                    continue;
                }*/


                // получаем текущее время и вычисляем разницу с предыдущим
                // сохраненным моментом времени
                /*long now = System.currentTimeMillis();
                long elapsedTime = now - prevTime;
                if (elapsedTime > 2){
                    // если прошло больше 30 миллисекунд - сохраним текущее время
                    // и повернем картинку на 2 градуса.
                    // точка вращения - центр картинки
                    prevTime = now;





                }*/



            /*if(x != 0 && y != 0){
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
            aniY = aniY + scaledY;*/


                yPos = yPos - shift;
                if ((yPos - shift) <= 0){
                    yPos = canvasHeight;
                    long realTime = (System.currentTimeMillis() - startTime);
                    startTime = System.currentTimeMillis();
                    Log.e(TAG, "Real time = " + realTime + "s");
                    Log.d(TAG, "Number of iterations = " + i);
                    i = 0;
                }else{
                    i++;
                }

                    /*
            next line means that we assign the surface to this canvas and right after
            that we locking the "door" to this surface, so that no one else can
            draw on it except this canvas.
             */
                Canvas canvas = surfaceHolder.lockCanvas();
                canvas.drawARGB(255, 125, 11, 54);
                //canvas.drawColor(Color.WHITE);

                canvas.drawText("103 > 3", canvasWidth / 2, yPos, textPaint);
                /*
            here we unlocking the "door" to the surface
            and display the canvas to the user
             */
                surfaceHolder.unlockCanvasAndPost(canvas);

                /*try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }
        }
    }
}
