/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments.tableutils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;

/**
 * This class wraps {@link android.animation.AnimatorSet} object.
 * It adapts {@link android.animation.Animator.AnimatorListener}
 * interface returning for further proceeding
 * {@link com.logosprog.elqdisplay.fragments.tableutils.TerminalRow} object,
 * that has just been removed from the table.<br>
 *
 * Created by forando on 29.12.14.
 */
public class SlideAsideAnimatorSetAdapter {

    /**
     * An object to be returned after
     * {@link android.animation.Animator.AnimatorListener}
     * is called.<br>
     * The whole point of the
     * {@link com.logosprog.elqdisplay.fragments.tableutils.SlideAsideAnimatorSetAdapter}
     * is to return, when needed, this object.
     */
    private TerminalRow row;
    /**
     * wrapping object
     */
    private AnimatorSet animator;

    /**
     * An adapter listener. <br> Adapts
     * {@link android.animation.Animator.AnimatorListener} calls.
     */
    SlideAsideListener listener;

    public SlideAsideAnimatorSetAdapter(AnimatorSet animator, TerminalRow row){
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
