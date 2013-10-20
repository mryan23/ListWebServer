package com.mryan23.list;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class ListWebServer extends HttpServlet {

	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			System.out.println("Opened database successfully");

			stmt = c.createStatement();
			String sql = "CREATE TABLE LISTS "
					+ "(ID INTEGER PRIMARY KEY     NOT NULL,"
					+ " NAME           TEXT    NOT NULL," 
					+ " USER			TEXT	NOT NULL,"
					+ " FILE			TEXT	NOT NULL"+")";
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			// System.exit(0);
		}
		System.out.println("Table created successfully");

		WebAppContext context = new WebAppContext();
		context.setWar("war");
		context.setContextPath("/");
		server.setHandler(context);
		server.start();
		server.join();
	}

}
