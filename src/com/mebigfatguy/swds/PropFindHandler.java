package com.mebigfatguy.swds;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class PropFindHandler implements HttpHandler {

	private Logger LOGGER = LoggerFactory.getLogger(PropFindHandler.class);
	
	@Override
	public void handleRequest(HttpServletRequest req, HttpServletResponse resp, File rootDirectory) {
		try {
			resp.setHeader("DAV", "1,2");
			
			String serverPath = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort()  + req.getContextPath() + req.getServletPath();
	
			Map<String, String> headers = HeaderParser.getRequestHeaders(req);
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
				resp.setStatus(MULTI_STATUS);
				resp.setContentType("application/xml");
				PropFindBuilder b = new PropFindBuilder(serverPath, rootDirectory, new File(rootDirectory, req.getPathInfo()), props, Integer.parseInt(depth));
				b.generate(os);
			
			}
		} catch (IllegalArgumentException | ParserConfigurationException | SAXException | TransformerException | IOException e) {
			LOGGER.error("Failed to process PROPFIND", e);
		}
	}
}
