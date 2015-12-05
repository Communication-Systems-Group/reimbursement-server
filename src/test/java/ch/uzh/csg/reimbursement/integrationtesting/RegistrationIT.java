package ch.uzh.csg.reimbursement.integrationtesting;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
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
public class RegistrationIT {

	@Autowired
	private  WebApplicationContext context;

	private  MockMvc mvc;
	private  MockHttpSession session;


	@Before
	public void setup() throws Exception{
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
		RequestBuilder requestBuilder = formLogin().user("junior").password("password");
		MvcResult result = mvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
		session = (MockHttpSession)result.getRequest().getSession();
	}

	@Test
	public void setSignatureTest() throws Exception{
		File f = new File("C:\\Users\\Christian\\workspace\\reimbursement-server\\src\\main\\resources\\img\\uzh_card_new.png");
		FileInputStream fi1 = new FileInputStream(f);
		MockMultipartFile fstmp = new MockMultipartFile("upload", f.getName(), "multipart/form-data",fi1);

		mvc.perform(fileUpload("/user/signature").file(fstmp).with(csrf().asHeader()))
		.andDo(print())
		.andExpect(status().isUnauthorized());

		mvc.perform(fileUpload("/user/signature").file(fstmp).session(session).with(csrf().asHeader()))
		.andDo(print())
		.andExpect(status().is2xxSuccessful());
	}

	@Test
	public void setSignatureCropTest() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		CroppingDto dto = new CroppingDto();
		dto.setHeight(100);
		dto.setLeft(0);
		dto.setTop(0);
		dto.setWidth(100);

		//Object to JSON in String
		String jsonString = mapper.writeValueAsString(dto);
		mvc
		.perform(post("/user/signature/crop").content(jsonString).contentType("application/json").with(csrf().asHeader()))
		.andDo(print())
		.andExpect(status().isUnauthorized());

		mvc
		.perform(post("/user/signature/crop").content(jsonString).contentType("application/json").session(session).with(csrf().asHeader()))
		.andDo(print())
		.andExpect(status().is2xxSuccessful());
	}

	@Test
	public void setPersonellNumber() throws Exception{
		mvc
		.perform(put("/user/settings/personnel-number").param("personnelNumber", "1071924").with(csrf().asHeader()))
		.andDo(print())
		.andExpect(status().isUnauthorized());

		mvc
		.perform(put("/user/settings/personnel-number").param("personnelNumber", "1071924").session(session).with(csrf().asHeader()))
		.andDo(print())
		.andExpect(status().is2xxSuccessful());
	}

	@Test
	public void setPhoneNumberTest() throws Exception{
		mvc
		.perform(put("/user/settings/phone-number").param("phoneNumber", "0818542020").with(csrf().asHeader()))
		.andDo(print())
		.andExpect(status().isUnauthorized());

		mvc
		.perform(put("/user/settings/phone-number").param("phoneNumber", "0818542020").session(session).with(csrf().asHeader()))
		.andDo(print())
		.andExpect(status().is2xxSuccessful());
	}

}
