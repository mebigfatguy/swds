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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.mebigfatguy.swds.propfind.PropFindBuilder;
import com.mebigfatguy.swds.propfind.PropFindParser;

public class PropFindHandler implements HttpHandler {

    private Logger LOGGER = LoggerFactory.getLogger(PropFindHandler.class);

    @Override
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp, File rootDirectory) throws ServletException {
        try {
            String serverPath = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() + req.getServletPath();

            Map<String, String> headers = HeaderParser.getRequestHeaders(req);
            String depth = headers.get("depth");
            if (depth == null) {
                depth = "1";
            } else if ("infinite".equals(depth)) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            Set<String> props = null;
            try (InputStream is = new BufferedInputStream(req.getInputStream());
                 OutputStream os = new BufferedOutputStream(resp.getOutputStream())) {
                PropFindParser p = new PropFindParser();
                p.parse(is);
                props = p.getProperties();
                resp.setStatus(MULTI_STATUS);
                resp.setContentType("application/xml");
                PropFindBuilder b = new PropFindBuilder(serverPath, rootDirectory, new File(rootDirectory, req.getPathInfo()), props, Integer.parseInt(depth));
                b.generate(os);

            }
        } catch (IllegalArgumentException | ParserConfigurationException | SAXException | TransformerException | IOException e) {
            LOGGER.error("Failed to process PROPFIND request on {}", req.getPathInfo(), e);
            throw new ServletException("Failed to process PROPFIND request", e);
        }
    }
}
