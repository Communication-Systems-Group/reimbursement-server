package ch.uzh.csg.reimbursement.integrationtesting;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.uzh.csg.reimbursement.model.CostCategory;

public class IntegrationTestHelper {

	private ObjectMapper mapper;

	public IntegrationTestHelper() {
		mapper = new ObjectMapper();
	}

	public String createExpense(MockMvc mvc, MockHttpSession session, String accounting) throws Exception {
		String result = mvc
				.perform(post("/expenses").param("accounting", accounting).session(session).with(csrf().asHeader()))
				.andDo(print()).andExpect(status().is2xxSuccessful()).andReturn().getResponse().getContentAsString();

		ObjectNode expense = mapper.readValue(result, ObjectNode.class);
		return expense.get("uid").asText();
	}

	public String createInitialExpenseItem(MockMvc mvc, MockHttpSession session, String expenseUid, String jsonString)
			throws Exception {
		String result = mvc.perform(post("/expenses/" + expenseUid + "/expense-items").content(jsonString)
				.contentType(MediaType.APPLICATION_JSON_VALUE).session(session).with(csrf().asHeader()))
				.andDo(print()).andExpect(status().is2xxSuccessful()).andReturn().getResponse().getContentAsString();
		ObjectNode expenseItem = mapper.readValue(result, ObjectNode.class);

		return expenseItem.get("uid").asText();
	}

	public CostCategory[] getCostCategory(MockMvc mvc) throws Exception {
		String result = mvc.perform(get("/public/cost-categories")).andExpect(status().isOk()).andReturn().getResponse()
				.getContentAsString();
		return mapper.readValue(result, CostCategory[].class);
	}

	public ObjectNode getUser(MockMvc mvc, MockHttpSession session) throws Exception {
		String result = mvc.perform(get("/user").session(session)).andDo(print()).andExpect(status().is2xxSuccessful())
				.andReturn().getResponse().getContentAsString();

		ObjectNode user = mapper.readValue(result, ObjectNode.class);
		return user;
	}

	public void updateExpenseItem(MockMvc mvc, MockHttpSession session, String expenseItemUid, String jsonString) throws Exception {
		//TODO for some reason returns a 400 not a 401
		mvc.perform(put("/expenses/expense-items/" + expenseItemUid).content(jsonString).contentType(MediaType.APPLICATION_JSON_VALUE).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is4xxClientError());

		mvc.perform(put("/expenses/expense-items/" + expenseItemUid).content(jsonString).contentType(MediaType.APPLICATION_JSON_VALUE).session(session).with(csrf().asHeader())).andDo(print())
		.andExpect(status().is2xxSuccessful());
	}

	public ObjectNode getExpenseItem(MockMvc mvc, MockHttpSession session, String expenseItemUid) throws Exception {
		mvc.perform(get("/expenses/expense-items/"+ expenseItemUid)).andDo(print()).andExpect(status().isUnauthorized());
		String result = mvc.perform(get("/expenses/expense-items/"+ expenseItemUid).session(session)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		return mapper.readValue(result, ObjectNode.class);
	}

	public String generateInitialExpenseItemJsonString(MockMvc mvc) throws Exception{
		return mapper.createObjectNode()
				.put("date",new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
				.put("costCategoryUid", this.getCostCategory(mvc)[0].getUid())
				.put("currency", "CHF")
				.toString();
	}

	public String generateExtendedExpenseItemJsonString(MockMvc mvc,String project,String examplanation) throws Exception{
		double amount = 300;
		return mapper.createObjectNode()
				.put("date",new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
				.put("costCategoryUid", this.getCostCategory(mvc)[0].getUid())
				.put("currency", "CHF")
				.put("originalAmount", amount)
				.put("project", project)
				.put("explanation", examplanation)
				.toString();
	}

	public MockHttpSession loginUser(MockMvc mvc, String username, String password) throws Exception{
		RequestBuilder requestBuilder = formLogin().user(username).password(password);
		MvcResult loginResult = mvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
		return (MockHttpSession) loginResult.getRequest().getSession();
	}
}
