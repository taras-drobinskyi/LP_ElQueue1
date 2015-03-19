/*
 * Copyright (c) 2015. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import com.logosprog.elqdisplay.fragments.tableutils.TextDrawable;
import com.logosprog.elqdisplay.fragments.tableutils.TickerMessageAnimatorSetAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by forando on 17.03.15.
 */
public class TickerMessageView extends View implements ValueAnimator.AnimatorUpdateListener,
        ViewTreeObserver.OnGlobalLayoutListener {

    private final String TAG = getClass().getSimpleName();

    private List<String> messages;
    private TextDrawable drawable;
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

        /*
        We need next three lines only for this view initialization.
        Without them first system call to this.onDraw() will through
        a NullPointer exception.
         */
        drawable = new TextDrawable("");
        drawable.setX(0f);
        drawable.setY(0f);
    }

    public void init(){
        requestedINIT = true;

        if (layoutDimensionsAreValid){
            initTickerAnimation();
        }
    }

    private void initTickerAnimation(){
        runAnimation(0);
        Log.d(TAG, "initTickerAnimation: Exiting method.");
    }

    private void runAnimation(int index){
        int duration = 8000;
        drawable = new TextDrawable(messages.get(index));
        float fontHeight = panelHeight/1.5f;
        drawable.setFontSize(fontHeight);
        drawable.setY(panelHeight/2 + fontHeight/2);
        drawable.setX(panelWidth + 10);

        ObjectAnimator slidingAnimator = ObjectAnimator.ofFloat(drawable, "x", drawable.getX(),
                0 - drawable.getTextWidth() -10).setDuration(duration);
        slidingAnimator.setInterpolator(null);
        slidingAnimator.addUpdateListener(this);
        TickerMessageAnimatorSetAdapter animatorAdapter = new TickerMessageAnimatorSetAdapter(new AnimatorSet(), index);
        animatorAdapter.playTogether(slidingAnimator);
        animatorAdapter.addTickerMessageSlideAsideListener(new TickerMessageAnimatorSetAdapter.TickerMessageSlideAsideListener() {
            @Override
            public void onAnimationStart(int index) {
                Log.d(TAG, "slidingAnimator: Slide Aside Animation Started");
            }

            @Override
            public void onAnimationEnd(int index) {
                Log.d(TAG, "slidingAnimator: Slide Aside Animation Finished");
                if (index +1 <messages.size()){
                    runAnimation(index + 1);
                }else{
                    runAnimation(0);
                }
            }
        });

        animatorAdapter.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(drawable.getX(), drawable.getY());
        drawable.draw(canvas);
        canvas.save();
        //canvas.translate(0-drawable.getX(), 0-drawable.getY());
        canvas.restore();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        invalidate();
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
