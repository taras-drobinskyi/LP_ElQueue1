package com.versiya.server;

import listeners.WebServerListener;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

public class JettyServer {

	private Server server;
	private WebServerListener listener;

	Logger log = Logger.getLogger(JettyServer.class.getName());

	public JettyServer(WebServerListener listener) throws NullPointerException {
		this(8787);
		
		if (listener != null) {
		this.listener = listener;
		} else {
			new NullPointerException("WebServerListener can not be null");
		}
		config();

	}

	public JettyServer(Integer runningPort) {
		server = new Server(runningPort);
	}

	public void setHandler(ProccessAllHandlerList handlers) {
		server.setHandler(handlers);
	}

	public void start() throws Exception {
		server.start();
		log.debug("Jetty server started");

	}

	public void startWithGUI() {
		new ServerRunner(this);
		log.debug("Jetty server started with GUI");

	}

	public void stop() throws Exception {
		server.stop();
		server.join();
		log.debug("Jetty server stopped");

	}

	public boolean isStarted() {
		return server.isStarted();
	}

	public boolean isStopped() {
		return server.isStopped();
	}

	private void config() {
		ContextHandlerCollection contexts = new ContextHandlerCollection();

		contexts.setHandlers(new Handler[] { new AppContextBuilder().buildWebAppContext() });
		ProccessAllHandlerList handlers = new ProccessAllHandlerList();
		handlers.setHandlers(new Handler[] { contexts, new ElQueueServerHandler(listener) });
		setHandler(handlers);
		// new ServerRunner(webServer);
		// Client client = new Client(webServer);
	}
}