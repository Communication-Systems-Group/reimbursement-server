package ch.uzh.csg.reimbursement.configuration;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SpringSecurityInitializer extends AbstractSecurityWebApplicationInitializer {

	public SpringSecurityInitializer() {
		super(WebSecurityConfiguration.class);
	}

	//	@Override
	//	protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
	//		insertFilters(servletContext, new MultipartFilter());
	//	}

}
