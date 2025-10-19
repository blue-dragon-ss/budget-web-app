package com.example.minimal.common;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ResponseHeaderFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
	    HttpServletResponse res = (HttpServletResponse) response;
	    res.setHeader("Referrer-Policy", "no-referrer");
	    res.setHeader("X-Frame-Options", "DENY");
	    res.setHeader("X-Content-Type-Options", "nosniff");
	    res.setHeader("Cache-Control", "no-store");
	    chain.doFilter(request, response);
	}

}
