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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
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
	
	private static final String DEFAULT_TIMEOUT = "Second-604800";
	
	private File resourcePath;
	private LockInfo info;
	
	public LockBuilder(File resource, LockInfo lockInfo) {
		resourcePath = resource;
		info = lockInfo;
	}
	
	public void generate(OutputStream os) throws IOException, ParserConfigurationException, TransformerException {

		
		try (InputStream xsl = LockBuilder.class.getResourceAsStream("/com/mebigfatguy/swds/lock/lockresponse.xslt");
			 InputStream xml = LockBuilder.class.getResourceAsStream("/com/mebigfatguy/swds/lock/lockresponse.xml")) {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer(new StreamSource(xsl));
			t.setParameter("lockType", info.getType().name());
			t.setParameter("lockScope", info.getScope().name());
			t.setParameter("lockDepth", info.getDepth() == Depth.self ? "0" : "infinity");
			t.setParameter("lockOwner", info.getOwner());
			t.setParameter("lockTimeout", DEFAULT_TIMEOUT);
			t.setParameter("lockToken", "opaquelocktoken:e71d4fae-5dec-22d6-fea5-00a0c91e6be4");
			
			ByteArrayOutputStream baos = null;

			if (LOGGER.isDebugEnabled()) {
				baos = new ByteArrayOutputStream();
				os = new TeeOutputStream(os, baos);
			}
			
			t.transform(new StreamSource(xml),  new StreamResult(os));
			
			if (LOGGER.isDebugEnabled()) {
				baos.close();
				LOGGER.debug("LOCK RESPONSE: \n{}", new String(baos.toByteArray()));
			}
		}
	}
}
