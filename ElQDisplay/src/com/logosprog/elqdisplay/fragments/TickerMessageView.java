/*
 * Copyright (c) 2015. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import com.logosprog.elqdisplay.fragments.tableutils.TextDrawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by forando on 17.03.15.
 */
public class TickerMessageView extends View implements ValueAnimator.AnimatorUpdateListener,
        ViewTreeObserver.OnGlobalLayoutListener {

    private final String TAG = getClass().getSimpleName();

    private List<String> messages;
    private TextDrawable message;
    //private Paint paint;

    /**
     * Flag that indicates whether or not the init
     * method has been called.
     */
    private boolean requestedINIT = false;
    /**
     * Flag that indicates if the width and height of this View has been already
     * specified.
     */
    private boolean layoutDimensionsAreValid = false;

    private int panelWidth;
    private int panelHeight;
    private int onePercentWidth;
    private int onePercentHeight;


    public TickerMessageView(Context context) {
        super(context);

        /*
        Register for measuring layout height and width
         */
        getViewTreeObserver().addOnGlobalLayoutListener(this);

        messages = new ArrayList<>();
        messages.add("Это тестовый образец электронной очереди.");
        messages.add("Данное решение расчитано на 10 терминалов / окон.");
        message = new TextDrawable(messages.get(0));
        /*this.paint = new Paint();
        this.paint.setColor(Color.WHITE);
        this.paint.setStrokeWidth(5f);*/
    }

    private void init(){
        requestedINIT = true;

        if (layoutDimensionsAreValid){
            initTickerAnimation();
        }
    }

    private void initTickerAnimation(){

    }

    private void runAnimation(String textMessage){
        int duration = 4000;
        TextDrawable drawable = new TextDrawable(textMessage);
        ObjectAnimator slidingAnimator = ObjectAnimator.ofFloat(drawable, "x", panelWidth + (10 * onePercentWidth),
                0 - drawable.getTextWidth() - 10).setDuration(duration);
    }

    private void redraw() {
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(message.getX(), message.getY());
        message.draw(canvas);
        canvas.restore();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        redraw();
    }

    @Override
    public void onGlobalLayout() {
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
        panelWidth = getMeasuredWidth();
        panelHeight = getMeasuredHeight();
        Log.d(TAG, "Got TickerMessage View Dimensions! Height = " + panelHeight +
                " Width = " + panelWidth);
        onePercentWidth = panelWidth / 100;
        onePercentHeight = panelHeight / 100;
        layoutDimensionsAreValid = true;

        if (requestedINIT){
            initTickerAnimation();
        }
    }
}
