/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments.tableutils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;

/**
 * Created by forando on 29.12.14.
 */
public class SlideAsideAnimatorSetWrapper {

    private TerminalRow row;
    private AnimatorSet animator;
    SlideAsideListener listener;

    public SlideAsideAnimatorSetWrapper(AnimatorSet animator, TerminalRow row){
        this.animator = animator;
        this.row = row;
    }

    public void addSlideAsideListener(final SlideAsideListener listener){
        this.listener = listener;
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                listener.onAnimationEnd(row);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                listener.onAnimationStart(row);
                super.onAnimationStart(animation);
            }
        });
    }

    public void playTogether(Animator... items) {
        animator.playTogether(items);
    }

    public Animator getAnimator(){return animator;}

    public interface SlideAsideListener{
        void onAnimationStart(TerminalRow row);

        void onAnimationEnd(TerminalRow row);
    }
}
