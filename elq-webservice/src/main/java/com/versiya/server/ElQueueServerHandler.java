package com.versiya.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import listeners.WebServerListener;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class ElQueueServerHandler extends AbstractHandler {

	WebServerListener listener;

	public ElQueueServerHandler(WebServerListener listner) throws NullPointerException {
		if (listner != null) {
		this.listener = listner;
		} else {
			throw new NullPointerException("WebServerListener can not be null");
		}
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		try {
			if (Boolean.valueOf(response.getHeader("upload"))) {
				listener.onFileUpload(true);
			} else if (Boolean.valueOf(response.getHeader("ticker"))) {
				listener.onTickerChanched(true);
			}

		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}


	}

}
