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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class LockParser {
	
	private static final List<String> INSCOPE = Arrays.asList("lockinfo", "lockscope");
	private static final List<String> INTYPE = Arrays.asList("lockinfo", "locktype");	
	private static final List<String> INDEPTH = Arrays.asList("lockinfo", "lockdepth");
	private static final List<String> INOWNER = Arrays.asList("lockinfo", "locktype", "owner", "href");	
	
	private List<String> location = new ArrayList<>();
	private String scope = "exclusive";
	private String type = "write";
	private String depth = "0";
	private StringBuilder owner = new StringBuilder();

	
	public void parse(InputStream is) throws SAXException, IOException {
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(new LockHandler());
		
		reader.parse(new InputSource(is));
	}
	
	public LockInfo getLockInfo() {
		return new LockInfo(scope, type, depth, owner.toString().trim());
	}
	
	class LockHandler extends DefaultHandler {

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (INSCOPE.equals(location)) {
				scope = localName;
			} else if (INTYPE.equals(location)) {
				type = localName;
			} else if (INDEPTH.equals(location)) {
				depth = localName;
			}
			location.add(localName);
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			location.remove(location.size() - 1);
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (INOWNER.equals(location)) {
				String value = new String(ch, start, length);
				owner.append(value);
			}
		}
	}
}
