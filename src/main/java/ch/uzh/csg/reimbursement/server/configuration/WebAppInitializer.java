package ch.uzh.csg.reimbursement.server.configuration;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class WebAppInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext container) throws ServletException {

		FilterRegistration.Dynamic corsFilter = container.addFilter("corsFilter", CORSFilter.class);
		corsFilter.addMappingForUrlPatterns(null, false, "/*");

		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(SpringMvcConfiguration.class);
		context.setServletContext(container);

		ServletRegistration.Dynamic servlet = container.addServlet("dispatcher",  new DispatcherServlet(context));

		servlet.setLoadOnStartup(1);
		servlet.addMapping("/");

	}

}
