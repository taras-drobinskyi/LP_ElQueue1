package com.versiya.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerCollection;

public class ProccessAllHandlerList extends HandlerCollection {
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		Handler[] handlers = getHandlers();

		if (handlers != null && isStarted()) {
			for (int i = 0; i < handlers.length; i++) {
				handlers[i].handle(target, baseRequest, request, response);
			}
		}
	}
}

