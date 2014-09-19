package com.mebigfatguy.swds;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PropFindBuilder {

	public File rootPath;
	private File resourcePath;
	private Set<String> properties;
	private int depth;
	private DateFormat df;
	
	public PropFindBuilder(File root, File resource, Set<String> props, int exploreDepth) {
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
		
		Element multistatus = d.createElementNS("DAV", "d:multistatus");
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
		
	}
	
	private void appendResponse(Document d, Element parent, File resource) throws IOException {
		Element response = d.createElementNS("DAV", "d:response");
		parent.appendChild(response);
		Element href = d.createElementNS("DAV", "d.href");
		href.appendChild(d.createTextNode(resourcePath.getPath().substring(rootPath.getPath().length())));
		response.appendChild(href);
		Element propstat = d.createElementNS("DAV", "d:propstat");
		response.appendChild(propstat);
		Element prop = d.createElementNS("DAV", "d:prop");
		propstat.appendChild(prop);
		Map<String, String> values = populateValues(resource);
		for (Map.Entry<String, String> entry : values.entrySet()) {
			Element oneProp = d.createElementNS("DAV", "d:" + entry.getKey());
			oneProp.appendChild(d.createTextNode(entry.getValue()));
			prop.appendChild(oneProp);
		}
	}
	
	private Map<String, String> populateValues(File resource) throws IOException {
		Map<String, String> values = new HashMap<>();
		for (String prop : properties) {
			switch (prop) {
				case "resourcetype":
					if (resource.isDirectory()) {
						values.put(prop, "container");
					}
				break;
				
				case "quota-used-bytes":
					values.put(prop, String.valueOf(resource.getUsableSpace()));
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
			}
		}
		return values;
	}
}
