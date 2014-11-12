/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments.tableutils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by forando on 10.11.14.<br/>
 * This class wraps {@link java.util.Queue} object and adds
 * {@link com.logosprog.elqdisplay.fragments.tableutils.DeleteRowQueue.DeleteRowQueueListener}
 * callbacks to it's functionality
 */
public class DeleteRowQueue {

    private volatile Queue<TerminalRow>queue;

    public DeleteRowQueue(){
        queue = new LinkedList<>();
        listeners = new ArrayList<>();
    }

    /**
     * The same as {@link java.util.Queue#offer(Object)}. But in addition to that,
     * when before this method is called the queue was empty, it triggers
     * {@link com.logosprog.elqdisplay.fragments.tableutils.DeleteRowQueue.DeleteRowQueueListener#onDeleteRowQueueInit()}
     * callback.
     * @param row {@link com.logosprog.elqdisplay.fragments.tableutils.TerminalRow}
     * @return boolean whether or not the element has been added to the queue
     */
    public boolean offer(TerminalRow row){
        if (queue.size() == 0){
            for (DeleteRowQueueListener listener : listeners){
                listener.onDeleteRowQueueInit();
            }
        }
        return queue.offer(row);
    }

    /**
     * The same as {@link java.util.Queue#poll()}.
     * @return {@link com.logosprog.elqdisplay.fragments.tableutils.TerminalRow} object
     */
    public TerminalRow poll(){
        return queue.poll();
    }

    private List<DeleteRowQueueListener> listeners;

    public void addDeleteRowQueueListener(DeleteRowQueueListener listener){
        listeners.add(listener);
    }

    public interface DeleteRowQueueListener{
        /**
         * This callback is called when, after being empty, the first element
         * is added to the {@link com.logosprog.elqdisplay.fragments.tableutils.DeleteRowQueue}
         * object
         */
        public void onDeleteRowQueueInit();

    }

}
