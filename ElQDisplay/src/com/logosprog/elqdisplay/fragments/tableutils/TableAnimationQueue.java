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
    /**
     * Type of operation that specifies delete animation
     */
    public static final int OPERATION_DELETE = 0;
    /**
     * Type of operation that specifies add animation
     */
    public static final int OPERATION_ADD = 1;
    /**
     * A key to get animation value from given HashMap<String, Integer>
     */
    public static final String KEY_ANIMATION = "animation";
    /**
     * A key to get terminal number from given HashMap<String, Integer>
     */
    public static final String KEY_TERMINAL = "terminal";
    /**
     * A key to get client number from given HashMap<String, Integer>
     */
    public static final String KEY_CLIENT = "client";

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
     * @param terminal Terminal index of the row
     * @param animation Type of requested animation can be
     *              <ul>
     *                  <li>{@link #OPERATION_DELETE}</li>
     *                  <li>{@link #OPERATION_ADD}</li>
     *              </ul>
     * @return boolean whether or not the element has been added to the queue
     */
    public boolean offer(int animation, int terminal, int... client)throws RuntimeException{
        if (animation == OPERATION_ADD && client == null) throw new RuntimeException(
                "NullPointerException In method TableAnimationQueue.offer(third argument)");
        HashMap<String, Integer> rowData = new HashMap<>();
        rowData.put(KEY_ANIMATION, animation);
        rowData.put(KEY_TERMINAL, terminal);
        rowData.put(KEY_CLIENT, (client == null)? -1 : client[0]);
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
