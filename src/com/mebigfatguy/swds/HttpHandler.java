package com.mebigfatguy.swds;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HttpHandler {

    static final int MULTI_STATUS = 207;
    
	void handleRequest(HttpServletRequest req, HttpServletResponse resp, File rootDirectory);
}
