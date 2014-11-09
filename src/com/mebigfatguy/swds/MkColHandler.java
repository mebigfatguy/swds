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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MkColHandler implements HttpHandler {

    @Override
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp, File rootDirectory) throws ServletException {
        
        String len = req.getHeader("Content-Length");
        if ((len != null) && (Integer.parseInt(len) > 0)) {
            resp.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return;
        }
        
        File file = new File(rootDirectory, req.getPathInfo());
        if (file.exists()) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            return;
        }
        
        File parent = file.getParentFile();
        if (!parent.exists()) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            return;
        }
        
        if (file.mkdir()) {
            resp.setStatus(HttpServletResponse.SC_CREATED);
        }
        else {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
        }
    }
}
