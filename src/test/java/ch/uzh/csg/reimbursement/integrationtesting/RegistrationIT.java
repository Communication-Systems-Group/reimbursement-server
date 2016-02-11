package ch.uzh.csg.reimbursement.integrationtesting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import ch.uzh.csg.reimbursement.model.Role;
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
	private String userUid;
	private IntegrationTestHelper helper;

	@Before
	public void setup() throws Exception {
		helper = new IntegrationTestHelper();
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
		RequestBuilder requestBuilder = formLogin().user("junior").password("password");
		MvcResult loginResult = mvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
		session = (MockHttpSession) loginResult.getRequest().getSession();

		if(userUid == null || userUid.isEmpty() ){
			ObjectNode user = helper.getUser(mvc, session);
			userUid = user.get("uid").asText();
		}
	}

	@Test
	public void getUserTest() throws Exception{
		ObjectNode user = helper.getUser(mvc, session);
		assertEquals("junior", user.get("uid").asText());
	}


	@Test
	public void setSignatureAndCropTest() throws Exception {
		//signature upload part
		String string = getClass().getResource("/img/uzh_card_new.png").getFile();
		File f = new File(string);
		FileInputStream fi1 = new FileInputStream(f);
		MockMultipartFile fstmp = new MockMultipartFile("file", f.getName(), MediaType.IMAGE_PNG_VALUE, fi1);

		mvc.perform(fileUpload("/user/signature").file(fstmp).with(csrf().asHeader())).andDo(print())
		.andExpect(status().isUnauthorized());

		mvc.perform(fileUpload("/user/signature").file(fstmp).session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());

		mvc.perform(get("/user/signature").session(session)).andExpect(status().is2xxSuccessful());

		assertNotNull(userRepo.findByUid(userUid).getSignature());
		assertEquals(fstmp.getContentType(), userRepo.findByUid(userUid).getSignature().getContentType());
		assertTrue(Arrays.equals(fstmp.getBytes(), userRepo.findByUid(userUid).getSignature().getContent()));

		//cropping part
		ObjectMapper mapper = new ObjectMapper();
		CroppingDto dto = new CroppingDto();
		dto.setHeight(100);
		dto.setLeft(0);
		dto.setTop(0);
		dto.setWidth(100);

		String jsonString = mapper.writeValueAsString(dto);
		mvc.perform(post("/user/signature/crop").content(jsonString).contentType(MediaType.APPLICATION_JSON)
				.with(csrf().asHeader())).andDo(print()).andExpect(status().isUnauthorized());
		mvc.perform(post("/user/signature/crop").content(jsonString).contentType(MediaType.APPLICATION_JSON).session(session)
				.with(csrf().asHeader())).andDo(print()).andExpect(status().is2xxSuccessful());

		Signature signature = userRepo.findByUid(userUid).getSignature();
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

		//TODO check different exception cases like not valid cropping exception and so on :)
	}

	@Test
	public void setPersonellNumberTest() throws Exception {
		String persNr = "1048632";
		mvc.perform(put("/user/settings/personnel-number").param("personnelNumber",persNr ).with(csrf().asHeader()))
		.andDo(print()).andExpect(status().isUnauthorized());

		mvc.perform(put("/user/settings/personnel-number").param("personnelNumber", persNr).session(session)
				.with(csrf().asHeader())).andDo(print()).andExpect(status().is2xxSuccessful());

		assertEquals(persNr ,userRepo.findByUid(userUid).getPersonnelNumber());
	}

	@Test
	public void setPhoneNumberTest() throws Exception {
		String phoneNr = "0818546666";
		mvc.perform(put("/user/settings/phone-number").param("phoneNumber", phoneNr).with(csrf().asHeader()))
		.andDo(print()).andExpect(status().isUnauthorized());

		mvc.perform(put("/user/settings/phone-number").param("phoneNumber", phoneNr).session(session)
				.with(csrf().asHeader())).andDo(print()).andExpect(status().is2xxSuccessful());

		assertEquals(phoneNr ,userRepo.findByUid(userUid).getPhoneNumber());
	}

	@Test
	public void testRegistration() throws Exception{
		setPersonellNumberTest();
		setPhoneNumberTest();
		setSignatureAndCropTest();

		assertTrue(userRepo.findByUid(userUid).getRoles().contains(Role.REGISTERED_USER));
	}

	@Test
	public void setIsActiveTest() throws Exception {
		mvc.perform(put("/user/settings/is-active").param("isActive", "false").session(session).with(csrf().asHeader()))
		.andDo(print()).andExpect(status().is2xxSuccessful());

		ObjectNode user = helper.getUser(mvc, session);
		assertFalse(user.get("isActive").asBoolean(true));
		assertFalse(userRepo.findByUid(userUid).getIsActive());

		mvc.perform(put("/user/settings/is-active").param("isActive", "true").session(session).with(csrf().asHeader()))
		.andDo(print()).andExpect(status().is2xxSuccessful());

		user = helper.getUser(mvc, session);
		assertTrue(user.get("isActive").asBoolean(false));
		assertTrue(userRepo.findByUid(userUid).getIsActive());
	}

	@Test
	public void registerAllUsers() throws Exception{
		// junior should already be logged in (before test method)
		// userUid = "junior";
		// session  = helper.loginUser(mvc, userUid, "password");
		setPersonellNumberTest();
		setPhoneNumberTest();
		setSignatureAndCropTest();

		userUid = "senior";
		session  = helper.loginUser(mvc, userUid, "password");
		setPersonellNumberTest();
		setPhoneNumberTest();
		setSignatureAndCropTest();

		userUid="prof";
		session  = helper.loginUser(mvc, userUid, "password");
		setPersonellNumberTest();
		setPhoneNumberTest();
		setSignatureAndCropTest();

		userUid="fadmin";
		session  = helper.loginUser(mvc, userUid, "password");
		setPersonellNumberTest();
		setPhoneNumberTest();
		setSignatureAndCropTest();

		userUid = "fadmin2";
		session  = helper.loginUser(mvc, userUid, "password");
		setPersonellNumberTest();
		setPhoneNumberTest();
		setSignatureAndCropTest();

		userUid="depman";
		session  = helper.loginUser(mvc, userUid, "password");
		setPersonellNumberTest();
		setPhoneNumberTest();
		setSignatureAndCropTest();

		userUid="headinst";
		session  = helper.loginUser(mvc, userUid, "password");
		setPersonellNumberTest();
		setPhoneNumberTest();
		setSignatureAndCropTest();
	}
}
