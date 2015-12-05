package ch.uzh.csg.reimbursement.integrationtesting;

import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ch.uzh.csg.reimbursement.configuration.HibernateConfiguration;
import ch.uzh.csg.reimbursement.configuration.LdapConfiguration;
import ch.uzh.csg.reimbursement.configuration.MailConfiguration;
import ch.uzh.csg.reimbursement.configuration.WebMvcConfiguration;
import ch.uzh.csg.reimbursement.configuration.WebSecurityConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HibernateConfiguration.class, LdapConfiguration.class, MailConfiguration.class,
		WebMvcConfiguration.class, WebSecurityConfiguration.class })
@WebAppConfiguration
public class LoginIT {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	private MockMvc mvc;

	@Before
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).addFilter( springSecurityFilterChain ).build();
	}


	@Test
	public void performWrongLogin() throws Exception{
		RequestBuilder requestBuilder = formLogin().user("junir").password("password");
		mvc.perform(requestBuilder)
		.andDo(print())
		.andExpect(status().isUnauthorized());

		requestBuilder = formLogin().user("junior").password("passwod");
		mvc.perform(requestBuilder)
		.andDo(print())
		.andExpect(status().isUnauthorized());
	}

	@Test
	public void performCorrectLogin() throws Exception{
		RequestBuilder requestBuilder = formLogin().user("junior").password("password");
		mvc.perform(requestBuilder)
		.andDo(print())
		.andExpect(status().isOk());

		//TODO check for session authentication
		fail();
	}

}
