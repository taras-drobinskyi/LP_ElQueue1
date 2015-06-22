package com.versiya.server;

import listeners.WebServerListener;


public class Client implements WebServerListener {

	@Override
	public void onFileUpload(Boolean success) {
		System.out.println("Test file upload message " + success);
	}

	@Override
	public void onTickerChanched(Boolean success) {
		System.out.println("Test ticker message " + success);

	}



}
