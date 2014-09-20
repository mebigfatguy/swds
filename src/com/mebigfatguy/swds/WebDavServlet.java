package com.mebigfatguy.swds;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebDavServlet extends HttpServlet {

	private static final Map<String, HttpHandler> HANDLERS = new HashMap<String, HttpHandler>();
	static {
		HANDLERS.put("OPTIONS", new OptionsHandler());
		HANDLERS.put("PROPFIND",  new PropFindHandler());
		HANDLERS.put("GET", new GetHandler());
	}
	
	private File rootDirectory;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			Context ctx = new InitialContext();
			Context envCtx = (Context) ctx.lookup("java:comp/env");
			rootDirectory = new File((String) envCtx.lookup("rootDir"));
			
			if (!rootDirectory.isDirectory()) {
				throw new ServletException("'rootDir' " + rootDirectory + " is not a directory.");
			}
			
		} catch (NamingException e) {
			throw new ServletException("Failed looking up jndi property 'rootDir'", e);
		}
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		resp.setHeader("DAV", "1,2");
		
		HttpHandler handler = HANDLERS.get(req.getMethod());
		if (handler == null) {
			try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(resp.getOutputStream()))) {
				bw.write("Unprocessed METHOD: " + req.getMethod());
			}
			return;
		}
		
		handler.handleRequest(req,  resp, rootDirectory);
	}
}