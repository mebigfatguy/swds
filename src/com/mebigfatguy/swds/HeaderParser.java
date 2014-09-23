/** SWDS - a sample webdav server. 
 * Copyright 2014 MeBigFatGuy.com 
 * Copyright 2014 Dave Brosius 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and limitations 
 * under the License. 
 */
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
