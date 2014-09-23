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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class PropFindParser {

	private static final List<String> INPROP = Arrays.asList("propfind", "prop");
	
	private List<String> location = new ArrayList<>();
	private Set<String> properties = new HashSet<>();
	
	public void parse(InputStream is) throws SAXException, IOException {
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(new PropContentHandler());
		
		reader.parse(new InputSource(is));
	}
	
	public Set<String> getProperties() {
		return properties;
	}
	
	class PropContentHandler extends DefaultHandler {

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (isInProp()) {
				properties.add(localName);
			}
			location.add(localName);
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			location.remove(location.size() - 1);
		}
		
		private boolean isInProp() {
			return INPROP.equals(location);
		}
	}
}
