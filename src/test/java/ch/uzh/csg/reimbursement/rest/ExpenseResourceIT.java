package ch.uzh.csg.reimbursement.rest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
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

public class ExpenseResourceIT {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@Before
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@Test
	@WithMockUser(username = "junior", roles = { "USER", "ADMIN", "REGISTERED_USER" })

	public void createExpense() throws Exception {
		mvc
		.perform(post("/expenses").with(csrf().asHeader()))
		.andExpect(status().isOk());
	}

	@Test
	public void getAllExpensesForUser() throws Exception {
		mvc
		.perform(get("/expenses"))
		.andExpect(status().isOk());
	}


	@Test
	public void connectionTest() throws Exception {
		mvc
		.perform(get("/api/user"))
		.andExpect(status().isUnauthorized());
	}

}
