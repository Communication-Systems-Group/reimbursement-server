package ch.uzh.csg.reimbursement.configuration;

import javax.servlet.ServletContext;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.multipart.support.MultipartFilter;

import ch.uzh.csg.reimbursement.security.CorsHeaderFilter;

public class WebSecurityInitializer extends AbstractSecurityWebApplicationInitializer {

	@Override
	protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
		insertFilters(servletContext,  new MultipartFilter());
		insertFilters(servletContext, new CorsHeaderFilter());
	}

}
