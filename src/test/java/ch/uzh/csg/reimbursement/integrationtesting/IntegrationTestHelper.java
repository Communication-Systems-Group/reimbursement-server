package ch.uzh.csg.reimbursement.integrationtesting;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

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

	public String createInitialExpenseItem(MockMvc mvc, MockHttpSession session, String accounting, String jsonString)
			throws Exception {
		String expenseUid = this.createExpense(mvc, session, accounting);
		String result = mvc.perform(post("/expenses/" + expenseUid + "/expense-items").content(jsonString)
				.contentType(MediaType.APPLICATION_JSON).session(session).with(csrf().asHeader()))
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
}
