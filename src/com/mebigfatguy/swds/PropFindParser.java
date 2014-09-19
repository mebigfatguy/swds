package com.mebigfatguy.swds;

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
