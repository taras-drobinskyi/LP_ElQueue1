package com.versiya.server;

import java.awt.EventQueue;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

public class Main {
	public static void main(String[] args) {
		ContextHandlerCollection contexts = new ContextHandlerCollection();

		final JettyServer jettyServer = new JettyServer();

		contexts.setHandlers(new Handler[] { new AppContextBuilder()
				.buildWebAppContext() });
	jettyServer.setHandler(contexts);
		Runnable runner = new Runnable() {
			public void run() {
				new ServerRunner(jettyServer);
		}
		};
		EventQueue.invokeLater(runner);
	}
	
}
