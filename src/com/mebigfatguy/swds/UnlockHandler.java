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
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mebigfatguy.swds.lock.LockManager;

public class UnlockHandler implements HttpHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnlockHandler.class);

    @Override
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp, File rootDirectory) throws ServletException {
        try {
            Map<String, String> headers = HeaderParser.getRequestHeaders(req);
            String token = headers.get("lock-token");
            token = token.substring(token.indexOf(":") + 1);
            if (token.endsWith(">"))
                token = token.substring(0, token.length() - 1);

            if (token == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                LockManager lm = LockManager.getInstance();
                String resource = req.getPathInfo();

                String existingToken = lm.getToken(resource);
                if ((existingToken == null) || (!existingToken.equals(token))) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                } else {
                    lm.removeLock(resource);
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to process UNLOCK request on {}", req.getPathInfo(), e);
            throw new ServletException("Failed process UNLOCK request", e);
        }
    }
}
