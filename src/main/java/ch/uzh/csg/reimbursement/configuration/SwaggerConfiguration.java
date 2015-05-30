package ch.uzh.csg.reimbursement.configuration;

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
				"Reimbursement IFI (API)",
				"This API documentation display a full overview of the various REST interfaces of the Reimbursement IFI API. You can directly produce all calls from this page. Be aware, that certain interfaces need an authentication. You can authenticate yourself by entering the credentials in the header bar.",
				"", "", "", "");
	}
}
