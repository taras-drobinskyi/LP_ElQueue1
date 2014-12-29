/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.logosprog.elqdisplay.interfaces.MainActivityController;
import com.logosprog.elqdisplay.interfaces.MainActivityDelegate;
import display.TerminalData;

import java.util.List;

/**
 * A simple {@link android.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.logosprog.elqdisplay.fragments.ClientLayout.ClientInteractionListener} interface
 * to handle interaction events.
 *
 */
public class ClientLayout extends Fragment implements MainActivityDelegate {

    Context activityContext;

    ClientView clientView;

    private ClientInteractionListener mListener;

    public ClientLayout() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityContext = this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_client, container, false);
        clientView = new ClientView(activityContext);
        return clientView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final MainActivityController controller = (MainActivityController) getActivity();
        controller.onAttachDelegate(this);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onClientCall();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*try {
            mListener = (OnClientInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnClientInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAssignClient(TerminalData terminalRowData, int restOfClients) {

        clientView.client = terminalRowData.clientNumber;
        clientView.terminal = terminalRowData.terminalNumber;
        clientView.invalidate();
        /*clientView.animate()
                .translationY(0)
                .setDuration(getResources().getInteger(
                        android.R.integer.config_longAnimTime));*/
    }

    @Override
    public void onAcceptClient(TerminalData terminalRowData, int restOfClients) {
        /*clientView.animate()
                .translationY(clientView.getHeight())
                .setDuration(getResources().getInteger(
                        android.R.integer.config_longAnimTime));*/
    }

    @Override
    public void onInitTable(List<TerminalData> terminals, int restOfClients) {

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
    public interface ClientInteractionListener {
        public void onClientCall();
        public void onHiddenClientView();
    }

}
