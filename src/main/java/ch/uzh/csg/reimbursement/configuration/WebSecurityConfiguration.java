package ch.uzh.csg.reimbursement.configuration;

import static ch.uzh.csg.reimbursement.configuration.BuildLevel.DEVELOPMENT;
import static ch.uzh.csg.reimbursement.configuration.BuildLevel.INTEGRATION;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import ch.uzh.csg.reimbursement.application.ldap.LdapUserDetailsAuthoritiesPopulator;
import ch.uzh.csg.reimbursement.security.CsrfHeaderFilter;
import ch.uzh.csg.reimbursement.security.FormLoginFailureHandler;
import ch.uzh.csg.reimbursement.security.FormLoginSuccessHandler;
import ch.uzh.csg.reimbursement.security.HttpAuthenticationEntryPoint;
import ch.uzh.csg.reimbursement.security.HttpLogoutSuccessHandler;
import ch.uzh.csg.reimbursement.security.ResourceAccessDeniedHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	private final Logger LOG = LoggerFactory.getLogger(WebSecurityConfiguration.class);

	@Autowired
	private HttpAuthenticationEntryPoint authenticationEntryPoint;

	@Autowired
	private ResourceAccessDeniedHandler accessDeniedHandler;

	@Autowired
	private FormLoginSuccessHandler authSuccessHandler;

	@Autowired
	private FormLoginFailureHandler authFailureHandler;

	@Autowired
	private HttpLogoutSuccessHandler logoutSuccessHandler;

	@Autowired
	private UserDetailsService userDetailsService;

	@Value("${reimbursement.filesize.maxUploadFileSize}")
	private long maxUploadFileSize;

	@Value("${reimbursement.buildLevel}")
	private BuildLevel buildLevel;

	@Value("${reimbursement.ldap.url}")
	private String ldapUrl;

	@Value("${reimbursement.ldap.base}")
	private String ldapBase;

	/* Enables File Upload through REST */
	@Bean
	public CommonsMultipartResolver filterMultipartResolver() {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setMaxUploadSize(maxUploadFileSize);
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
		.accessDeniedHandler(accessDeniedHandler)
		.and().authorizeRequests()
		// allow CORS's options preflight
		.antMatchers(HttpMethod.OPTIONS,"/**").permitAll()
		// allow specific rest resources
		.antMatchers("/public/**", "/expenses/**").permitAll()
		.antMatchers("/api-docs/**", "/swagger-ui/**").permitAll()
		// block everything else
		.anyRequest().fullyAuthenticated()
		.and()
		.formLogin().permitAll()
		.loginProcessingUrl("/login")
		.successHandler(authSuccessHandler)
		.failureHandler(authFailureHandler)
		.and()
		.logout()
		//		check if this is needed. only logged in users should be able to logout :)
		//		.permitAll()
		.logoutUrl("/logout")
		.logoutSuccessHandler(logoutSuccessHandler)
		.and()
		.sessionManagement().maximumSessions(1);
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

		if(buildLevel == DEVELOPMENT || buildLevel == INTEGRATION) {
			LOG.info("Development/Integration Mode: Local LDAP server will be started for the authentication. The user database is remotely loaded.");

			auth.ldapAuthentication()
			.ldapAuthoritiesPopulator(new LdapUserDetailsAuthoritiesPopulator(userDetailsService))
			.userSearchFilter("uid={0}")
			.groupSearchBase("ou=Groups")
			.contextSource()
			.ldif("classpath:development-server.ldif")
			.root(ldapBase);
		}
		else { // if buildLevel == PRODUCTION
			LOG.info("Production Mode: Remote LDAP server is used for authentication and and also for the user database.");

			auth.ldapAuthentication()
			.ldapAuthoritiesPopulator(new LdapUserDetailsAuthoritiesPopulator(userDetailsService))
			.userSearchFilter("uid={0}")
			.groupSearchBase("ou=Groups")
			.contextSource()
			.url(ldapUrl + "/" + ldapBase);
		}
	}
}
