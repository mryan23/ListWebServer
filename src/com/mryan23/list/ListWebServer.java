package com.mryan23.list;

import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class ListWebServer extends HttpServlet{
	
	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);
		
		WebAppContext context = new WebAppContext();
		context.setWar("war");
		context.setContextPath("/");
		server.setHandler(context);
		server.start();
		server.join();
	}

}
