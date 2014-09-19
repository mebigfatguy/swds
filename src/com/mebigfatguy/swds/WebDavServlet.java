package com.mebigfatguy.swds;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class WebDavServlet extends HttpServlet {

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
		
		Map<String, String> headers = getRequestHeaders(req);
		
		String method = req.getMethod();
		switch (method) {
			
			case "OPTIONS":
				resp.setStatus(HttpServletResponse.SC_OK);
			break;
			
			case "PROPFIND":
				String depth = headers.get("depth");
				if ("infinite".equals(depth)) {
					resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
				
				Set<String> props = null;
				try (InputStream is = new BufferedInputStream(req.getInputStream());
					 OutputStream os = new BufferedOutputStream(resp.getOutputStream())) {
					PropFindParser p = new PropFindParser();
					p.parse(is);
					props = p.getProperties();
					resp.setStatus(HttpServletResponse.SC_OK);
					PropFindBuilder b = new PropFindBuilder(rootDirectory, new File(rootDirectory, req.getPathInfo()), props, Integer.parseInt(depth));
					b.generate(os);
				
				} catch (IllegalArgumentException | ParserConfigurationException | SAXException | TransformerException se) {
					throw new ServletException("Failed parsing PROPFIND properties", se);
				}
				
			break;
			
			default:
				
				try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(resp.getOutputStream()))) {
					bw.write("Server Up");
				}
				break;
		}
	}
	
	private static Map<String, String> getRequestHeaders(HttpServletRequest req) {
		Map<String, String> headers = new HashMap<>();
		Enumeration<String> enm = req.getHeaderNames();
		while (enm.hasMoreElements()) {
			String key = enm.nextElement();
			headers.put(key, req.getHeader(key));
		}
		return headers;
	}
	
	private static String getRequestBody(HttpServletRequest req) throws IOException {
		StringBuilder body = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()))) {
			String line = br.readLine();
			while (line != null) {
				body.append(line);
				line = br.readLine();
			}
		}
		
		return body.toString();
	}
}