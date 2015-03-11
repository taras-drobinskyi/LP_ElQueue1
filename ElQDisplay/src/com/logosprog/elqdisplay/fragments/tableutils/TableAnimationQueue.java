/*
 * Copyright (c) 2015. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments.tableutils;

import android.util.Log;

import java.util.*;

/**
 * Created by forando on 11.03.15.
 * This class wraps {@link java.util.Queue} object and adds
 * {@link com.logosprog.elqdisplay.fragments.tableutils.TableAnimationQueue.TableAnimationQueueListener}
 * callbacks to it's functionality
 */
public class TableAnimationQueue {

    private final String TAG = getClass().getSimpleName();

    private volatile Queue<HashMap<String, Integer>> queue;

    public TableAnimationQueue(){
        queue = new LinkedList<>();
        listeners = new ArrayList<>();
    }

    /**
     * The same as {@link java.util.Queue#offer(Object)}. But in addition to that,
     * when before this method is called the queue was empty, it triggers
     * {@link com.logosprog.elqdisplay.fragments.tableutils.TableAnimationQueue.TableAnimationQueueListener#onTableAnimationQueueInit()}
     * callback.
     * @param levelIndex Level index of the row
     * @param animation Type of requested animation (0 - delete, 1 - add)
     * @return boolean whether or not the element has been added to the queue
     */
    public boolean offer(int levelIndex, int animation){
        HashMap<String, Integer> rowData = new HashMap<>();
        rowData.put("levelIndex", levelIndex);
        rowData.put("animation", animation);
        int size = queue.size();
        boolean result = queue.offer(rowData);
        if (size == 0){
            for (TableAnimationQueueListener listener : listeners){
                listener.onTableAnimationQueueInit();
            }
        }
        return result;
    }

    /**
     * The same as {@link java.util.Queue#poll()}.
     * @return {@link com.logosprog.elqdisplay.fragments.tableutils.TerminalRow} object
     */
    public HashMap<String, Integer> poll(){
        HashMap<String, Integer> rowData;
        try {
            rowData = queue.poll();
            return rowData;
        }catch (NullPointerException ex){
            //System.out.println("The deleteQueue is empty, size = " + queue.size() );
            Log.d(TAG, "The deleteQueue is empty, size = " + queue.size());
            return null;
        }
    }

    public int getSize(){
        return queue.size();
    }

    private List<TableAnimationQueueListener> listeners;

    public void addTableAnimationQueueListener(TableAnimationQueueListener listener){
        listeners.add(listener);
    }

    public interface TableAnimationQueueListener{
        /**
         * This callback is called when, after being empty, the first element
         * is added to the {@link com.logosprog.elqdisplay.fragments.tableutils.TableAnimationQueue}
         * object
         */
        public void onTableAnimationQueueInit();
    }
}
