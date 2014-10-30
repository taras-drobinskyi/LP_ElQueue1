package com.logosprog.elqdisplay.interfaces;

/**
 * Created by logosprog on 16.10.2014.
 */
public interface MainActivityDelegate {
    public void onAssignClient(int terminal, int client);
    public void onDetachClient(int terminal, int client);
}
