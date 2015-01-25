/** SWDS - a sample webdav server. 
 * Copyright 2014-2015 MeBigFatGuy.com 
 * Copyright 2014-2015 Dave Brosius 
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PutHandler implements HttpHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutHandler.class);

    @Override
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp, File rootDirectory) throws ServletException {
        File file = new File(rootDirectory, req.getPathInfo());
        try (InputStream is = new BufferedInputStream(req.getInputStream());
             OutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
            byte[] data = new byte[10240];
            int len = is.read(data);
            while (len >= 0) {
                os.write(data, 0, len);
                len = is.read(data);
            }
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException e) {
            LOGGER.error("Failed to process PUT request on {}", req.getPathInfo(), e);
            throw new ServletException("Failed to process PUT request", e);
        }
    }
}
