package com.mryan23.list;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Scanner;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.mryan23.list.model.List;
import com.mryan23.list.model.ListObject;

public class ListServlet extends HttpServlet {
	
	List l;
	Gson gson = new Gson();
	public ListServlet(){
		l = new List();
		l.setName("List 1");
		ListObject[] items = new ListObject[4];
		ListObject item0 = new ListObject();
		item0.setName("Item0");
		items[0] = item0;
		ListObject item1 = new ListObject();
		item1.setName("Item1");
		ListObject subItem10 = new ListObject();
		subItem10.setName("SubItem10");
		ListObject subItem11 = new ListObject();
		subItem11.setName("SubItem11");
		ListObject[] subItems = { subItem10,subItem11 };
		item1.setItems(subItems);
		item1.setCompleted(true);
		items[1] = item1;
		l.setItems(items);
		ListObject item2 = new ListObject();
		item2.setName("Item2");
		item2.setDeleted(true);
		items[2]=item2;
		ListObject item3 = new ListObject();
		item3.setName("Item3");
		items[3]=item3;
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		
		resp.setContentType("application/json");
		String uri = URLDecoder.decode(req.getRequestURI().substring("/api/list/".length()));
		if(uri.indexOf("/")>=0)
			uri = uri.substring(0,uri.indexOf("/"));
		
		resp.getWriter().write(gson.toJson(l));
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		if(!req.getContentType().toLowerCase().equals("application/json")){
			System.out.println(req.getContentType());
			throw new IOException();
		}
		InputStream body = req.getInputStream();
		Scanner s = new Scanner(body);
		StringBuffer buf = new StringBuffer();
		while(s.hasNext()){
			buf.append(s.nextLine());
		}
		s.close();
		
		System.out.println("POSTED");
		String data = buf.toString();
		l = gson.fromJson(data, List.class);
		
	}

}
