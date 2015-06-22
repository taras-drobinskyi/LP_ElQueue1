package com.versiya.server;

import listeners.WebServerListener;

public class JettyServerRunnable implements Runnable {

	WebServerListener listener;

	public JettyServerRunnable(WebServerListener listener) {
		this.listener = listener;
	}

	@Override
	public void run() {
		final JettyServer jettyServer = new JettyServer(listener);

		try {
			// jettyServer.startWithGUI();
			jettyServer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
