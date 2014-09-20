package com.mebigfatguy.swds;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class HeaderParser {

	private HeaderParser() {
		
	}
	
	public static Map<String, String> getRequestHeaders(HttpServletRequest req) {
		Map<String, String> headers = new HashMap<>();
		Enumeration<String> enm = req.getHeaderNames();
		while (enm.hasMoreElements()) {
			String key = enm.nextElement();
			headers.put(key, req.getHeader(key));
		}
		return headers;
	}
}
