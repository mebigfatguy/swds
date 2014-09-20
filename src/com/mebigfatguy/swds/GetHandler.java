package com.mebigfatguy.swds;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetHandler implements HttpHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(GetHandler.class);
	
	@Override
	public void handleRequest(HttpServletRequest req, HttpServletResponse resp, File rootDirectory) {
		try {
			resp.setHeader("DAV", "1,2");
	
			File file = new File(rootDirectory, req.getPathInfo());
			try (InputStream is = new BufferedInputStream(new FileInputStream(file));
				 OutputStream os = new BufferedOutputStream(resp.getOutputStream())) {
				resp.setStatus(MULTI_STATUS);
				byte[] data = new byte[10240];
				int len = is.read(data);
				while (len >= 0) {
					os.write(data, 0, len);
					len = is.read(data);
				}
			}
		} catch (IOException e) {
			LOGGER.error("Failed to GET item", e);
		}
	}
}
