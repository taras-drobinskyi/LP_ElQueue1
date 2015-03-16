/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.VideoView;

import com.logosprog.elqdisplay.App;
import com.logosprog.elqdisplay.R;

/**
 * A simple {@link android.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.logosprog.elqdisplay.fragments.VideoLayout.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.logosprog.elqdisplay.fragments.VideoLayout#newInstance} factory method to
 * create an instance of this fragment.<br>
 * This class is a direct child to Main Activity class ({@link com.logosprog.elqdisplay.FullscreenActivity})
 *
 */
public class VideoLayout extends MainActivityFragment {

    private final String TAG = getClass().getSimpleName();

    public VideoView video;

    //private static final String MOVIE_URL = "http://www.ex.ua/get/893429553880/63690479";

    /**
     * The Path to "shipped with app" movie (located in src/raw folder).
     */
    private String APP_MOVIE_URL;

    /**
     * The Path to current movie.
     */
    private String DEFAULT_MEDIA_PATH;

    /**
     * The Path to new movie.
     */
    private String MEDIA_PATH;

    private boolean got_ext_media;


    /**
     * The time of where the current moovie has been stopped
     */
    private int length = 0;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GOT_EXTRA_MEDIA = "param1";

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param paramGotExtraMedia Parameter 1.
     * @return A new instance of fragment VideoLayout.
     */
    public static VideoLayout newInstance(boolean paramGotExtraMedia) {
        VideoLayout fragment = new VideoLayout();
        Bundle args = new Bundle();

        args.putBoolean(ARG_GOT_EXTRA_MEDIA, paramGotExtraMedia);
        fragment.setArguments(args);
        return fragment;
    }
    public VideoLayout() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            got_ext_media = getArguments().getBoolean(ARG_GOT_EXTRA_MEDIA);
        }else{
            got_ext_media = false;
        }
        activityContext = this.getActivity();
        APP_MOVIE_URL = "android.resource://" + activityContext.getPackageName() + "/" + R.raw.clipcanvas_14348_offline;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment;
        // Inflate the layout for this fragment
        fragment = inflater.inflate(R.layout.fragment_video, container, false);

        MEDIA_PATH = App.getMediaPath(activityContext);
        if (MEDIA_PATH != null){
            got_ext_media = true;
        }

        boolean got_def_media = false;
        DEFAULT_MEDIA_PATH = App.getDefaultMediaPath(activityContext);
        if (DEFAULT_MEDIA_PATH != null){
            got_def_media = true;
        }

        video = (VideoView) fragment.findViewById(R.id.video);

        if (got_ext_media){
            Log.d(TAG, "New File: " + MEDIA_PATH);

            video.setVideoPath(MEDIA_PATH);

        } else if (got_def_media){
            Log.d(TAG, "Default File: " + DEFAULT_MEDIA_PATH);

            video.setVideoPath(DEFAULT_MEDIA_PATH);

        } else {
            Log.d(TAG, "KEY_GOT_NEW_CONTENT = false");
            Uri vid_Uri = Uri.parse(APP_MOVIE_URL);
            video.setVideoURI(vid_Uri);
        }

        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                boolean got_ext_media = false;
                MEDIA_PATH = App.getMediaPath(activityContext);
                //Log.d(TAG, "File: " + MEDIA_PATH);
                if (MEDIA_PATH != null){
                    got_ext_media = true;
                }

                if (got_ext_media){
                    try
                    {
                        Log.d(TAG, "File: " + MEDIA_PATH);
                        video.setVideoPath(MEDIA_PATH);
                        video.start();
                    }
                    catch(Exception e)
                    {
                        Log.e(TAG, "CAUGHT ERROR: " + e.toString());

                        //send HTTPPOST "failed to play new media"
                        //delete new Media File

                        //getApp.editor.putBoolean(getApp.KEY_GOT_NEW_CONTENT, false);
                        App.setFlag_gotNewContent(activityContext, false);

                        //getApp.editor.putString(getApp.KEY_MEDIA_PATH, null);
                        App.setMediaPath(activityContext, null);


                        String defaultMediaPath = App.getDefaultMediaPath(activityContext);
                        if(defaultMediaPath != null){
                            DEFAULT_MEDIA_PATH = defaultMediaPath;
                            Log.d(TAG, "Setting back defaultVideoPath: " + DEFAULT_MEDIA_PATH);
                            video.setVideoPath(DEFAULT_MEDIA_PATH);
                            video.start();

                        }else{
                            Uri vid_Uri = Uri.parse(APP_MOVIE_URL);
                            video.setVideoURI(vid_Uri);
                            video.start();
                            String text = "Failed to play defiened media";
                            Toast.makeText(activityContext, text, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    mp.start();
                }
            }
        });

        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        try{
            video.start();

            //if the line above didn't through an ERROR, set New Media as Default Media
            if(got_ext_media){
                //getApp.editor.putString(getApp.KEY_DEFAULT_MEDIA_PATH, MEDIA_PATH);
                App.setDefaultMediaPath(activityContext, MEDIA_PATH);
                //getApp.editor.putBoolean(getApp.KEY_GOT_NEW_CONTENT, false);
                App.setFlag_gotNewContent(activityContext, false);
                //getApp.editor.putString(getApp.KEY_MEDIA_PATH, null);
                App.setMediaPath(activityContext, null);
            }
        }catch(Exception e){
            Log.e(TAG, "CATCHED ERROR: " + e.toString());

            //---!!!---send HTTPPOST "failed to play new media"
            //---!!!---delete new Media File

            //getApp.editor.putBoolean(getApp.KEY_GOT_NEW_CONTENT, false);
            App.setFlag_gotNewContent(activityContext, false);
            //getApp.editor.putString(getApp.KEY_MEDIA_PATH, null);
            App.setMediaPath(activityContext, null);

            if(DEFAULT_MEDIA_PATH != null){
                Log.d(TAG, "Setting back defaultVideoPath: " + DEFAULT_MEDIA_PATH);
                video.setVideoPath(DEFAULT_MEDIA_PATH);
            }else{
                Uri vid_Uri = Uri.parse(APP_MOVIE_URL);
                video.setVideoURI(vid_Uri);

                //String text = "Failed to play defiened media";
                //Toast.makeText(ActivityMain.this, text, Toast.LENGTH_SHORT).show();
            }

            video.start();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
