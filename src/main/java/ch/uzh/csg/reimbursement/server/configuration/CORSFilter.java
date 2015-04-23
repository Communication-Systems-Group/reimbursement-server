package ch.uzh.csg.reimbursement.server.configuration;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

@Component
public class CORSFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse servletResponse = (HttpServletResponse) response;
		// TODO Sebi | specify an allowed host for CORS
		servletResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:9005");
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) {}

	@Override
	public void destroy() {}
}
