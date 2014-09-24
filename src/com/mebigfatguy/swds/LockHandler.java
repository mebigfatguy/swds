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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mebigfatguy.swds.lock.LockBuilder;
import com.mebigfatguy.swds.lock.LockInfo;
import com.mebigfatguy.swds.lock.LockParser;

public class LockHandler implements HttpHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(LockHandler.class);
	
	@Override
	public void handleRequest(HttpServletRequest req, HttpServletResponse resp, File rootDirectory) {
		try {
			resp.setHeader("DAV", "1,2");
			
			try (InputStream is = new BufferedInputStream(req.getInputStream());
			     OutputStream os = new BufferedOutputStream(resp.getOutputStream())) {
					LockParser p = new LockParser();
					p.parse(is);
					LockInfo info = p.getLockInfo();
					
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.setContentType("text/xml");
					LockBuilder b = new LockBuilder(new File(rootDirectory, req.getPathInfo()), info);
					b.generate(os);
				}
		} catch (Exception e) {
			LOGGER.error("Failed to process LOCK", e);
		}
	}

}
