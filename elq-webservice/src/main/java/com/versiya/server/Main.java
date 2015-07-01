package com.versiya.server;

import java.awt.EventQueue;

import org.apache.log4j.Logger;

public class Main {
	
	public static void main(String[] args) {

		Client client = new Client();
		/*
		 * ContextHandlerCollection contexts = new ContextHandlerCollection();
		 * final JettyServer jettyServer = new JettyServer(); // final WebServer
		 * webServer = new WebServer();
		 * 
		 * contexts.setHandlers(new Handler[] { new AppContextBuilder()
		 * .buildWebAppContext() }); // webServer.setHandler(contexts);
		 * //jettyServer.setHandler(contexts); ProccessAllHandlerList handlers =
		 * new ProccessAllHandlerList(); handlers.setHandlers(new Handler[] {
		 * contexts, new ElQueueServerHandler(client) });
		 * jettyServer.setHandler(handlers);
		 */

		Logger log = Logger.getLogger(Main.class.getName());
		log.debug("Main class launched");
		JettyServerRunnable jsr = new JettyServerRunnable(client);

		EventQueue.invokeLater(jsr);

	}
	
	
}
