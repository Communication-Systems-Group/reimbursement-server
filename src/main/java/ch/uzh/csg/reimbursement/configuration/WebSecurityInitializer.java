package ch.uzh.csg.reimbursement.configuration;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class WebSecurityInitializer extends AbstractSecurityWebApplicationInitializer {

	public WebSecurityInitializer() {
		super(WebSecurityConfiguration.class);
	}

	//	@Override
	//	protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
	//		insertFilters(servletContext,  new MultipartFilter());
	//	}

}
