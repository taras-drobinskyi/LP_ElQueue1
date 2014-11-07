/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import com.logosprog.elqdisplay.fragments.tableutils.TextDrawable;

import java.util.ArrayList;

/**
 * Created by forando on 02.11.14.
 */
public class MyAnimationView extends View implements ValueAnimator.AnimatorUpdateListener {

    final static String TAG = "MyAnimationView";

    public final ArrayList<TextDrawable> textList = new ArrayList<>();
    AnimatorSet animation = null;
    ObjectAnimator anim2 = null;
    private float mDensity;

    public MyAnimationView(Context context) {
        super(context);

        mDensity = getContext().getResources().getDisplayMetrics().density;

        TextDrawable text0 = addTextView(50f, 25f);
        TextDrawable text1 = addTextView(150f, 25f);
        TextDrawable text2 = addTextView(250f, 25f);
        TextDrawable text3 = addTextView(350f, 25f);
    }

    private void createTextAnimation() {
        if (animation == null) {
            textList.get(0).setVisible(false,false);
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(textList.get(0), "y",
                    0f, getHeight() - textList.get(0).getHeight()).setDuration(1500);
            anim2 = anim1.clone();
            anim2.setTarget(textList.get(1));
            anim1.addUpdateListener(this);

            TextDrawable text2 = textList.get(2);
            ObjectAnimator animDown = ObjectAnimator.ofFloat(text2, "y",
                    0f, getHeight() - text2.getHeight()).setDuration(500);
            animDown.setInterpolator(new AccelerateInterpolator());
            ObjectAnimator animUp = ObjectAnimator.ofFloat(text2, "y",
                    getHeight() - text2.getHeight(), 0f).setDuration(1000);
            animUp.setInterpolator(new DecelerateInterpolator());
            AnimatorSet s1 = new AnimatorSet();
            s1.playSequentially(animDown, animUp);
            animDown.addUpdateListener(this);
            animUp.addUpdateListener(this);
            AnimatorSet s2 = (AnimatorSet) s1.clone();
            s2.setTarget(textList.get(3));

            animation = new AnimatorSet();
            animation.playTogether(anim1, anim2, s1);
            animation.playSequentially(s1, s2);

            animation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    Log.d(TAG, "isStarted = " + animator.isStarted() + " isRunning = " + animator.isRunning());
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    Log.d(TAG, "isStarted = " + animator.isStarted() + " isRunning = " + animator.isRunning());
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }
    }

    private TextDrawable addTextView(float x, float y) {
        TextDrawable textDrawable = new TextDrawable("test");
        textDrawable.setX(x - 25f);
        textDrawable.setY(y - 25f);
        textList.add(textDrawable);
        return textDrawable;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < textList.size(); ++i) {
            TextDrawable textDrawable = textList.get(i);
            canvas.save();
            /*
            Shifting the canvas so that next drawing object's location (x and y)
             is on screen's x=0, y=0 coordinates
             */
            canvas.translate(textDrawable.getX(), textDrawable.getY());
            /*
            draw the object with object.x and object.y arguments = 0
             */
            textDrawable.draw(canvas);
            canvas.restore();
        }
    }

    public void startAnimation() {
        //createAnimation();
        createTextAnimation();
        animation.start();
        ObjectAnimator animRight = ObjectAnimator.ofFloat(textList.get(1), "x",
                textList.get(1).getX(), getWidth() - 100f).setDuration(4000);
        //animRight.addUpdateListener(this);
        animRight.start();
    }

    public void onAnimationUpdate(ValueAnimator animation) {
        invalidate();
    }

}
