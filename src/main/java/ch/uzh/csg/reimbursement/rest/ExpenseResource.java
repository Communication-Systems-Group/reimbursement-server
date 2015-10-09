package ch.uzh.csg.reimbursement.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.dto.AccessRights;
import ch.uzh.csg.reimbursement.dto.ExpenseItemDto;
import ch.uzh.csg.reimbursement.dto.ExpenseStateStatisticsDto;
import ch.uzh.csg.reimbursement.dto.SearchExpenseDto;
import ch.uzh.csg.reimbursement.model.Document;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseItem;
import ch.uzh.csg.reimbursement.model.Token;
import ch.uzh.csg.reimbursement.service.ExpenseItemService;
import ch.uzh.csg.reimbursement.service.ExpenseService;
import ch.uzh.csg.reimbursement.service.TokenService;
import ch.uzh.csg.reimbursement.view.View;
import ch.uzh.csg.reimbursement.view.View.DashboardSummary;
import ch.uzh.csg.reimbursement.view.View.Summary;
import ch.uzh.csg.reimbursement.view.View.SummaryWithUid;

import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/expenses")
@Api(value = "Expense", description = "Authorization depends on state.")
public class ExpenseResource {

	@Autowired
	private ExpenseService expenseService;

	@Autowired
	private ExpenseItemService expenseItemService;

	@Autowired
	private TokenService tokenService;

	@PreAuthorize("hasRole('USER')")
	@JsonView(SummaryWithUid.class)
	@RequestMapping(method = POST)
	@ApiOperation(value = "Creates a new expense for currently logged in user")
	@ResponseStatus(CREATED)
	public Expense createExpense(@RequestParam("accounting") String accounting) {

		return expenseService.createExpense(accounting);
	}

	@PreAuthorize("hasRole('USER')")
	@JsonView(DashboardSummary.class)
	@RequestMapping(method = GET)
	@ApiOperation(value = "Find all expenses for the currently logged in user")
	public Set<Expense> getExpenses() {

		return expenseService.getAllByCurrentUser();
	}

	@JsonView(Summary.class)
	@RequestMapping(value = "/{expense-uid}", method = GET)
	@ApiOperation(value = "Find expense by uid")
	@ResponseStatus(OK)
	public Expense getExpenseByUid(@PathVariable("expense-uid") String uid) {

		return expenseService.getByUid(uid);
	}

	@PreAuthorize("hasRole('USER')")
	@RequestMapping(value = "/{expense-uid}", method = PUT)
	@ApiOperation(value = "Update the expense with the given uid.")
	@ResponseStatus(OK)
	public void updateExpense(@PathVariable("expense-uid") String uid, @RequestParam("accounting") String accounting) {

		expenseService.updateExpense(uid, accounting);
	}

	@PreAuthorize("hasRole('USER')")
	@RequestMapping(value = "/{expense-uid}", method = DELETE)
	@ApiOperation(value = "Delete the expense with the given uid", notes = "Delete the expense with the given uid.")
	@ResponseStatus(OK)
	public void deleteExpense(@PathVariable("expense-uid") String uid) {

		expenseService.deleteExpense(uid);
	}

	@PreAuthorize("hasAnyRole('PROF', 'FINANCE_ADMIN')")
	@RequestMapping(value = "/{expense-uid}/accept", method = PUT)
	@ApiOperation(value = "Accept the expense with the given uid.")
	@ResponseStatus(OK)
	public void acceptExpense(@PathVariable("expense-uid") String uid) {

		expenseService.acceptExpense(uid);
	}

	@PreAuthorize("hasRole('USER')")
	@RequestMapping(value = "/{expense-uid}/assign-to-prof", method = PUT)
	@ApiOperation(value = "Assign the expense with the given uid to the manager.")
	@ResponseStatus(OK)
	public void assignExpenseToProf(@PathVariable("expense-uid") String uid) {

		expenseService.assignExpenseToProf(uid);
	}

	@PreAuthorize("hasRole('FINANCE_ADMIN')")
	@RequestMapping(value = "/{expense-uid}/assign-to-me", method = PUT)
	@ApiOperation(value = "Assign the expense to the logged in user.")
	@ResponseStatus(OK)
	public void assignExpenseToMe(@PathVariable("expense-uid") String uid) {

		expenseService.assignExpenseToMe(uid);
	}

	@PreAuthorize("hasAnyRole('PROF', 'FINANCE_ADMIN')")
	@RequestMapping(value = "/{expense-uid}/reject", method = PUT)
	@ApiOperation(value = "Decline the expense with the given.")
	@ResponseStatus(OK)
	public void rejectExpense(@PathVariable("expense-uid") String uid, @RequestParam("comment") String comment) {

		expenseService.rejectExpense(uid, comment);
	}

	@RequestMapping(value = "/{expense-uid}/access-rights", method = GET)
	@ApiOperation(value = "Update the expense with the given uid.")
	@ResponseStatus(OK)
	public AccessRights getPermission(@PathVariable("expense-uid") String uid) {

		return expenseService.getAccessRights(uid);
	}

	@RequestMapping(value = "/{expense-uid}/expense-items", method = GET)
	@ApiOperation(value = "Find all expense-items of an expense for the currently logged in user")
	public Set<ExpenseItem> getAllExpenseItems(@PathVariable("expense-uid") String uid) {

		return expenseItemService.getExpenseItemsByExpenseUid(uid);
	}

	@PreAuthorize("hasRole('USER')")
	@JsonView(SummaryWithUid.class)
	@RequestMapping(value = "/{expense-uid}/expense-items", method = POST)
	@ApiOperation(value = "Create new expenseItem", notes = "Creates a new expenseItem for the specified expense. yyyy-MM-dd'T'HH:mm:ss.SSSZ, yyyy-MM-dd'T'HH:mm:ss.SSS'Z', EEE, dd MMM yyyy HH:mm:ss zzz, yyyy-MM-dd<br><br>{  \"date\": \"2015-06-06\",  \"costCategoryUid\": \"a353602d-50d0-4007-b134-7fdb42f23542\",  \"explanation\": \"blub\",  \"currency\": \"CHF\",  \"originalAmount\": 200,  \"project\": \"Testing of chuncks\"}")
	@ResponseStatus(CREATED)
	public ExpenseItem createExpenseItem(@PathVariable("expense-uid") String uid, @RequestBody ExpenseItemDto dto) {

		return expenseItemService.createExpenseItem(uid, dto);
	}

	@RequestMapping(value = "/expense-items/{expense-item-uid}", method = GET)
	@ApiOperation(value = "Get the expenseItem with the given uid", notes = "Gets the expenseItem with the given uid.")
	public ExpenseItem getExpenseItem(@PathVariable("expense-item-uid") String uid) {

		return expenseItemService.getByUid(uid);
	}

	@PreAuthorize("hasRole('USER')")
	@RequestMapping(value = "/expense-items/{expense-item-uid}", method = PUT)
	@ApiOperation(value = "Update the expenseItem with the given uid", notes = "Updates the expenseItem with the given uid.")
	@ResponseStatus(OK)
	public void updateExpenseItem(@PathVariable("expense-item-uid") String uid, @RequestBody ExpenseItemDto dto) {

		expenseItemService.updateExpenseItem(uid, dto);
	}

	@PreAuthorize("hasRole('USER')")
	@RequestMapping(value = "/expense-items/{expense-item-uid}", method = DELETE)
	@ApiOperation(value = "Delete the expenseItem with the given uid", notes = "Delete the expenseItem with the given uid.")
	@ResponseStatus(OK)
	public void deleteExpenseItem(@PathVariable("expense-item-uid") String uid) {

		expenseItemService.deleteExpenseItem(uid);
	}

	@PreAuthorize("hasRole('USER')")
	@RequestMapping(value = "/expense-items/{expense-item-uid}/attachments", method = GET)
	@ApiOperation(value = "Get a certain expenseItemAttachment", notes = "")
	@ResponseStatus(OK)
	public Document getExpenseItemAttachment(@PathVariable("expense-item-uid") String uid) {

		return expenseItemService.getAttachment(uid);
	}

	@PreAuthorize("hasRole('USER')")
	@JsonView(SummaryWithUid.class)
	@RequestMapping(value = "/expense-items/{expense-item-uid}/attachments", method = POST)
	@ApiOperation(value = "Upload a new expenseItemAttachment", notes = "")
	@ResponseStatus(CREATED)
	public Document uploadExpenseItemAttachment(@PathVariable("expense-item-uid") String uid,
			@RequestParam("file") MultipartFile file) {

		return expenseItemService.setAttachment(uid, file);
	}

	@PreAuthorize("hasRole('USER')")
	@RequestMapping(value = "/expense-items/{expense-item-uid}/attachments/token", method = POST)
	@ApiOperation(value = "Create a new expenseItemAttachment token for mobile access")
	public Token createExpenseItemAttachmentMobileToken(@PathVariable("expense-item-uid") String uid) {

		return tokenService.createExpenseItemAttachmentMobileToken(uid);
		// TODO The attachmnet service does sometimes not include the content -
		// occurs only at first popup open...
	}

	@PreAuthorize("hasAnyRole('PROF', 'FINANCE_ADMIN')")
	@JsonView(View.DashboardSummary.class)
	@RequestMapping(value = "/review-expenses", method = GET)
	@ApiOperation(value = "Find all review expenses for the currently logged in user.")
	public Set<Expense> getReviewExpenses() {

		return expenseService.getAllReviewExpenses();
	}

	@PreAuthorize("hasRole('USER')")
	@JsonView(SummaryWithUid.class)
	@RequestMapping(value = "/{expense-uid}/upload-pdf", method = POST)
	@ApiOperation(value = "Upload a PDF for the expense with the given expense-uid", notes = "")
	@ResponseStatus(CREATED)
	public Document uploadPdf(@PathVariable("expense-uid") String uid,
			@RequestParam("file") MultipartFile file) {

		return expenseService.setSignedPdf(uid, file);
	}

	@PreAuthorize("hasRole('USER')")
	@RequestMapping(value = "/{expense-uid}/export-pdf", method = POST)
	@ApiOperation(value = "Export a PDF for the expense with the given expense-uid", notes = "")
	@ResponseStatus(CREATED)
	public Document exportPdf(@PathVariable("expense-uid") String uid) {

		return expenseService.getPdf(uid);
	}

	@PreAuthorize("hasRole('USER')")
	@RequestMapping(value = "/{expense-uid}/generate-pdf", method = POST)
	@ApiOperation(value = "Export a PDF for the expense with the given expense-uid", notes = "")
	@ResponseStatus(CREATED)
	public void generatePdf(@PathVariable("expense-uid") String uid, @RequestParam("url") String url) {

		expenseService.generatePdf(uid, url);
	}

	@PreAuthorize("hasRole('FINANCE_ADMIN')")
	@RequestMapping(value = "/search", method = POST)
	@ApiOperation(value = "Find all expenses according to the defined search criteria.", notes = "Finds all expenses according to the defined search criteria.")
	public Set<Expense> searchExpenses(@RequestBody SearchExpenseDto dto) {

		return expenseService.search(dto);
	}

	@PreAuthorize("hasRole('USER')")
	@RequestMapping(value = "/user/{user-uid}", method = GET)
	@ApiOperation(value = "Find all expenses for a given user.", notes = "Finds all expenses that were created by the user.")
	public Set<Expense> getAllExpenses(@PathVariable("user-uid") String uid) {

		return expenseService.getAllByUser(uid);
	}

	@PreAuthorize("hasRole('FINANCE_ADMIN')")
	@RequestMapping(value = "/statistics/states", method = GET)
	@ApiOperation(value = "Find the info in which states the expenses are")
	public ExpenseStateStatisticsDto getExpenseStateStatistics() {

		return expenseService.getExpenseStateStatistics();
	}

}
