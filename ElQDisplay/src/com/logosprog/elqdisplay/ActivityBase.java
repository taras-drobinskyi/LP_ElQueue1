/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.logosprog.elqdisplay.App;

import java.io.File;

/**
 * Created by logosprog on 20.10.2014.
 */
public class ActivityBase extends Activity {
    /**
     * Tag used on log messages.
     */
    static final String TAG = "com.logosprog.elqdisplay.ActivityBase";

    static String DIR_PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DIR_PATH = App.DIR_PATH;

        File root = Environment.getExternalStorageDirectory();

    }

    public static boolean createDirIfNotExists() {
        boolean ret = true;

        File file = new File(Environment.getExternalStorageDirectory(), DIR_PATH);

        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e(TAG, "Problem creating media folder: " + DIR_PATH);
                ret = false;
            }
        }else{
            Log.e(TAG, "New Folder = " + DIR_PATH);
        }
        return ret;
    }
}
