package com.clsa.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
/**
 * This is for CORS
 */
public class ApiOriginFilter implements javax.servlet.Filter {
	
	private static final Logger logger = LogManager.getLogger(ApiOriginFilter.class);
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		logger.info("Client IP: {}", request.getRemoteAddr());
		
		HttpServletResponse res = (HttpServletResponse) response;
		res.addHeader("Access-Control-Allow-Origin", "*");
		res.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
		res.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
		chain.doFilter(request, response);
		
	}
	
	@Override
	public void destroy() {
		// Do nothing for destroy method
	}
	
	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		// Do nothing for init method
	}
}