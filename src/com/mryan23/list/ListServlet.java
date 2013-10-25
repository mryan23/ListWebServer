package com.mryan23.list;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.mryan23.list.model.List;
import com.mryan23.list.model.ListObject;

public class ListServlet extends HttpServlet {

	//List l;
	Gson gson = new Gson();

	public ListServlet() {

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		resp.setContentType("application/json");
		// int id = Integer.parseInt(req.getParameter("id"));
		String idString = req.getParameter("id");
		String userString = req.getParameter("user");
		String nameString = req.getParameter("name");
		String newList = req.getParameter("new");
		boolean newListBool = Boolean.parseBoolean(newList);
		if (idString != null) {
			int id = Integer.parseInt(idString);
			List list = getList(id);
			resp.getWriter().write(gson.toJson(list));
		} else if (newList != null&&newListBool) {
			List list = new List();
			list.setName(nameString);
			list.setUser(userString!=null?userString:"");
			insertList(list);
			list.setId(getId(list));
			resp.getWriter().write(gson.toJson(list));
		} else if (userString != null) {
			List[] lists = getLists(userString);
			resp.getWriter().write(gson.toJson(lists));
		}

	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		String idString = req.getParameter("id");
		int id=Integer.parseInt(idString);
		deleteList(id);
		
	}
	

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		if (!req.getContentType().toLowerCase().equals("application/json")) {
			throw new IOException();
		}
		InputStream body = req.getInputStream();
		Scanner s = new Scanner(body);
		StringBuffer buf = new StringBuffer();
		while (s.hasNext()) {
			buf.append(s.nextLine());
		}
		s.close();

		String data = buf.toString();
		List list = gson.fromJson(data, List.class);
		insertList(list);

	}
	
	private void deleteList(int id){
		String query = "SELECT * FROM LISTS WHERE ID =" + id;
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			stmt = c.createStatement();

			ResultSet rs = stmt.executeQuery(query);
			int count = 0;
			String fileName = null;
			while (rs.next()) {
				count++;

				fileName = rs.getString("FILE");
				break;
			}
			deleteFile(fileName);
			stmt.execute("DELETE FROM LISTS WHERE ID="+id);
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			// System.exit(0);
		}
		
	}
	private void deleteFile(String fileName){
		File f = new File(fileName);
		if(f.exists())
			f.delete();
	}

	private void insertList(List list) {
		String query = "SELECT * FROM LISTS WHERE ID =" + list.getId();
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			stmt = c.createStatement();

			ResultSet rs = stmt.executeQuery(query);
			int count = 0;
			String fileName = null;
			while (rs.next()) {
				count++;

				fileName = rs.getString("FILE");
				break;
			}
			if (count > 0) {
				// EXISTS
				writeListToFile(list, fileName);
				String update ="UPDATE LISTS SET NAME='"+list.getName()+"' WHERE ID="+list.getId();
				stmt.executeUpdate(update);
			} else {
				//NEW
				fileName = list.getName() + "_" + list.getUser() + ".json";
				fileName = fileName.toLowerCase().replace(" ", "");
				query = "INSERT INTO LISTS (NAME, USER, FILE) VALUES ('"
						+ list.getName() + "','" + list.getUser() + "','"
						+ fileName + "')";
				stmt.executeUpdate(query);
				writeListToFile(list, fileName);
			}
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			// System.exit(0);
		}
	}
	public int getId(List list){
		String query = "SELECT ID FROM LISTS WHERE NAME ='" + list.getName()+"' AND USER='"+list.getUser()+"'";
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			stmt = c.createStatement();

			ResultSet rs = stmt.executeQuery(query);
			int count = 0;
			while (rs.next()) {
				count++;

				String idString = rs.getString("ID");
				stmt.close();
				c.close();
				return Integer.parseInt(idString);
			}
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			// System.exit(0);
		}
		return -1;
	}

	private void writeListToFile(List l, String fileName) {
		try {
			File f = new File(fileName);
			if (!f.exists()) {
				f.createNewFile();
			}
			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(gson.toJson(l));
			bw.close();
		} catch (Exception e) {

		}
	}

	private List getList(int id) {
		// String query =
		// "SELECT LISTS.NAME, items.name, items.checked, items.deleted FROM items WHERE LISTS.ID="+id+" JOIN LISTS ON LISTS.id=items.list_id";
		String query = "SELECT * FROM LISTS WHERE ID =" + id;
		Connection c = null;
		Statement stmt = null;
		List result = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			String fileName = null;
			while (rs.next()) {
				fileName = rs.getString("FILE").replace(" ", "");
			}
			if (fileName != null) {
				BufferedReader br = new BufferedReader(new FileReader(fileName));
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();
				while (line != null) {
					sb.append(line);
					sb.append("\n");
					line = br.readLine();
				}
				result = gson.fromJson(sb.toString(), List.class);
			}
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			// System.exit(0);
		}
		return result;
	}

	private List[] getLists(String userName) {
		String query = "SELECT ID, NAME FROM LISTS WHERE USER='" + userName
				+ "'";
		Connection c = null;
		Statement stmt = null;
		ArrayList<List> result = new ArrayList<List>();
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			String idString = null;
			String name = null;
			while (rs.next()) {
				idString = rs.getString("ID");
				name = rs.getString("NAME");
				List dummy = new List();
				dummy.setId(Integer.parseInt(idString));
				dummy.setName(name);
				result.add(dummy);
			}
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			// System.exit(0);
		}
		List[] returnVals = new List[result.size()];
		for (int i = 0; i < result.size(); i++) {
			returnVals[i] = result.get(i);
		}
		return returnVals;
	}

}
