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

import ch.uzh.csg.reimbursement.dto.ExpenseItemDto;
import ch.uzh.csg.reimbursement.dto.ExpenseStateStatisticsDto;
import ch.uzh.csg.reimbursement.dto.SearchExpenseDto;
import ch.uzh.csg.reimbursement.model.Document;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseItem;
import ch.uzh.csg.reimbursement.model.ExpenseState;
import ch.uzh.csg.reimbursement.model.Token;
import ch.uzh.csg.reimbursement.service.ExpenseItemService;
import ch.uzh.csg.reimbursement.service.ExpenseService;
import ch.uzh.csg.reimbursement.service.PdfGenerationService;
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
@Api(value = "Expense", description = "Authorization depends on state")
public class ExpenseResource {

	@Autowired
	private ExpenseService expenseService;

	@Autowired
	private ExpenseItemService expenseItemService;

	@Autowired
	private PdfGenerationService pdfGenerationService;

	@Autowired
	private TokenService tokenService;

	@PreAuthorize("hasRole('REGISTERED_USER')")
	@JsonView(SummaryWithUid.class)
	@RequestMapping(method = POST)
	@ApiOperation(value = "Create expense", notes = "Creates a new expense for the currently logged in user.")
	@ResponseStatus(CREATED)
	public Expense createExpense(@RequestParam("accounting") String accounting) {

		return expenseService.createExpense(accounting);
	}

	@PreAuthorize("hasRole('REGISTERED_USER')")
	@JsonView(DashboardSummary.class)
	@RequestMapping(method = GET)
	@ApiOperation(value = "Get expenses", notes = "Returns all expenses for the currently logged in user.")
	public Set<Expense> getExpenses() {

		return expenseService.getAllByCurrentUser();
	}

	@JsonView(DashboardSummary.class)
	@PreAuthorize("hasRole('REGISTERED_USER')")
	@RequestMapping(value = "/archive", method = GET)
	@ApiOperation(value = "Get archived expenses", notes = "Returns all archived expenses for the currently logged in user.")
	public Set<Expense> getArchive() {

		return expenseService.getArchive();
	}

	@PreAuthorize("hasRole('FINANCE_ADMIN')")
	@RequestMapping(value = "/expense-states", method = GET)
	@ApiOperation(value = "Get expense states", notes = "Returns a list of all defined expense states. \n Authorization for finance admins.")
	public ExpenseState[] getAllExpenseStates() {

		return expenseService.getExpenseStates();
	}

	@JsonView(Summary.class)
	@RequestMapping(value = "/{expense-uid}", method = GET)
	@ApiOperation(value = "Get expense", notes = "Returns the whole expense with the given uid.")
	public Expense getExpenseByUid(@PathVariable("expense-uid") String uid) {

		return expenseService.getByUid(uid);
	}

	@PreAuthorize("hasRole('REGISTERED_USER')")
	@RequestMapping(value = "/{expense-uid}", method = PUT)
	@ApiOperation(value = "Update expense", notes = "Updates the expense with the given uid.")
	@ResponseStatus(OK)
	public void updateExpense(@PathVariable("expense-uid") String uid, @RequestParam("accounting") String accounting) {

		expenseService.updateExpense(uid, accounting);
	}

	@PreAuthorize("hasRole('REGISTERED_USER')")
	@RequestMapping(value = "/{expense-uid}", method = DELETE)
	@ApiOperation(value = "Delete expense", notes = "Deletes the expense with the given uid.")
	@ResponseStatus(OK)
	public void deleteExpense(@PathVariable("expense-uid") String uid) {

		expenseService.deleteExpense(uid);
	}

	@PreAuthorize("hasAnyRole('PROF', 'FINANCE_ADMIN', 'HEAD_OF_INSTITUTE')")
	@RequestMapping(value = "/{expense-uid}/accept", method = PUT)
	@ApiOperation(value = "Accept expense", notes = "Accepts the expense with the given uid. \n Authorization for professors, finance admins and head of institute.")
	@ResponseStatus(OK)
	public void acceptExpense(@PathVariable("expense-uid") String uid) {

		expenseService.acceptExpense(uid);
	}

	@PreAuthorize("hasRole('REGISTERED_USER')")
	@RequestMapping(value = "/{expense-uid}/assign-to-manager", method = PUT)
	@ApiOperation(value = "Assign expense to manager", notes = "Assigns the expense with the given uid to the manager.")
	@ResponseStatus(OK)
	public void assignExpenseToManager(@PathVariable("expense-uid") String uid) {

		expenseService.assignExpenseToManager(uid);
	}

	@PreAuthorize("hasRole('FINANCE_ADMIN')")
	@RequestMapping(value = "/{expense-uid}/assign-to-me", method = PUT)
	@ApiOperation(value = "Assign expense to logged in user", notes = "Assigns the expense with the given uid to the logged in user. \n Authorization for finance admins.")
	@ResponseStatus(OK)
	public void assignExpenseToMe(@PathVariable("expense-uid") String uid) {

		expenseService.assignExpenseToMe(uid);
	}

	@PreAuthorize("hasAnyRole('PROF', 'FINANCE_ADMIN', 'HEAD_OF_INSTITUTE')")
	@RequestMapping(value = "/{expense-uid}/reject", method = PUT)
	@ApiOperation(value = "Reject expense", notes = "Rejects the expense with the given uid. \n Authorization for professors, finance admins and head of institute.")
	@ResponseStatus(OK)
	public void rejectExpense(@PathVariable("expense-uid") String uid, @RequestParam("comment") String comment) {

		expenseService.rejectExpense(uid, comment);
	}

	@RequestMapping(value = "/{expense-uid}/expense-items", method = GET)
	@ApiOperation(value = "Get expense-items", notes = "Returns all expense-items of the expense with the given uid for the currently logged in user.")
	public Set<ExpenseItem> getAllExpenseItems(@PathVariable("expense-uid") String uid) {

		return expenseItemService.getExpenseItemsByExpenseUid(uid);
	}

	@PreAuthorize("hasRole('REGISTERED_USER')")
	@JsonView(SummaryWithUid.class)
	@RequestMapping(value = "/{expense-uid}/expense-items", method = POST)
	@ApiOperation(value = "Create expense-item", notes = "Creates a new expense-item for the expense with the given uid. \n Testing data: \n{  \"date\": \"2015-06-06\",  \"costCategoryUid\": \"a353602d-50d0-4007-b134-7fdb42f23542\",  \"explanation\": \"Test explanation\",  \"currency\": \"CHF\",  \"originalAmount\": 200,  \"project\": \"Test project\"}")
	@ResponseStatus(CREATED)
	public ExpenseItem createExpenseItem(@PathVariable("expense-uid") String uid, @RequestBody ExpenseItemDto dto) {

		return expenseItemService.createExpenseItem(uid, dto);
	}

	@RequestMapping(value = "/expense-items/{expense-item-uid}", method = GET)
	@ApiOperation(value = "Get expense-item", notes = "Returns the expenseItem with the given uid.")
	public ExpenseItem getExpenseItem(@PathVariable("expense-item-uid") String uid) {

		return expenseItemService.getByUid(uid);
	}

	@PreAuthorize("hasRole('REGISTERED_USER')")
	@RequestMapping(value = "/expense-items/{expense-item-uid}", method = PUT)
	@ApiOperation(value = "Update expense-item", notes = "Updates the expenseItem with the given uid.")
	@ResponseStatus(OK)
	public void updateExpenseItem(@PathVariable("expense-item-uid") String uid, @RequestBody ExpenseItemDto dto) {

		expenseItemService.updateExpenseItem(uid, dto);
	}

	@PreAuthorize("hasRole('REGISTERED_USER')")
	@RequestMapping(value = "/expense-items/{expense-item-uid}", method = DELETE)
	@ApiOperation(value = "Delete expense-item", notes = "Deletes the expense-item with the given uid.")
	@ResponseStatus(OK)
	public void deleteExpenseItem(@PathVariable("expense-item-uid") String uid) {

		expenseItemService.deleteExpenseItem(uid);
	}

	@PreAuthorize("hasRole('REGISTERED_USER')")
	@RequestMapping(value = "/expense-items/{expense-item-uid}/attachments", method = GET)
	@ApiOperation(value = "Get attachment", notes = "Returns the attachment of the expense-item with the given uid.")
	public Document getExpenseItemAttachment(@PathVariable("expense-item-uid") String uid) {

		return expenseItemService.getAttachment(uid);
	}

	@PreAuthorize("hasRole('REGISTERED_USER')")
	@JsonView(SummaryWithUid.class)
	@RequestMapping(value = "/expense-items/{expense-item-uid}/attachments", method = POST)
	@ApiOperation(value = "Upload attachment", notes = "Upload an attachment for the expense-item with the given uid.")
	@ResponseStatus(CREATED)
	public Document uploadExpenseItemAttachment(@PathVariable("expense-item-uid") String uid,
			@RequestParam("file") MultipartFile file) {

		return expenseItemService.setAttachment(uid, file);
	}

	@PreAuthorize("hasRole('REGISTERED_USER')")
	@RequestMapping(value = "/expense-items/{expense-item-uid}/attachments", method = DELETE)
	@ApiOperation(value = "Delete attachment", notes = "Deletes the existing attachment of the expense-item with the given uid.")
	@ResponseStatus(OK)
	public void deleteExpenseItemAttachment(@PathVariable("expense-item-uid") String uid) {

		expenseItemService.deleteAttachment(uid);
	}

	@PreAuthorize("hasRole('REGISTERED_USER')")
	@RequestMapping(value = "/expense-items/{expense-item-uid}/attachments/token", method = POST)
	@ApiOperation(value = "Create token for mobile access", notes = "Creates a token for mobile access to upload an attachment for the expense-item with the given uid.")
	public Token createExpenseItemAttachmentMobileToken(@PathVariable("expense-item-uid") String uid) {

		return tokenService.createExpenseItemAttachmentMobileToken(uid);
		// TODO The attachmnet service does sometimes not include the content -
		// occurs only at first popup open...
	}

	@PreAuthorize("hasAnyRole('PROF', 'FINANCE_ADMIN', 'DEPARTMENT_MANAGER', 'HEAD_OF_INSTITUTE')")
	@JsonView(View.DashboardSummary.class)
	@RequestMapping(value = "/review-expenses", method = GET)
	@ApiOperation(value = "Get review expenses", notes = "Gets all review expenses for the currently logged in user. \n Authorization for professors, finance admins, department manager and head of institute.")
	public Set<Expense> getReviewExpenses() {

		return expenseService.getAllReviewExpenses();
	}

	@PreAuthorize("hasRole('REGISTERED_USER')")
	@JsonView(SummaryWithUid.class)
	@RequestMapping(value = "/{expense-uid}/upload-pdf", method = POST)
	@ApiOperation(value = "Upload PDF", notes = "Uploads a PDF for the expense with the given uid.")
	@ResponseStatus(CREATED)
	public Document uploadPdf(@PathVariable("expense-uid") String uid, @RequestParam("file") MultipartFile file) {

		return expenseService.setSignedPdf(uid, file);
	}

	@PreAuthorize("hasRole('REGISTERED_USER')")
	@JsonView(SummaryWithUid.class)
	@RequestMapping(value = "/{expense-uid}/sign-electronically", method = POST)
	@ApiOperation(value = "Sign PDF electronically", notes = "Signs the PDF electronically for the expense with the given uid.")
	@ResponseStatus(OK)
	public void signElectronically(@PathVariable("expense-uid") String uid) {

		expenseService.signElectronically(uid);
	}

	@RequestMapping(value = "/{expense-uid}/export-pdf", method = GET)
	@ApiOperation(value = "Export PDF", notes = "Exports a PDF for the expense with the given uid.")
	public Document exportPdf(@PathVariable("expense-uid") String uid) {

		return expenseService.getPdf(uid);
	}

	@PreAuthorize("hasRole('REGISTERED_USER')")
	@RequestMapping(value = "/{expense-uid}/generate-pdf", method = POST)
	@ApiOperation(value = "Generate PDF", notes = "Generates a PDF for the expense with the given uid.")
	@ResponseStatus(CREATED)
	public void generatePdf(@PathVariable("expense-uid") String uid, @RequestParam("url") String url) {

		pdfGenerationService.generateExpensePdf(uid, url);
	}

	@PreAuthorize("hasRole('FINANCE_ADMIN')")
	@RequestMapping(value = "/search", method = POST)
	@ApiOperation(value = "Search for expenses", notes = "Returns all expenses according to the defined search criteria. \n Authorization for finance admins.")
	public Set<Expense> searchExpenses(@RequestBody SearchExpenseDto dto) {

		return expenseService.search(dto);
	}

	@PreAuthorize("hasRole('REGISTERED_USER')")
	@RequestMapping(value = "/user/{user-uid}", method = GET)
	@ApiOperation(value = "Get expenses for a given user.", notes = "Returns all expenses that were created by the given user.")
	public Set<Expense> getAllExpenses(@PathVariable("user-uid") String uid) {

		return expenseService.getAllByUser(uid);
	}

	@PreAuthorize("hasRole('FINANCE_ADMIN')")
	@RequestMapping(value = "/statistics/states", method = GET)
	@ApiOperation(value = "Get statistics", notes = "Gets statistics about all expenses. \n Authorization for finance admins.")
	public ExpenseStateStatisticsDto getExpenseStateStatistics() {

		return expenseService.getExpenseStateStatistics();
	}

	@PreAuthorize("hasRole('REGISTERED_USER')")
	@RequestMapping(value = "/{expense-uid}/digital-signature", method = PUT)
	@ApiOperation(value = "Set sign method", notes = "Sets if the expense should be signed digitally or electronically.")
	@ResponseStatus(OK)
	public void setHasDigitalSignature(@PathVariable("expense-uid") String uid,
			@RequestParam("hasDigitalSignature") Boolean hasDigitalSignature) {

		expenseService.setHasDigitalSignature(uid, hasDigitalSignature);
	}
}
