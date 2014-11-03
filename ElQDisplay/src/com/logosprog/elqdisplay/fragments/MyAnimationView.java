/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;

/**
 * Created by forando on 02.11.14.
 */
public class MyAnimationView extends View implements ValueAnimator.AnimatorUpdateListener {

    final static String TAG = "MyAnimationView";

    public final ArrayList<TextDrawable> textList = new ArrayList<>();
    AnimatorSet animation = null;
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
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(textList.get(0), "y",
                    0f, getHeight() - textList.get(0).getHeight()).setDuration(1500);
            ObjectAnimator anim2 = anim1.clone();
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
            canvas.translate(textDrawable.getX(), textDrawable.getY());
            textDrawable.draw(canvas);
            canvas.restore();
        }
    }

    public void startAnimation() {
        //createAnimation();
        createTextAnimation();
        animation.start();
    }

    public void onAnimationUpdate(ValueAnimator animation) {
        invalidate();
    }

}
