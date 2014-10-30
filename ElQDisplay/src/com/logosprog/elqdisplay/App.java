/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;


public class App extends Application {

    /**
     * SharedPref key indicate if there is a new commercial content available to substitute the current one.
     */
    public static final String KEY_GOT_NEW_CONTENT = "got_new_content";

    /**
     * SharedPref key indicate if the new commercial content played successfully.
     */
    public static final String KEY_PLAYED_NEW_CONTENT = "played_new_content";

    /**
     * SharedPref key to store path to new media content file.
     */
    public static final String KEY_MEDIA_PATH = "media_path";

    /**
     * SharedPref key to store path to currently running media content file.
     */
    public static final String KEY_DEFAULT_MEDIA_PATH = "default_media_path";

    /**
     * Directory where to store media content.
     */
    public static final String DIR_PATH = "ElQDisplay";

    /**
     * URL to the media directory on the SERVER.
     */
    public static final String DOWNLOAD_URL = "http://logosprog.com/georgiaguide/media/";

    public static final String FILE_NAME_VIDEO = "frame_video";


    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static String getMediaPath(Context context) throws NullPointerException{
        SharedPreferences prefs = context.getSharedPreferences(FILE_NAME_VIDEO, 0);
        return prefs.getString(KEY_MEDIA_PATH, null);
    }

    public static void setMediaPath (Context context, String val){
        SharedPreferences.Editor editor = context.getSharedPreferences(FILE_NAME_VIDEO, 0).edit();
        editor.putString(KEY_MEDIA_PATH, val);
        editor.apply();
    }

    public static String getDefaultMediaPath(Context context) throws NullPointerException{
        SharedPreferences prefs = context.getSharedPreferences(FILE_NAME_VIDEO, 0);
        return prefs.getString(KEY_DEFAULT_MEDIA_PATH, null);
    }

    public static void setDefaultMediaPath(Context context, String val){
        SharedPreferences.Editor editor = context.getSharedPreferences(FILE_NAME_VIDEO, 0).edit();
        editor.putString(KEY_DEFAULT_MEDIA_PATH, val);
        editor.apply();
    }

    public static void setFlag_gotNewContent (Context context, boolean val){
        SharedPreferences.Editor editor = context.getSharedPreferences(FILE_NAME_VIDEO, 0).edit();
        editor.putBoolean(KEY_GOT_NEW_CONTENT, val);
        editor.apply();
    }

}
