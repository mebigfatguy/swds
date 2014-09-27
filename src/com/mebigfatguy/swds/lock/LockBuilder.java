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
package com.mebigfatguy.swds.lock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mebigfatguy.swds.lock.LockInfo.Depth;
import com.mebigfatguy.swds.utils.TeeOutputStream;

public class LockBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(LockBuilder.class);
	
	private static final String LOCK_TOKEN_PROTOCOL = "opaquelocktoken:";
	private static final String DEFAULT_TIMEOUT = "Second-604800";
	
	private LockInfo info;
	
	public LockBuilder(LockInfo lockInfo) {
		info = lockInfo;
	}
	
	public void generate(HttpServletResponse resp, OutputStream os) throws IOException, TransformerException {

		resp.setHeader("Lock-Token", LOCK_TOKEN_PROTOCOL + info.getToken());
		try (InputStream xsl = LockBuilder.class.getResourceAsStream("/com/mebigfatguy/swds/lock/lockresponse.xslt");
			 InputStream xml = LockBuilder.class.getResourceAsStream("/com/mebigfatguy/swds/lock/lockresponse.xml")) {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer(new StreamSource(xsl));
			t.setParameter("lockType", info.getType().name());
			t.setParameter("lockScope", info.getScope().name());
			t.setParameter("lockDepth", info.getDepth() == Depth.self ? "0" : "infinity");
			t.setParameter("lockOwner", info.getOwner());
			t.setParameter("lockTimeout", DEFAULT_TIMEOUT);
			t.setParameter("lockToken", LOCK_TOKEN_PROTOCOL + info.getToken());
			
			ByteArrayOutputStream baos = null;

			if (LOGGER.isDebugEnabled()) {
				baos = new ByteArrayOutputStream();
				os = new TeeOutputStream(os, baos);
			}
			
			t.transform(new StreamSource(xml),  new StreamResult(os));
			
			if (LOGGER.isDebugEnabled()) {
				baos.close();
				LOGGER.debug("LOCK RESPONSE: \n{}", new String(baos.toByteArray(), StandardCharsets.UTF_8));
			}
		}
	}
}
