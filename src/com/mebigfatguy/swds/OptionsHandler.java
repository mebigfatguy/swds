package com.mebigfatguy.swds;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OptionsHandler implements HttpHandler {

	@Override
	public void handleRequest(HttpServletRequest req, HttpServletResponse resp, File rootDirectory) {
		resp.setHeader("DAV", "1,2");
		resp.setStatus(MULTI_STATUS);
	}
}
