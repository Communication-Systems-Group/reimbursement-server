package ch.uzh.csg.reimbursement.integrationtesting;

import static org.junit.Assert.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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

	private MockMvc mvc;
	private MockHttpSession session;

	@Before
	public void setup() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}


	@Test
	public void performWrongLogin() throws Exception{
		RequestBuilder requestBuilder = formLogin().user("junir").password("password");
		mvc.perform(requestBuilder)
		.andDo(print())
		.andExpect(status().isUnauthorized());

		requestBuilder = formLogin().user("junior").password("passwod");
		MvcResult loginResult = mvc.perform(requestBuilder).andExpect(status().isUnauthorized()).andReturn();
		session = (MockHttpSession) loginResult.getRequest().getSession();

		mvc
		.perform(get("/user").session(session))
		.andExpect(status().isUnauthorized());
	}

	@Test
	public void performCorrectLogin() throws Exception{
		RequestBuilder requestBuilder = formLogin().user("junior").password("password");
		MvcResult loginResult = mvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
		session = (MockHttpSession) loginResult.getRequest().getSession();

		assertFalse(session.isInvalid());

		mvc
		.perform(get("/user").session(session))
		.andExpect(status().isOk());
	}
}
