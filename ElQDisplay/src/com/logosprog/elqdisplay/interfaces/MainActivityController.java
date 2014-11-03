package com.logosprog.elqdisplay.interfaces;

import com.logosprog.elqdisplay.fragments.ClientLayout;

/**
 * Created by logosprog on 16.10.2014.
 */
public interface MainActivityController {
    public void onAttachDelegate(MainActivityDelegate delegate);
    public void onDetachDelegate(MainActivityDelegate delegate);
}
