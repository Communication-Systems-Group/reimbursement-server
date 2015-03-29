package ch.uzh.csg.reimbursement.server.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;

@Configuration
@EnableSwagger
@ComponentScan(basePackages = "com.mangofactory.swagger")
public class SwaggerConfiguration {

	private SpringSwaggerConfig springSwaggerConfig;

	@Autowired
	public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
		this.springSwaggerConfig = springSwaggerConfig;
	}

	@Bean
	public SwaggerSpringMvcPlugin customImplementation() {
		return new SwaggerSpringMvcPlugin(this.springSwaggerConfig).apiInfo(apiInfo()).includePatterns(".*");
	}

	private ApiInfo apiInfo() {
		return new ApiInfo(
				"Reimbursement Server API",
				"This API depicts the full range of REST interfaces of the Reimbursement Server. It allows to make calls directly from the web.",
				"http://www.apache.org/licenses/LICENSE-2.0.html",
				"",
				"Apache 2.0",
				"http://www.apache.org/licenses/LICENSE-2.0.html"
				);
	}
}
