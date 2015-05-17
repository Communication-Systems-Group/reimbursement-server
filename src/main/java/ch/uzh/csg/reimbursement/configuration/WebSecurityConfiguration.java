package ch.uzh.csg.reimbursement.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;

import ch.uzh.csg.reimbursement.security.FormLoginFailureHandler;
import ch.uzh.csg.reimbursement.security.FormLoginSuccessHandler;
import ch.uzh.csg.reimbursement.security.HttpAuthenticationEntryPoint;
import ch.uzh.csg.reimbursement.security.HttpLogoutSuccessHandler;
import ch.uzh.csg.reimbursement.security.StatelessCsrfFilter;

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

	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(){
		return new MappingJackson2HttpMessageConverter();
	};

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		//TODO should we use the cookie or the header based csrf filter approach? for angular probably better cookie
		//.addFilterAfter(new CsrfTokenBindingFilter(), CsrfFilter.class)
		.csrf().disable()
		.addFilterAfter(new StatelessCsrfFilter(), CsrfFilter.class)
		.exceptionHandling()
		.authenticationEntryPoint(authenticationEntryPoint)
		.and().authorizeRequests()
		.antMatchers("/api/user/**").permitAll()
		.antMatchers("/testingpublic/**").permitAll()
		.antMatchers("/api-docs/**", "/swagger-ui/**").permitAll()
		.anyRequest().fullyAuthenticated()
		.and()
		.formLogin()
		.permitAll()
		.loginProcessingUrl("/api/login")
		.successHandler(authSuccessHandler)
		.failureHandler(authFailureHandler)
		.and()
		.logout()
		.permitAll()
		.logoutUrl("/api/logout")
		.logoutSuccessHandler(logoutSuccessHandler)
		.and()
		.sessionManagement()
		.maximumSessions(1);
	}

	@Configuration
	protected static class AuthenticationConfiguration extends GlobalAuthenticationConfigurerAdapter {
		@Override
		public void init(AuthenticationManagerBuilder auth) throws Exception {
			auth.inMemoryAuthentication()
			.withUser("user").password("user").roles("USER")
			.and()
			.withUser("admin").password("password").roles("USER", "ADMIN");

			//TODO sebi | implement an LDAP authentication
			/*auth.ldapAuthentication()
				.userDnPatterns("uid={0},ou=people")
				.groupSearchBase("ou=groups")
				.contextSource().ldif("classpath:test-server.ldif");*/
		}
	}

}
