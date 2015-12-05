package ch.uzh.csg.reimbursement.rest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;

import javax.servlet.http.Cookie;

//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.csg.reimbursement.configuration.HibernateConfiguration;
import ch.uzh.csg.reimbursement.configuration.LdapConfiguration;
import ch.uzh.csg.reimbursement.configuration.MailConfiguration;
import ch.uzh.csg.reimbursement.configuration.WebMvcConfiguration;
import ch.uzh.csg.reimbursement.configuration.WebSecurityConfiguration;
import ch.uzh.csg.reimbursement.dto.CroppingDto;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HibernateConfiguration.class, LdapConfiguration.class, MailConfiguration.class,
		WebMvcConfiguration.class, WebSecurityConfiguration.class })
@WebAppConfiguration

public class ExpenseResourceIT {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	private MockMvc mvc;

	@Before
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).addFilter( springSecurityFilterChain ).build();
	}

	@Ignore
	@Test
	@WithMockUser(username = "junior", roles = { "USER", "ADMIN", "REGISTERED_USER" })
	public void createExpense() throws Exception {
		//TODO
	}

	@Ignore
	@Test
	public void getAllExpensesForUser() throws Exception {
		//TODO
	}


	@Test
	@Ignore
	public void connectionTest() throws Exception {
		mvc
		.perform(get("/api/user"))
		.andExpect(status().isUnauthorized());
	}

	@Test
	@Ignore
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
	@Ignore
	public void performCorrectLogin() throws Exception{
		RequestBuilder requestBuilder = formLogin().user("junior").password("password");
		mvc.perform(requestBuilder)
		.andDo(print())
		.andExpect(status().isOk());
	}

	@Test
	public void setSignatureTest() throws Exception{
		RequestBuilder requestBuilder = formLogin().user("junior").password("password");
		MvcResult result = mvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
		Cookie[] c = result.getResponse().getCookies();

		File f = new File("C:\\Users\\Christian\\workspace\\reimbursement-server\\src\\main\\resources\\img\\uzh_card_new.png");
		FileInputStream fi1 = new FileInputStream(f);
		MockMultipartFile fstmp = new MockMultipartFile("upload", f.getName(), "multipart/form-data",fi1);



		mvc.perform(fileUpload("/api/user/signature").file(fstmp).with(csrf().asHeader()))
		.andDo(print())
		.andExpect(status().isUnauthorized());

		mvc.perform(fileUpload("/api/user/signature").file(fstmp).cookie(c).with(csrf().asHeader()))
		.andDo(print())
		.andExpect(status().isUnauthorized());
	}

	@Test
	@Ignore
	public void setSignatureCropTest() throws Exception{
		performCorrectLogin();
		ObjectMapper mapper = new ObjectMapper();
		CroppingDto dto = new CroppingDto();
		dto.setHeight(10);
		dto.setLeft(10);
		dto.setTop(10);
		dto.setWidth(10);

		//Object to JSON in String
		String jsonString = mapper.writeValueAsString(dto);

		mvc
		.perform(post("/api/user/signature/crop").content(jsonString).with(csrf().asHeader()))
		.andDo(print())
		.andExpect(status().is2xxSuccessful());
	}

	@Test
	@Ignore
	public void setPersonellNumber() throws Exception{
		performCorrectLogin();
		mvc
		.perform(post("/api/user/settings/personnel-number").param("personnelNumber", "1071924").with(csrf().asHeader()))
		.andDo(print())
		.andExpect(status().is2xxSuccessful());
	}

	@Test
	@Ignore
	public void setPhoneNumberTest() throws Exception{
		performCorrectLogin();
		mvc
		.perform(post("/api/user/settings/phone-number").param("phoneNumber", "0818542020").with(csrf().asHeader()))
		.andDo(print())
		.andExpect(status().is2xxSuccessful());
	}


	//TODO
	@Test
	public void setIsActiveTest() throws Exception{
		performCorrectLogin();

		setSignatureTest();
		setSignatureCropTest();
		setPersonellNumber();
		setPhoneNumberTest();

		mvc
		.perform(post("/api/user/settings/is-active").param("isActive", "true").with(csrf().asHeader()))
		.andDo(print())
		.andExpect(status().is2xxSuccessful());
	}

}
