/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments.tableutils;

import java.util.concurrent.*;

/**
 * Created by forando on 06.11.14.
 */
public class ScheduledExecutor  extends ScheduledThreadPoolExecutor {
    public ScheduledExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    static class BlinkingTask implements RunnableScheduledFuture{

        private RunnableScheduledFuture task;

        private Runnable r;
        private Callable c;

        protected BlinkingTask(Runnable r, RunnableScheduledFuture task){
            this.r = r;
            this.task = task;
        }

        protected BlinkingTask(Callable c, RunnableScheduledFuture task){
            this.c = c;
            this.task = task;
        }

        protected  RunnableScheduledFuture decorateTask(
                Runnable r, RunnableScheduledFuture task) {
            return new BlinkingTask(r, task);
        }

        protected  RunnableScheduledFuture decorateTask(
                Callable c, RunnableScheduledFuture task) {
            return new BlinkingTask(c, task);
        }


        @Override
        public boolean isPeriodic() {
            return false;
        }

        @Override
        public long getDelay(TimeUnit timeUnit) {
            return 0;
        }

        @Override
        public int compareTo(Delayed delayed) {
            return 0;
        }

        @Override
        public void run() {

        }

        @Override
        public boolean cancel(boolean b) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public Object get() throws InterruptedException, ExecutionException {
            return null;
        }

        @Override
        public Object get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
        }
    }
}
