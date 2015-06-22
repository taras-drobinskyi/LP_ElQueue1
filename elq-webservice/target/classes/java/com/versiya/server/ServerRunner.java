package com.versiya.server;


import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;

public class ServerRunner extends JFrame {
	private static final long serialVersionUID = 8261022096695034L;

	private JButton btnStartStop;

	public ServerRunner(final JettyServer jettyServer) {
		super("Start/Stop Server");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		btnStartStop = new JButton("Start");
		btnStartStop.addActionListener(new ServerStartStopActionListner(
				jettyServer));
		add(btnStartStop, BorderLayout.CENTER);
		setSize(300, 300);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				if (jettyServer.isStarted()) {
					try {
						jettyServer.stop();
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}
		}, "Stop Jetty Hook"));
		setVisible(true);
	}
}