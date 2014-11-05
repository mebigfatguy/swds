package com.mebigfatguy.swds;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebDavAuthFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDavAuthFilter.class);
    private static final Pattern BASIC_PATTERN = Pattern.compile("Basic\\s+(.*)");

    @Override
    public void init(FilterConfig conf) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) req;
        HttpServletResponse httpResponse = (HttpServletResponse) resp;

        String auth = httpRequest.getHeader("authorization");
        if (auth == null) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setHeader("WWW-Authenticate",  "Basic realm=\"foobar\"");
            httpResponse.setHeader("DAV", "1,2");
            return;
        }

        Matcher m = BASIC_PATTERN.matcher(auth);
        if (m.matches()) {
            String userpass = new String(DatatypeConverter.parseBase64Binary(m.group(1)), StandardCharsets.UTF_8);
            String[] parts = userpass.split(":");
            LOGGER.info("Authentication with {} : {}", parts[0], parts[1]);
        } else {
            LOGGER.error("AuthFilter {}", auth);
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setHeader("WWW-Authenticate",  "Basic realm=\"foobar\"");
            httpResponse.setHeader("DAV", "1,2");
            return;
        }

        chain.doFilter(req,  resp);
    }
}
