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
package com.mebigfatguy.swds.propfind;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PropFindBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(PropFindBuilder.class);
	
	private static final String DAV_NS = "DAV:";
	private String serverPath;
	private File rootPath;
	private File resourcePath;
	private Set<String> properties;
	private int depth;
	private DateFormat df;
	
	public PropFindBuilder(String server, File root, File resource, Set<String> props, int exploreDepth) {
		serverPath = server;
		rootPath = root;
		resourcePath = resource;
		properties = props;
		depth = exploreDepth;
		df = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss:00");
	}
	
	public void generate(OutputStream os) throws IOException, ParserConfigurationException, TransformerException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document d = db.newDocument();
		
		Element multistatus = d.createElementNS(DAV_NS, "d:multistatus");
		d.appendChild(multistatus);
		appendResponse(d, multistatus, resourcePath);
		
		if (depth == 1) {
			File[] children = resourcePath.listFiles();
			for (File child : children) {
				appendResponse(d, multistatus, child);
			}
		}
		
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t = tf.newTransformer();
		t.transform(new DOMSource(d),  new StreamResult(os));
				
		if (LOGGER.isDebugEnabled()) {
			StringWriter sw = new StringWriter();
			t.transform(new DOMSource(d), new StreamResult(sw));
			String s = sw.toString();
			
			LOGGER.debug("PROPFIND response: \n{}", s);
		}
	}
	
	private void appendResponse(Document d, Element parent, File resource) throws IOException {
		Element response = d.createElementNS(DAV_NS, "d:response");
		parent.appendChild(response);
		Element href = d.createElementNS(DAV_NS, "d:href");
		href.appendChild(d.createTextNode(serverPath + resource.getPath().substring(rootPath.getPath().length())));
		response.appendChild(href);
		Element propstat = d.createElementNS(DAV_NS, "d:propstat");
		response.appendChild(propstat);
		Element prop = d.createElementNS(DAV_NS, "d:prop");
		propstat.appendChild(prop);
		Map<String, String> values = populateValues(resource);
		for (Map.Entry<String, String> entry : values.entrySet()) {
			String key = entry.getKey();
			Element oneProp = d.createElementNS(DAV_NS, "d:" + key);
			if (key.equals("resourcetype")) {
				if (entry.getValue().equals("collection")) {
					Element collection = d.createElementNS(DAV_NS, "d:collection");
					oneProp.appendChild(collection);
				}				
			} else {
				oneProp.appendChild(d.createTextNode(entry.getValue()));
			}
			prop.appendChild(oneProp);
		}
		Element status = d.createElementNS(DAV_NS, "d:status");
		status.appendChild(d.createTextNode("HTTP/1.1 200 OK"));
		propstat.appendChild(status);
		
	}
	
	private Map<String, String> populateValues(File resource) throws IOException {
		Map<String, String> values = new HashMap<>();
		for (String prop : properties) {
			switch (prop) {
				case "resourcetype":
					if (resource.isDirectory()) {
						values.put(prop, "collection");
					} else {
						values.put(prop, "item");
					}
				break;
				
				case "quota-available-bytes":
					values.put(prop, String.valueOf(resource.getUsableSpace()));
				break;
				
				case "quota-used-bytes":
					values.put(prop, String.valueOf(resource.length()));
				break;
				
				case "creationdate":
					Path path = Paths.get(resource.toURI());
					BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
					Date d = new Date(attr.creationTime().toMillis());
					values.put(prop, df.format(d));
				break;
				
				case "getlastmodified":
					values.put(prop, df.format(resource.lastModified()));
				break;
				
				case "getetag":
				break;
				
				case "getcontentlength":
					if (resource.isFile()) {
						values.put(prop, String.valueOf(resource.length()));
					}
				break;
				
				case "executable":
				break;
				
				default:
					LOGGER.warn("Unrecognized webdav property {}", prop);
				break;
			}
		}
		return values;
	}
}
