/*
 * Copyright (c) 2015. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments.tableutils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;

/**
 * Created by forando on 17.03.15.<br>
 * This class wraps {@link android.animation.AnimatorSet} object.
 * It adapts {@link android.animation.Animator.AnimatorListener}
 * interface returning for further proceeding an index of animated message,
 */
public class TickerMessageAnimatorSetAdapter {

    /**
     * An index to be returned after
     * {@link android.animation.Animator.AnimatorListener}
     * is called.<br>
     * The whole point of the
     * {@link com.logosprog.elqdisplay.fragments.tableutils.TickerMessageAnimatorSetAdapter}
     * is to return, when needed, this index.
     */
    private int index;
    /**
     * wrapping object
     */
    private AnimatorSet animator;

    /**
     * An adapter listener. <br> Adapts
     * {@link android.animation.Animator.AnimatorListener} calls.
     */
    TickerMessageSlideAsideListener listener;

    public TickerMessageAnimatorSetAdapter(AnimatorSet animator, int index){
        this.animator = animator;
        this.index = index;
    }

    public void addTickerMessageSlideAsideListener(final TickerMessageSlideAsideListener listener){
        this.listener = listener;
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                listener.onAnimationEnd(index);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                listener.onAnimationStart(index);
                super.onAnimationStart(animation);
            }
        });
    }

    public void playTogether(Animator... items) {
        animator.playTogether(items);
    }

    public Animator getAnimator(){return animator;}

    /**
     * See {@link android.animation.AnimatorSet#start}
     */
    public void start(){
        animator.start();
    }

    public interface TickerMessageSlideAsideListener{
        void onAnimationStart(int index);

        void onAnimationEnd(int index);
    }
}
