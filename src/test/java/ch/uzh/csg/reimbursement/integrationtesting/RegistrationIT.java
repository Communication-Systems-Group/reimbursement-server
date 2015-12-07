package ch.uzh.csg.reimbursement.integrationtesting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;

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
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.uzh.csg.reimbursement.configuration.HibernateConfiguration;
import ch.uzh.csg.reimbursement.configuration.LdapConfiguration;
import ch.uzh.csg.reimbursement.configuration.MailConfiguration;
import ch.uzh.csg.reimbursement.configuration.WebMvcConfiguration;
import ch.uzh.csg.reimbursement.configuration.WebSecurityConfiguration;
import ch.uzh.csg.reimbursement.dto.CroppingDto;
import ch.uzh.csg.reimbursement.model.Signature;
import ch.uzh.csg.reimbursement.repository.UserRepositoryProvider;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HibernateConfiguration.class, LdapConfiguration.class, MailConfiguration.class,
		WebMvcConfiguration.class, WebSecurityConfiguration.class })
@WebAppConfiguration
public class RegistrationIT {

	@Autowired
	private WebApplicationContext context;
	@Autowired
	private UserRepositoryProvider userRepo;

	private MockMvc mvc;
	private MockHttpSession session;
	private String juniorUid;

	@Before
	public void setup() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
		RequestBuilder requestBuilder = formLogin().user("junior").password("password");
		MvcResult loginResult = mvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
		session = (MockHttpSession) loginResult.getRequest().getSession();

		if(juniorUid == null || juniorUid.isEmpty() ){
			String result = mvc
					.perform(get("/user").session(session))
					.andDo(print())
					.andExpect(status().is2xxSuccessful())
					.andReturn()
					.getResponse()
					.getContentAsString();

			ObjectNode user = new ObjectMapper().readValue(result, ObjectNode.class);
			juniorUid = user.get("uid").asText();
		}
	}

	@Test
	public void setSignatureAndCropTest() throws Exception {
		File f = new File(
				"C:\\Users\\Christian\\workspace\\reimbursement-server\\src\\main\\resources\\img\\uzh_card_new.png");
		FileInputStream fi1 = new FileInputStream(f);
		MockMultipartFile fstmp = new MockMultipartFile("file", f.getName(), "image/png", fi1);

		mvc.perform(fileUpload("/user/signature").file(fstmp).with(csrf().asHeader())).andDo(print())
		.andExpect(status().isUnauthorized());

		mvc.perform(fileUpload("/user/signature").file(fstmp).session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());



		mvc.perform(get("/user/signature").session(session)).andExpect(status().is2xxSuccessful());

		assertNotNull(userRepo.findByUid(juniorUid).getSignature());
		assertEquals(fstmp.getContentType(), userRepo.findByUid(juniorUid).getSignature().getContentType());

		ObjectMapper mapper = new ObjectMapper();
		CroppingDto dto = new CroppingDto();
		dto.setHeight(100);
		dto.setLeft(0);
		dto.setTop(0);
		dto.setWidth(100);

		String jsonString = mapper.writeValueAsString(dto);
		mvc.perform(post("/user/signature/crop").content(jsonString).contentType("application/json")
				.with(csrf().asHeader())).andDo(print()).andExpect(status().isUnauthorized());
		mvc.perform(post("/user/signature/crop").content(jsonString).contentType("application/json").session(session)
				.with(csrf().asHeader())).andDo(print()).andExpect(status().is2xxSuccessful());

		Signature signature = userRepo.findByUid(juniorUid).getSignature();
		Field cw = ReflectionUtils.findField(Signature.class, "cropWidth");
		Field ch = ReflectionUtils.findField(Signature.class, "cropHeight");
		Field ct = ReflectionUtils.findField(Signature.class, "cropTop");
		Field cl = ReflectionUtils.findField(Signature.class, "cropLeft");
		ReflectionUtils.makeAccessible(cw);
		ReflectionUtils.makeAccessible(ch);
		ReflectionUtils.makeAccessible(ct);
		ReflectionUtils.makeAccessible(cl);
		assertEquals(dto.getWidth(), cw.getInt(signature));
		assertEquals(dto.getHeight(), ch.getInt(signature));
		assertEquals(dto.getTop(), ct.getInt(signature));
		assertEquals(dto.getLeft(), cl.getInt(signature));
	}

	@Test
	public void setPersonellNumber() throws Exception {
		String persNr = "1071924";
		mvc.perform(put("/user/settings/personnel-number").param("personnelNumber",persNr ).with(csrf().asHeader()))
		.andDo(print()).andExpect(status().isUnauthorized());

		mvc.perform(put("/user/settings/personnel-number").param("personnelNumber", persNr).session(session)
				.with(csrf().asHeader())).andDo(print()).andExpect(status().is2xxSuccessful());

		assertEquals(persNr ,userRepo.findByUid(juniorUid).getPersonnelNumber());
	}

	@Test
	public void setPhoneNumberTest() throws Exception {
		String phoneNr = "0818542020";
		mvc.perform(put("/user/settings/phone-number").param("phoneNumber", phoneNr).with(csrf().asHeader()))
		.andDo(print()).andExpect(status().isUnauthorized());

		mvc.perform(put("/user/settings/phone-number").param("phoneNumber", phoneNr).session(session)
				.with(csrf().asHeader())).andDo(print()).andExpect(status().is2xxSuccessful());

		assertEquals(phoneNr ,userRepo.findByUid(juniorUid).getPhoneNumber());
	}
}
