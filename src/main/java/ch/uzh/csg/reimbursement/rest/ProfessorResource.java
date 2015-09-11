package ch.uzh.csg.reimbursement.rest;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.csg.reimbursement.dto.ExpenseDto;
import ch.uzh.csg.reimbursement.dto.ExpenseItemDto;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseItem;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.service.ExpenseItemService;
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
	private ExpenseItemService expenseItemService;

	@Autowired
	private UserService userService;

	@JsonView(View.DashboardSummary.class)
	@RequestMapping(value = "/review-expenses", method = GET)
	@ApiOperation(value = "Find all review expenses for the currently logged in user.")
	public Set<Expense> getExpenses() {
		return expenseService.findAllByAssignedManager();
	}

	@JsonView(View.Summary.class)
	@RequestMapping(value = "/review-expenses/{expense-uid}", method = GET)
	@ApiOperation(value = "Find the expense with the given uid.")
	@ResponseStatus(OK)
	public Expense getReviewExpenseByUid(@PathVariable("expense-uid") String uid) {
		return expenseService.findByUid(uid);
	}

	@RequestMapping(value = "/review-expenses/{expense-uid}", method = PUT)
	@ApiOperation(value = "Update the expense with the given uid.")
	@ResponseStatus(OK)
	public void updateExpense(@PathVariable("expense-uid") String uid, @RequestBody ExpenseDto dto) {
		expenseService.updateExpense(uid, dto);
	}

	@RequestMapping(value = "/review-expenses/{expense-uid}/review-expense-items", method = GET)
	@ApiOperation(value = "Find all expense-items of an expense to review for the currently logged in user.")
	public Set<ExpenseItem> getAllReviewExpenseItems(@PathVariable ("expense-uid") String uid) {
		return expenseItemService.findAllExpenseItemsByExpenseUid(uid);
	}

	@RequestMapping(value = "/review-expenses/review-expense-items/{expense-item-uid}", method = GET)
	@ApiOperation(value = "Find expense-item with the given uid.")
	@ResponseStatus(OK)
	public ExpenseItem getReviewExpenseItem(@PathVariable("expense-item-uid") String uid) {
		return expenseItemService.findByUid(uid);
	}

	@RequestMapping(value = "/review-expenses/review-expense-items/{expense-item-uid}", method = PUT)
	@ApiOperation(value = "Update the expense-item with the given uid.")
	@ResponseStatus(OK)
	public void updateReviewExpenseItem(@PathVariable("expense-item-uid") String uid, @RequestBody ExpenseItemDto dto) {
		expenseItemService.updateExpenseItem(uid, dto);
	}

	@RequestMapping(value = "/professors", method = GET)
	@ApiOperation(value = "Find all professors.")
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