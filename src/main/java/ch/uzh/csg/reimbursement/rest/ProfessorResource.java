package ch.uzh.csg.reimbursement.rest;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.service.ExpenseService;
import ch.uzh.csg.reimbursement.service.UserService;
import ch.uzh.csg.reimbursement.view.View;

import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/prof")
@PreAuthorize("hasRole('PROF')")
@Api(value = "Professor", description = "Authorized access required, only for professors")
public class ProfessorResource {

	// resource naming convention
	// http://www.restapitutorial.com/lessons/restfulresourcenaming.html

	@Autowired
	private ExpenseService expenseService;

	@Autowired
	private UserService userService;

	@JsonView(View.DashboardSummary.class)
	@RequestMapping(value = "/review-expenses", method = GET)
	@ApiOperation(value = "Find all review expenses for the currently logged in user")
	public Set<Expense> getExpenses() {

		return expenseService.findAllByAssignedManager();
	}

	@RequestMapping(value = "/professors", method = GET)
	@ApiOperation(value = "Find all professors")
	public List<User> getUserByRole() {
		return userService.getManagersWithoutMe();
	}

	@RequestMapping(value = "/expenses/{expense-uid}/assign-to-finance-admin", method = PUT)
	@ApiOperation(value = "Assign the expense with the given uid to the finance admin.")
	@ResponseStatus(OK)
	public void assignExpenseToProf(@PathVariable("expense-uid") String uid) {
		expenseService.assignExpenseToFinanceAdmin(uid);
	}
}