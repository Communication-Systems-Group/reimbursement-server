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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.uzh.csg.reimbursement.configuration.HibernateConfiguration;
import ch.uzh.csg.reimbursement.configuration.LdapConfiguration;
import ch.uzh.csg.reimbursement.configuration.MailConfiguration;
import ch.uzh.csg.reimbursement.configuration.WebMvcConfiguration;
import ch.uzh.csg.reimbursement.configuration.WebSecurityConfiguration;
import ch.uzh.csg.reimbursement.model.Document;
import ch.uzh.csg.reimbursement.model.ExpenseItem;
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
		String costCategoryUid = helper.getCostCategory(mvc)[0].getUid();
		String jsonString = mapper.createObjectNode()
				.put("date",new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
				.put("costCategoryUid", costCategoryUid)
				.put("currency", "CHF")
				.toString();

		String expenseItemUid = helper.createInitialExpenseItem(mvc, session, accounting,jsonString);

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

	@Test
	public void uploadImageAttachmentTest() throws Exception{
		String uri = getClass().getResource("/img/uzh_card_new.png").getFile();
		File f = new File(uri);
		FileInputStream fi1 = new FileInputStream(f);

		MockMultipartFile fstmp = new MockMultipartFile("file", f.getName(), "image/jpeg", fi1);
		assertTrue(fstmp.getBytes().length > 0);

		String jsonString = mapper.createObjectNode()
				.put("date",new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
				.put("costCategoryUid", helper.getCostCategory(mvc)[0].getUid())
				.put("currency", "CHF")
				.toString();

		String expenseItemUid = helper.createInitialExpenseItem(mvc, session, "Upload Image Attachment Test", jsonString );

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
		File f = new File("C:\\Users\\Christian\\Downloads\\Success_Story_Haufe-umantis_DE_140805.pdf");
		FileInputStream fi1 = new FileInputStream(f);

		MockMultipartFile fstmp = new MockMultipartFile("file", f.getName(),MIME_PDF , fi1);
		assertTrue(fstmp.getBytes().length > 0);

		String jsonString = mapper.createObjectNode()
				.put("date",new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
				.put("costCategoryUid", helper.getCostCategory(mvc)[0].getUid())
				.put("currency", "CHF")
				.toString();

		String expenseItemUid = helper.createInitialExpenseItem(mvc, session, "Upload PDF Attachment Test", jsonString );

		mvc.perform(fileUpload("/expenses/expense-items/"+expenseItemUid+"/attachments").file(fstmp).with(csrf().asHeader())).andDo(print())
		.andExpect(status().isUnauthorized());

		mvc.perform(fileUpload("/expenses/expense-items/"+expenseItemUid+"/attachments").file(fstmp).session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());


		assertNotNull(expItemRepo.findByUid(expenseItemUid).getAttachment());
		assertEquals(fstmp.getContentType(), expItemRepo.findByUid(expenseItemUid).getAttachment().getContentType());
		assertEquals(fstmp.getBytes().length, expItemRepo.findByUid(expenseItemUid).getAttachment().getContent().length);
		assertTrue(Arrays.equals(fstmp.getBytes(), expItemRepo.findByUid(expenseItemUid).getAttachment().getContent()));

		Document attachment = mapper.readValue(mvc.perform(get("/expenses/expense-items/"+expenseItemUid+"/attachments").session(session)).andExpect(status().is2xxSuccessful()).andReturn().getResponse().getContentAsString(), Document.class);
		assertTrue(Arrays.equals(attachment.getContent(), expItemRepo.findByUid(expenseItemUid).getAttachment().getContent()));
	}


	@Test
	public void updateExpenseItem() throws Exception{
		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String costCat = helper.getCostCategory(mvc)[0].getUid();
		String currency = "CHF";

		String jsonString = mapper.createObjectNode()
				.put("date",date)
				.put("costCategoryUid", costCat)
				.put("currency", currency)
				.toString();

		String expenseItemUid = helper.createInitialExpenseItem(mvc, session, "Update Expense Item Test", jsonString);

		String amount = "300";
		String project = "Test Project";
		String examplanation = "Test Explanation";
		jsonString = mapper.createObjectNode()
				.put("date",date)
				.put("costCategoryUid", costCat)
				.put("originalAmount", amount)
				.put("currency", currency)
				.put("project", project)
				.put("explanation", examplanation)
				.toString();

		mvc.perform(put("/expenses/expense-items/" + expenseItemUid).content(jsonString).contentType(MediaType.APPLICATION_JSON_VALUE).session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());

		String result = mvc.perform(get("/expenses/expense-items/"+ expenseItemUid)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		ObjectNode expenseItem = mapper.readValue(result, ObjectNode.class);
		assertEquals(amount, expenseItem.get("originalAmount").asText());
		assertEquals(project, expenseItem.get("project").asText());
		assertEquals(examplanation, expenseItem.get("explanation").asText());
		assertEquals(costCat, expenseItem.get("costCategoryUid").asText());
		assertEquals(currency, expenseItem.get("currency").asText());
		assertEquals(date, expenseItem.get("date").asText());

		ExpenseItem expItm = expItemRepo.findByUid(expenseItemUid);
		assertEquals(expItm.getOriginalAmount(), expenseItem.get("originalAmount").asText());
		assertEquals(expItm.getProject(), expenseItem.get("project").asText());
		assertEquals(expItm.getExplanation(), expenseItem.get("explanation").asText());
		assertEquals(costCat, expenseItem.get("costCategoryUid").asText());
		assertEquals(currency, expenseItem.get("currency").asText());
		assertEquals(date, expenseItem.get("date").asText());
	}

	// GET http://localhost/api/expenses/db69346b-9322-453f-b8cb-02be8e4f943a/expense-items
	//[{"uid":"a967552b-4708-4c5a-896e-a6a33a466667","expense":"db69346b-9322-453f-b8cb-02be8e4f943a","date":1449360000000,"state":"SUCCESFULLY_CREATED","originalAmount":300.0,"calculatedAmount":300.0,"costCategory":{"uid":"a353602d-50d0-4007-b134-7fdb42f23542","accountNumber":322000,"name":{"de":"Reisekosten Mitarbeitende","en":"Travel expense employees"},"description":{"de":"Kosten für Reisen im Rahmen der universitären Tätigkeit zb. Fahrkosten, Flugkosten, Bahnkosten, Taxi, Reisetickets Übernachtungen, Hotel, Verpflegungskosten auswärts SBB, ESTA","en":"Costs for travel within the university activity eg. travel expenses, airfare, public transportation, taxi, travel, hotel, food expenses, ESTA"},"accountingPolicy":{"de":"ACHTUNG: - Reisespesen von Dritte auf das Konto 322040 verbuchen\n- Gipfeli und Sandwich für Sitzungen im Büro auf das Konto 306900 buchen\n- Teilnahmegebühren für Kongresse auf das Konto 306020 buchen","en":"CAUTION: - Travel expenses of third parties need to be booked on 322040\n\n- Book croissants and sandwich for meetings in the office on the account 306900\n- Book attendance fees for congresses to the account 306020"},"isActive":true},"explanation":"sfgsfsggffsgsg","currency":"CHF","project":null}]

	//GET http://localhost/api/expenses/
	// [{"uid":"db69346b-9322-453f-b8cb-02be8e4f943a","user":"Jnr. Bus Fahrer","date":1449356400000,"state":"DRAFT","accounting":"Hello","totalAmount":300.0,"userUid":"junior","financeAdminUid":null,"assignedManagerUid":null}]
}
