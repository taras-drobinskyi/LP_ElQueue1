package com.versiya.server;

import org.eclipse.jetty.webapp.WebAppContext;

public class AppContextBuilder {

	private WebAppContext webAppContext;

	public WebAppContext buildWebAppContext() {
		webAppContext = new WebAppContext();
		String webxmlLocation = Main.class.getResource("/webapp/WEB-INF/web.xml").toString();
		webAppContext.setDescriptor(webxmlLocation);
		String resLocation = Main.class.getResource("/webapp").toString();
		webAppContext.setResourceBase(resLocation);
		webAppContext.setContextPath("/elq-server");
		webAppContext.addServlet("com.versiya.servlets.AdvServlet",
				"/advertisement");

		return webAppContext;
	}
}