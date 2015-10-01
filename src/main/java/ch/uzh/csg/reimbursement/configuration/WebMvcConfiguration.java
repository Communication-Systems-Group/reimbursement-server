package ch.uzh.csg.reimbursement.configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.oxm.castor.CastorMarshaller;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@EnableScheduling
@ComponentScan(basePackages = { "ch.uzh.csg.reimbursement" })
@PropertySource({ "classpath:application.properties" })
@EnableCaching
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/swagger-ui/**").addResourceLocations("/swagger-overlay/",
				"classpath:/META-INF/resources/webjars/swagger-ui/");
	}

	/*
	 * Enables the @Value annotation in classes of this package.
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	/*
	 * Enables Caching for grabbing the exchange rates.
	 */
	@Bean
	public CacheManager cacheManager() {
		return new ConcurrentMapCacheManager("exchange-rates");
	}

	/*
	 * Necessary for the XML conversion (object -> XML)
	 */
	@Bean
	public CastorMarshaller castorMarshaller() {
		CastorMarshaller marshaller = new CastorMarshaller();
		Resource xmlMappingFile = new PathMatchingResourcePatternResolver().getResource("classpath:xml-mapping.xml");
		marshaller.setMappingLocation(xmlMappingFile);
		return marshaller;
	}

}
