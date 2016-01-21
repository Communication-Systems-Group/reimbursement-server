package ch.uzh.csg.reimbursement.integrationtesting;

import static org.apache.xmlgraphics.util.MimeConstants.MIME_PDF;
import static org.junit.Assert.assertEquals;
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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.uzh.csg.reimbursement.configuration.HibernateConfiguration;
import ch.uzh.csg.reimbursement.configuration.LdapConfiguration;
import ch.uzh.csg.reimbursement.configuration.MailConfiguration;
import ch.uzh.csg.reimbursement.configuration.WebMvcConfiguration;
import ch.uzh.csg.reimbursement.configuration.WebSecurityConfiguration;
import ch.uzh.csg.reimbursement.model.Document;
import ch.uzh.csg.reimbursement.model.ExpenseItem;
import ch.uzh.csg.reimbursement.model.ExpenseState;
import ch.uzh.csg.reimbursement.repository.CostCategoryRepositoryProvider;
import ch.uzh.csg.reimbursement.repository.ExpenseItemRepositoryProvider;
import ch.uzh.csg.reimbursement.repository.ExpenseRepositoryProvider;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HibernateConfiguration.class, LdapConfiguration.class, MailConfiguration.class,
		WebMvcConfiguration.class, WebSecurityConfiguration.class })
@WebAppConfiguration

public class ExpenseResourceIT {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ExpenseRepositoryProvider expRepo;

	@Autowired
	private ExpenseItemRepositoryProvider expItemRepo;

	@Autowired
	private CostCategoryRepositoryProvider costCatRepo;

	private MockMvc mvc;
	private MockHttpSession session;
	private String juniorUid;
	private IntegrationTestHelper helper;
	private static ObjectMapper mapper;

	@BeforeClass
	public static void beforeClass(){
		mapper = new ObjectMapper();
	}

	@Before
	public void setup() throws Exception {
		helper = new IntegrationTestHelper();
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
		RequestBuilder requestBuilder = formLogin().user("junior").password("password");
		MvcResult loginResult = mvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
		session = (MockHttpSession) loginResult.getRequest().getSession();

		if(juniorUid == null || juniorUid.isEmpty() ){
			ObjectNode user = helper.getUser(mvc, session);
			juniorUid = user.get("uid").asText();
		}
	}

	@Test
	public void createExpenseTest() throws Exception{
		String accounting = "Create Expense Test";
		assertEquals( accounting,expRepo.findByUid(helper.createExpense(mvc, session, accounting)).getAccounting());
	}

	@Test
	public void createExpenseItem() throws Exception{
		String accounting = "Create Expense Item";
		String expenseUid = helper.createExpense(mvc, session, accounting);
		String jsonString = helper.generateInitialExpenseItemJsonString(mvc);
		String expenseItemUid = helper.createInitialExpenseItem(mvc, session, expenseUid,jsonString);

		ExpenseItem expItem = expItemRepo.findByUid(expenseItemUid);
		assertNotNull(expItem);

		boolean found = false;
		for(ExpenseItem eI :  expRepo.findByUid(expItem.getExpense().getUid()).getExpenseItems()){
			if(eI.getUid().equals(expItem.getUid())){
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

	@Test
	public void getCostCategoriesTest() throws Exception{
		assertEquals(costCatRepo.findAllActive().size(), helper.getCostCategory(mvc).length);
	}

	@Ignore
	//not working since there is the classpath: shortcut issues in the pdf service
	@Test
	public void uploadImageAttachmentTest() throws Exception{
		String uri = getClass().getResource("/img/uzh_card_new.png").getFile();
		File f = new File(uri);
		FileInputStream fi1 = new FileInputStream(f);

		MockMultipartFile fstmp = new MockMultipartFile("file", f.getName(), "image/jpeg", fi1);
		assertTrue(fstmp.getBytes().length > 0);

		String expenseUid = helper.createExpense(mvc, session, "Upload Image Attachment Test");
		String jsonString = helper.generateInitialExpenseItemJsonString(mvc);
		String expenseItemUid = helper.createInitialExpenseItem(mvc, session, expenseUid, jsonString );

		mvc.perform(fileUpload("/expenses/expense-items/"+expenseItemUid+"/attachments").file(fstmp).with(csrf().asHeader())).andDo(print())
		.andExpect(status().isUnauthorized());

		mvc.perform(fileUpload("/expenses/expense-items/"+expenseItemUid+"/attachments").file(fstmp).session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());


		assertNotNull(expItemRepo.findByUid(expenseItemUid).getAttachment());
		assertEquals(MIME_PDF, expItemRepo.findByUid(expenseItemUid).getAttachment().getContentType());
		assertTrue(expItemRepo.findByUid(expenseItemUid).getAttachment().getContent().length>0);

		Document attachment = mapper.readValue(mvc.perform(get("/expenses/expense-items/"+expenseItemUid+"/attachments").session(session)).andExpect(status().is2xxSuccessful()).andReturn().getResponse().getContentAsString(), Document.class);
		assertTrue(Arrays.equals(attachment.getContent(), expItemRepo.findByUid(expenseItemUid).getAttachment().getContent()));
	}

	@Test
	public void uploadPdfAttachmentTest() throws Exception{
		String jsonString = mapper.createObjectNode()
				.put("date",new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
				.put("costCategoryUid", helper.getCostCategory(mvc)[0].getUid())
				.put("currency", "CHF")
				.toString();

		String expenseUid = helper.createExpense(mvc, session, "Upload PDF Attachment Test");
		String expenseItemUid = helper.createInitialExpenseItem(mvc, session, expenseUid, jsonString );

		MockMultipartFile fstmp = helper.uploadPdfAttachment(mvc, expenseItemUid, session);

		assertNotNull(expItemRepo.findByUid(expenseItemUid).getAttachment());
		assertEquals(fstmp.getContentType(), expItemRepo.findByUid(expenseItemUid).getAttachment().getContentType());
		assertEquals(fstmp.getBytes().length, expItemRepo.findByUid(expenseItemUid).getAttachment().getContent().length);
		assertTrue(Arrays.equals(fstmp.getBytes(), expItemRepo.findByUid(expenseItemUid).getAttachment().getContent()));

		Document attachment = mapper.readValue(mvc.perform(get("/expenses/expense-items/"+expenseItemUid+"/attachments").session(session)).andExpect(status().is2xxSuccessful()).andReturn().getResponse().getContentAsString(), Document.class);
		assertTrue(Arrays.equals(attachment.getContent(), expItemRepo.findByUid(expenseItemUid).getAttachment().getContent()));
	}

	@Test
	public void updateExpenseItem() throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String dateString = sdf.format(date);
		long dateStringInMillis = sdf.parse(dateString).getTime();
		String costCat = helper.getCostCategory(mvc)[0].getUid();
		String currency = "CHF";

		String jsonString = mapper.createObjectNode()
				.put("date",dateString)
				.put("costCategoryUid", costCat)
				.put("currency", currency)
				.toString();

		String expenseUid = helper.createExpense(mvc, session, "Update Expense Item Test");
		System.out.println("expenseUID: "+expenseUid);
		String expenseItemUid = helper.createInitialExpenseItem(mvc, session, expenseUid, jsonString);
		System.out.println("expenseItemUid: "+expenseItemUid);

		double amount = 300;
		String project = "Test Project";
		String examplanation = "Test Explanation";
		jsonString = mapper.createObjectNode()
				.put("date",dateString)
				.put("costCategoryUid", costCat)
				.put("originalAmount", amount)
				.put("currency", currency)
				.put("project", project)
				.put("explanation", examplanation)
				.toString();

		mvc.perform(put("/expenses/expense-items/" + expenseItemUid).content(jsonString).contentType(MediaType.APPLICATION_JSON_VALUE).session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());

		String result = mvc.perform(get("/expenses/expense-items/"+ expenseItemUid).session(session)).andDo(print()).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		ObjectNode expenseItem = mapper.readValue(result, ObjectNode.class);
		assertEquals(amount, expenseItem.get("originalAmount").asDouble(), 0);
		assertEquals(project, expenseItem.get("project").asText());
		assertEquals(examplanation, expenseItem.get("explanation").asText());
		assertEquals(costCat, expenseItem.get("costCategory").get("uid").asText());
		assertEquals(currency, expenseItem.get("currency").asText());
		assertEquals(dateStringInMillis, expenseItem.get("date").asLong(), 9000000);

		ExpenseItem expItm = expItemRepo.findByUid(expenseItemUid);
		assertEquals(expItm.getOriginalAmount(), expenseItem.get("originalAmount").asDouble(), 0);
		assertEquals(expItm.getProject(), expenseItem.get("project").asText());
		assertEquals(expItm.getExplanation(), expenseItem.get("explanation").asText());
		assertEquals(costCat, expenseItem.get("costCategory").get("uid").asText());
		assertEquals(currency, expenseItem.get("currency").asText());
		assertEquals(dateStringInMillis, expenseItem.get("date").asLong(), 9000000);
	}

	@Test
	public void getAllExpenseItemsTest() throws Exception{
		String expenseUid = helper.createExpense(mvc, session, "Get All Expense Items");
		String jsonString = helper.generateInitialExpenseItemJsonString(mvc);
		String expenseItemUid1 = helper.createInitialExpenseItem(mvc, session, expenseUid, jsonString);
		String expenseItemUid2 = helper.createInitialExpenseItem(mvc, session, expenseUid, jsonString);

		mvc.perform(put("/expenses/expense-items/" + expenseItemUid1).content(helper.generateExtendedExpenseItemJsonString(mvc, "Item 1 Project","Item 1 Explanation" )).contentType(MediaType.APPLICATION_JSON_VALUE).session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());

		mvc.perform(put("/expenses/expense-items/" + expenseItemUid2).content(helper.generateExtendedExpenseItemJsonString(mvc, "Item 2 Project","Item 2 Explanation" )).contentType(MediaType.APPLICATION_JSON_VALUE).session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());

		//the call returns a 400 since the user is not logged in - and no token is submitted!
		mvc.perform(get("/expenses/"+expenseUid+"/expense-items")).andDo(print()).andExpect(status().is4xxClientError());

		String result = mvc.perform(get("/expenses/"+expenseUid+"/expense-items").session(session)).andExpect(status().isOk()).andReturn().getResponse()
				.getContentAsString();


		final JsonNode arrNode = new ObjectMapper().readTree(result);
		assertTrue(arrNode.isArray());
		assertEquals(2, arrNode.size());
		//depends on right order!
		assertEquals(expenseItemUid1, arrNode.at("/0/uid").asText());
		assertEquals(expenseItemUid2, arrNode.at("/1/uid").asText());
	}

	@Test
	public void getAllExpensesTest() throws Exception{
		String result = mvc.perform(get("/expenses").session(session)).andExpect(status().isOk()).andReturn().getResponse()
				.getContentAsString();
		int initialSize = new ObjectMapper().readTree(result).size();

		String expenseUid = helper.createExpense(mvc, session, "Get All Expense Items - Expense 1");
		String jsonString = helper.generateInitialExpenseItemJsonString(mvc);
		String expenseItemUid1 = helper.createInitialExpenseItem(mvc, session, expenseUid, jsonString);
		String expenseItemUid2 = helper.createInitialExpenseItem(mvc, session, expenseUid, jsonString);

		mvc.perform(put("/expenses/expense-items/" + expenseItemUid1).content(helper.generateExtendedExpenseItemJsonString(mvc, "Expense1 Item 1","Item 1 Explanation" )).contentType(MediaType.APPLICATION_JSON_VALUE).session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());

		mvc.perform(put("/expenses/expense-items/" + expenseItemUid2).content(helper.generateExtendedExpenseItemJsonString(mvc, "Expense1 Item 2","Item 2 Explanation" )).contentType(MediaType.APPLICATION_JSON_VALUE).session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());

		String expense2Uid = helper.createExpense(mvc, session, "Get All Expense Items - Expense 2");
		jsonString = helper.generateInitialExpenseItemJsonString(mvc);
		String expense2ItemUid1 = helper.createInitialExpenseItem(mvc, session, expense2Uid, jsonString);
		String expense2ItemUid2 = helper.createInitialExpenseItem(mvc, session, expense2Uid, jsonString);

		mvc.perform(put("/expenses/expense-items/" + expense2ItemUid1).content(helper.generateExtendedExpenseItemJsonString(mvc, "Expense2 Item 1","Item 1 Explanation" )).contentType(MediaType.APPLICATION_JSON_VALUE).session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());

		mvc.perform(put("/expenses/expense-items/" + expense2ItemUid2).content(helper.generateExtendedExpenseItemJsonString(mvc, "Expense2 Item 2","Item 2 Explanation" )).contentType(MediaType.APPLICATION_JSON_VALUE).session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());

		mvc.perform(get("/expenses")).andDo(print()).andExpect(status().isUnauthorized());

		result = mvc.perform(get("/expenses").session(session)).andExpect(status().isOk()).andReturn().getResponse()
				.getContentAsString();

		assertEquals(initialSize+2, new ObjectMapper().readTree(result).size());
		//TODO add some more sophisticated tests
	};

	@Test
	public void wholeSigningProcessTestJuniorToFadmin() throws Exception{
		//logged in as Junior
		String expenseUid = helper.createExpense(mvc, session, "e-Sign Junior: " + helper.getCurrentDayAndTime());
		String jsonString = helper.generateInitialExpenseItemJsonString(mvc);
		String expenseItemUid1 = helper.createInitialExpenseItem(mvc, session, expenseUid, jsonString);
		String expenseItemUid2 = helper.createInitialExpenseItem(mvc, session, expenseUid, jsonString);
		helper.uploadPdfAttachment(mvc, expenseItemUid1, session);
		helper.uploadPdfAttachment(mvc, expenseItemUid2, session);

		mvc.perform(put("/expenses/expense-items/" + expenseItemUid1).content(helper.generateExtendedExpenseItemJsonString(mvc, "SignExpenseTest Item 1","Item 1 Explanation" )).contentType(MediaType.APPLICATION_JSON_VALUE).session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());

		mvc.perform(put("/expenses/expense-items/" + expenseItemUid2).content(helper.generateExtendedExpenseItemJsonString(mvc, "SignExpenseTest Item 2","Item 2 Explanation" )).contentType(MediaType.APPLICATION_JSON_VALUE).session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());


		mvc.perform(put("/expenses/"+expenseUid+"/assign-to-manager").session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());

		//login as Prof
		session = helper.loginUser(mvc, "prof", "password");
		mvc.perform(put("/expenses/"+expenseUid+"/accept").session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());

		//login as Fadmin
		session = helper.loginUser(mvc, "fadmin", "password");
		mvc.perform(put("/expenses/"+expenseUid+"/assign-to-me").session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());

		mvc.perform(put("/expenses/"+expenseUid+"/accept").session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());

		//login as Junior
		session = helper.loginUser(mvc, "junior", "password");
		mvc.perform(put("/expenses/"+expenseUid+"/set-electronical-signature").session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());

		mvc.perform(post("/expenses/"+expenseUid+"/sign-electronically").session(session).with(csrf().asHeader()))
		.andDo(print()).andExpect(status().is2xxSuccessful());

		//login as Prof
		session = helper.loginUser(mvc, "prof", "password");
		mvc.perform(post("/expenses/"+expenseUid+"/sign-electronically").session(session).with(csrf().asHeader()))
		.andDo(print()).andExpect(status().is2xxSuccessful());

		//login as Fadmin
		session = helper.loginUser(mvc, "fadmin", "password");
		mvc.perform(post("/expenses/"+expenseUid+"/sign-electronically").session(session).with(csrf().asHeader()))
		.andDo(print()).andExpect(status().is2xxSuccessful());

		//Test state via GET
		String result = mvc.perform(get("/expenses/"+expenseUid).session(session)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		ObjectNode expense = mapper.readValue(result, ObjectNode.class);
		assertEquals(ExpenseState.SIGNED.name(),expense.findValue("state").asText());

		//Test state via RepoProvider in DB
		assertEquals(ExpenseState.SIGNED, expRepo.findByUid(expenseUid).getState());
	}

	@Test
	public void wholeSigningProcessTestProfToDepman() throws Exception {
		// logged in as Prof
		session = helper.loginUser(mvc, "prof", "password");
		String expenseUid = helper.createExpense(mvc, session, "e-Sign Prof.: " + helper.getCurrentDayAndTime());
		String jsonString = helper.generateInitialExpenseItemJsonString(mvc);
		String expenseItemUid1 = helper.createInitialExpenseItem(mvc, session, expenseUid, jsonString);
		String expenseItemUid2 = helper.createInitialExpenseItem(mvc, session, expenseUid, jsonString);
		helper.uploadPdfAttachment(mvc, expenseItemUid1, session);
		helper.uploadPdfAttachment(mvc, expenseItemUid2, session);

		mvc.perform(put("/expenses/expense-items/" + expenseItemUid1)
				.content(helper.generateExtendedExpenseItemJsonString(mvc, "SignTest Item 1",
						"Item 1 Explanation"))
				.contentType(MediaType.APPLICATION_JSON_VALUE).session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());

		mvc.perform(put("/expenses/expense-items/" + expenseItemUid2)
				.content(helper.generateExtendedExpenseItemJsonString(mvc, "SignTest Item 2",
						"Item 2 Explanation"))
				.contentType(MediaType.APPLICATION_JSON_VALUE).session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());

		mvc.perform(put("/expenses/" + expenseUid + "/assign-to-manager").session(session).with(csrf().asHeader()))
		.andDo(print()).andExpect(status().is2xxSuccessful());

		// login as Fadmin
		session = helper.loginUser(mvc, "fadmin", "password");
		mvc.perform(put("/expenses/" + expenseUid + "/assign-to-me").session(session).with(csrf().asHeader()))
		.andDo(print()).andExpect(status().is2xxSuccessful());

		mvc.perform(put("/expenses/" + expenseUid + "/accept").session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());

		// login as prof
		session = helper.loginUser(mvc, "prof", "password");
		mvc.perform(
				put("/expenses/" + expenseUid + "/set-electronical-signature").session(session).with(csrf().asHeader()))
		.andDo(print()).andExpect(status().is2xxSuccessful());

		mvc.perform(post("/expenses/" + expenseUid + "/sign-electronically").session(session).with(csrf().asHeader()))
		.andDo(print()).andExpect(status().is2xxSuccessful());

		// login as depman
		session = helper.loginUser(mvc, "depman", "password");
		mvc.perform(post("/expenses/" + expenseUid + "/sign-electronically").session(session).with(csrf().asHeader()))
		.andDo(print()).andExpect(status().is2xxSuccessful());

		// login as Fadmin
		session = helper.loginUser(mvc, "fadmin", "password");
		mvc.perform(post("/expenses/" + expenseUid + "/sign-electronically").session(session).with(csrf().asHeader()))
		.andDo(print()).andExpect(status().is2xxSuccessful());

		// Test state via GET
		String result = mvc.perform(get("/expenses/" + expenseUid).session(session)).andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		ObjectNode expense = mapper.readValue(result, ObjectNode.class);
		assertEquals(ExpenseState.SIGNED.name(), expense.findValue("state").asText());

		// Test state via RepoProvider in DB
		assertEquals(ExpenseState.SIGNED, expRepo.findByUid(expenseUid).getState());
	}

	@Test
	public void wholeSigningProcessTestFadminDepmanFadmin() throws Exception {
		// logged in as Prof
		session = helper.loginUser(mvc, "fadmin", "password");
		String expenseUid = helper.createExpense(mvc, session, "e-Sign Prof.: " + helper.getCurrentDayAndTime());
		String jsonString = helper.generateInitialExpenseItemJsonString(mvc);
		String expenseItemUid1 = helper.createInitialExpenseItem(mvc, session, expenseUid, jsonString);
		String expenseItemUid2 = helper.createInitialExpenseItem(mvc, session, expenseUid, jsonString);
		helper.uploadPdfAttachment(mvc, expenseItemUid1, session);
		helper.uploadPdfAttachment(mvc, expenseItemUid2, session);

		mvc.perform(put("/expenses/expense-items/" + expenseItemUid1)
				.content(helper.generateExtendedExpenseItemJsonString(mvc, "SignTest Item 1",
						"Item 1 Explanation"))
				.contentType(MediaType.APPLICATION_JSON_VALUE).session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());

		mvc.perform(put("/expenses/expense-items/" + expenseItemUid2)
				.content(helper.generateExtendedExpenseItemJsonString(mvc, "SignTest Item 2",
						"Item 2 Explanation"))
				.contentType(MediaType.APPLICATION_JSON_VALUE).session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());

		mvc.perform(put("/expenses/" + expenseUid + "/assign-to-manager").session(session).with(csrf().asHeader()))
		.andDo(print()).andExpect(status().is2xxSuccessful());

		// login as Fadmin2
		session = helper.loginUser(mvc, "fadmin2", "password");
		mvc.perform(put("/expenses/" + expenseUid + "/assign-to-me").session(session).with(csrf().asHeader()))
		.andDo(print()).andExpect(status().is2xxSuccessful());

		mvc.perform(put("/expenses/" + expenseUid + "/accept").session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());

		// login as prof
		session = helper.loginUser(mvc, "fadmin", "password");
		mvc.perform(
				put("/expenses/" + expenseUid + "/set-electronical-signature").session(session).with(csrf().asHeader()))
		.andDo(print()).andExpect(status().is2xxSuccessful());

		mvc.perform(post("/expenses/" + expenseUid + "/sign-electronically").session(session).with(csrf().asHeader()))
		.andDo(print()).andExpect(status().is2xxSuccessful());

		// login as depman
		session = helper.loginUser(mvc, "depman", "password");
		mvc.perform(post("/expenses/" + expenseUid + "/sign-electronically").session(session).with(csrf().asHeader()))
		.andDo(print()).andExpect(status().is2xxSuccessful());

		// login as Fadmin
		session = helper.loginUser(mvc, "fadmin2", "password");
		mvc.perform(post("/expenses/" + expenseUid + "/sign-electronically").session(session).with(csrf().asHeader()))
		.andDo(print()).andExpect(status().is2xxSuccessful());

		// Test state via GET
		String result = mvc.perform(get("/expenses/" + expenseUid).session(session)).andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		ObjectNode expense = mapper.readValue(result, ObjectNode.class);
		assertEquals(ExpenseState.SIGNED.name(), expense.findValue("state").asText());

		// Test state via RepoProvider in DB
		assertEquals(ExpenseState.SIGNED, expRepo.findByUid(expenseUid).getState());
	}
}
