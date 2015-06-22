package com.versiya.server;

import listeners.WebServerListener;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

public class JettyServer {

	private Server server;
	private WebServerListener listener;

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
	}

	public void startWithGUI() {
		new ServerRunner(this);
	}

	public void stop() throws Exception {
		server.stop();
		server.join();
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