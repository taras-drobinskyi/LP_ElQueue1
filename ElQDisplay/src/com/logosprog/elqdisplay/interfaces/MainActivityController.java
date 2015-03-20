package com.logosprog.elqdisplay.interfaces;

/**
 * Created by logosprog on 16.10.2014.
 * This interface is implemented by Main Activity
 */
public interface MainActivityController {
    public void onAttachDelegate(MainActivityDelegate delegate);
    public void onDetachDelegate(MainActivityDelegate delegate);

    /**
     * Indicates that addRowAnimation in {@link com.logosprog.elqdisplay.fragments.TableView},
     * with assigned client, is about to begin. <br>
     *     This method is called by
     *     {@link com.logosprog.elqdisplay.fragments.TableLayout} object only.
     *     No other Main Activity Children are supposed to use it.
     * @param terminal The terminal number
     * @param client The client number
     */
    public void onClientAssignAnimationStart(int terminal, int client);
}
