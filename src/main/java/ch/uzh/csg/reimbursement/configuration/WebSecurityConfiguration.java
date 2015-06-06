package ch.uzh.csg.reimbursement.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import ch.uzh.csg.reimbursement.security.CsrfHeaderFilter;
import ch.uzh.csg.reimbursement.security.FormLoginFailureHandler;
import ch.uzh.csg.reimbursement.security.FormLoginSuccessHandler;
import ch.uzh.csg.reimbursement.security.HttpAuthenticationEntryPoint;
import ch.uzh.csg.reimbursement.security.HttpLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
@ComponentScan({ "ch.uzh.csg.reimbursement.security" })
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private FormLoginSuccessHandler authSuccessHandler;

	@Autowired
	private HttpAuthenticationEntryPoint authenticationEntryPoint;

	@Autowired
	private FormLoginFailureHandler authFailureHandler;

	@Autowired
	private HttpLogoutSuccessHandler logoutSuccessHandler;

	/* JSON - Object mapper for use in the authHandlers */
	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		return new MappingJackson2HttpMessageConverter();
	}

	/* Enables File Upload through REST */
	@Bean
	public CommonsMultipartResolver filterMultipartResolver() {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setMaxUploadSize(20000000);
		return resolver;
	}

	/* Token Repo for use with CsrfHeaderFilter */
	private CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		repository.setHeaderName("X-XSRF-TOKEN");
		return repository;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf()
		.csrfTokenRepository(csrfTokenRepository())
		.and()
		.addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class);

		http.exceptionHandling()
		.authenticationEntryPoint(authenticationEntryPoint)
		.and().authorizeRequests()
		// allow front-end folders located in src/main/webapp/static
		.antMatchers("/static/**").permitAll()
		// allow CORS's options preflight
		.antMatchers(HttpMethod.OPTIONS,"/api/**").permitAll()
		// allow specific rest resources
		.antMatchers("/api/public/**").permitAll()
		//TODO Chrigi remove if not used anymore - also remove the csrfToken page from frontend
		.antMatchers("/testingpublic/**").permitAll()
		.antMatchers("/api-docs/**", "/swagger-ui/**").permitAll()
		// block everything else
		.anyRequest().fullyAuthenticated()
		.and()
		.formLogin().permitAll()
		.loginProcessingUrl("/api/login")
		.successHandler(authSuccessHandler)
		.failureHandler(authFailureHandler)
		.and()
		.logout().permitAll()
		.logoutUrl("/api/logout")
		.logoutSuccessHandler(logoutSuccessHandler)
		.and()
		.sessionManagement().maximumSessions(1);
	}

	@Configuration
	protected static class AuthenticationConfiguration extends GlobalAuthenticationConfigurerAdapter {
		@Override
		public void init(AuthenticationManagerBuilder auth) throws Exception {
			// TODO Chrigi remove if not used anymore
			// Howto link: https://github.com/spring-projects/spring-security-javaconfig/blob/master/spring-security-javaconfig/src/test/groovy/org/springframework/security/config/annotation/authentication/ldap/NamespaceLdapAuthenticationProviderTestsConfigs.java
			//			auth.
			//			ldapAuthentication()
			//			// .userDnPattern only used for direct binding to the user -> userSearchFilter for searching
			//			.userDnPatterns("uid={0}")
			//			.contextSource()
			//			.url("ldap://ldap.forumsys.com:389/dc=example,dc=com");

			auth
			.ldapAuthentication()
			.userSearchFilter("uid={0}")
			.groupSearchBase("ou=Groups")
			.contextSource()
			.ldif("classpath:test-server.ldif")
			.root("dc=ifi,dc=uzh,dc=ch");
		}
	}

}
