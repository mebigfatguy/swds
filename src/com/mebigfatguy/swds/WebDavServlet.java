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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebDavServlet extends HttpServlet {

	private static final long serialVersionUID = -2234068300109718362L;

	private static final Logger LOGGER = LoggerFactory.getLogger(WebDavServlet.class);
	private static final String ROOT_DIR_PROP = "rootDir";

    private static final Map<WebDavMethods, HttpHandler> HANDLERS = new HashMap<>();
    static {
        HANDLERS.put(WebDavMethods.OPTIONS, new OptionsHandler());
        HANDLERS.put(WebDavMethods.PROPFIND, new PropFindHandler());
        HANDLERS.put(WebDavMethods.GET, new GetHandler());
        HANDLERS.put(WebDavMethods.HEAD, new HeadHandler());
        HANDLERS.put(WebDavMethods.PUT, new PutHandler());
        HANDLERS.put(WebDavMethods.LOCK, new LockHandler());
        HANDLERS.put(WebDavMethods.UNLOCK, new UnlockHandler());
        HANDLERS.put(WebDavMethods.VERSIONCONTROL, new VersionControlHandler());
    }

    private File rootDirectory;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            Context ctx = new InitialContext();
            Context envCtx = (Context) ctx.lookup("java:comp/env");
            rootDirectory = new File((String) envCtx.lookup(ROOT_DIR_PROP));

            if (!rootDirectory.isDirectory()) {
                throw new ServletException(String.format("'%s' (%s) is not a directory.", ROOT_DIR_PROP, rootDirectory));
            }

        } catch (NamingException e) {
            throw new ServletException(String.format("Failed looking up jndi property '%s'", ROOT_DIR_PROP), e);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setHeader("DAV", "1,2");

        HttpHandler handler = HANDLERS.get(WebDavMethods.fromString(req.getMethod()));
        if (handler == null) {

            LOGGER.error("Failed to process unexpected method {}", req.getMethod());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        handler.handleRequest(req, resp, rootDirectory);
    }
}