/** SWDS - a sample webdav server. 
 * Copyright 2014-2019 MeBigFatGuy.com 
 * Copyright 2014-2019 Dave Brosius 
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetHandler implements HttpHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetHandler.class);

    @Override
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp, File rootDirectory) throws ServletException {
        try {
            File file = new File(rootDirectory, req.getPathInfo());
            try (InputStream is = new BufferedInputStream(new FileInputStream(file));
                 OutputStream os = new BufferedOutputStream(resp.getOutputStream())) {
                resp.setStatus(MULTI_STATUS);
                resp.setContentLength((int) file.length());
                byte[] data = new byte[10240];
                int len = is.read(data);
                while (len >= 0) {
                    os.write(data, 0, len);
                    len = is.read(data);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to GET item", e);
            throw new ServletException("Failed to GET item", e);
        }
    }
}
