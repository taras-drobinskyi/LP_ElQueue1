package com.versiya.server;

import java.awt.EventQueue;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

public class Main {
	
	public static void main(String[] args) {
		ContextHandlerCollection contexts = new ContextHandlerCollection();

		// final JettyServer jettyServer = new JettyServer();
		final WebServer webServer = new WebServer();

		contexts.setHandlers(new Handler[] { new AppContextBuilder()
				.buildWebAppContext() });
		webServer.setHandler(contexts);
		// jettyServer.setHandler(contexts);
		Runnable runner = new Runnable() {
			public void run() {
				// new ServerRunner(jettyServer);
				new ServerRunner(webServer);
				Client client = new Client(webServer);
				System.out.println("static method finished");
		}
		};
		EventQueue.invokeLater(runner);

	}
	
	
}
