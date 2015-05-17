package ch.uzh.csg.reimbursement.configuration;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

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

	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(){
		return new MappingJackson2HttpMessageConverter();
	};

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.csrf()
		.csrfTokenRepository(csrfTokenRepository())
		.and()
		.addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);

		http
		.exceptionHandling()
		.authenticationEntryPoint(authenticationEntryPoint)
		.and().authorizeRequests()
		.antMatchers("/static/**").permitAll()
		.antMatchers("/languages/**").permitAll()
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

	private Filter csrfHeaderFilter() {
		return new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(HttpServletRequest request,
					HttpServletResponse response, FilterChain filterChain)
							throws ServletException, IOException {
				CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class
						.getName());
				if (csrf != null) {
					Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
					String token = csrf.getToken();
					if (cookie == null || token != null
							&& !token.equals(cookie.getValue())) {
						cookie = new Cookie("XSRF-TOKEN", token);
						cookie.setPath("/");
						response.addCookie(cookie);
					}
				}
				filterChain.doFilter(request, response);
			}
		};
	}

	private CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		repository.setHeaderName("X-XSRF-TOKEN");
		return repository;
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
