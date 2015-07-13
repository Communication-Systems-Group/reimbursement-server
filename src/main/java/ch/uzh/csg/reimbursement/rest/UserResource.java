package ch.uzh.csg.reimbursement.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.dto.CommentDto;
import ch.uzh.csg.reimbursement.dto.CroppingDto;
import ch.uzh.csg.reimbursement.dto.ExpenseDto;
import ch.uzh.csg.reimbursement.dto.ExpenseItemDto;
import ch.uzh.csg.reimbursement.model.Account;
import ch.uzh.csg.reimbursement.model.CostCategory;
import ch.uzh.csg.reimbursement.model.ExpenseItem;
import ch.uzh.csg.reimbursement.model.Token;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.service.AccountService;
import ch.uzh.csg.reimbursement.service.CommentService;
import ch.uzh.csg.reimbursement.service.CostCategoryService;
import ch.uzh.csg.reimbursement.service.ExpenseItemService;
import ch.uzh.csg.reimbursement.service.ExpenseService;
import ch.uzh.csg.reimbursement.service.UserService;
import ch.uzh.csg.reimbursement.view.ExpenseDetailedView;
import ch.uzh.csg.reimbursement.view.ExpenseMapper;
import ch.uzh.csg.reimbursement.view.ExpenseResourceView;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('USER')")
@Api(value = "User", description = "Authorized access required.")
public class UserResource {

	// resource naming convention
	// http://www.restapitutorial.com/lessons/restfulresourcenaming.html

	@Autowired
	private UserService userService;

	@Autowired
	private ExpenseService expenseService;

	@Autowired
	private ExpenseItemService expenseItemService;

	@Autowired
	private CostCategoryService costCategoryService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private ExpenseMapper expenseMapper;

	@RequestMapping(method = GET)
	@ApiOperation(value = "Returns the currently logged in user")
	public User getLoggedInUser(){

		return userService.getLoggedInUser();
	}

	@RequestMapping(value = "/signature", method = POST)
	@ApiOperation(value = "Upload a new signature")
	public void addSignature(@RequestParam("file") MultipartFile file) {

		userService.addSignature(file);
	}

	@RequestMapping(value = "/signature", method = GET)
	@ApiOperation(value = "Retrieve the signature image")
	public String getSignature(HttpServletResponse response){
		Encoder encoder = Base64.getEncoder();
		String base64String = encoder.encodeToString(userService.getSignature());
		return base64String;
	}

	@RequestMapping(value = "/signature/crop", method = POST)
	@ApiOperation(value = "Crop the existing signature", notes = "Stores the cropping data and cropped image into the database.")
	public void uploadSignature(@RequestBody CroppingDto dto) {

		userService.addSignatureCropping(dto);
	}

	@RequestMapping(value = "/signature/token", method = POST)
	@ApiOperation(value = "Create a new signature token for mobile access")
	public Token createSignatureMobileToken() {

		return userService.createSignatureMobileToken();
	}

	@RequestMapping(value = "/expenses", method = POST)
	@ApiOperation(value = "Creates a new expense for currently logged in user")
	@ResponseStatus(CREATED)
	public String createExpense(@RequestBody ExpenseDto dto) {
		return "{\n  \"expenseUid\": \""+expenseService.create(dto)+"\"\n}";
	}

	@RequestMapping(value = "/expenses", method = GET)
	@ApiOperation(value = "Find all expenses for the currently logged in user")
	public ExpenseResourceView getAllExpenses() {

		return expenseService.findAllByCurrentUser();
	}

	@RequestMapping(value = "/expenses/{expense-uid}", method = GET)
	@ApiOperation(value = "Find expense by uid")
	@ResponseStatus(OK)
	public ExpenseDetailedView getExpenseByUid(@PathVariable("expense-uid") String uid) {
		return expenseMapper.mapExpenseDetailedView(expenseService.findByUid(uid));
	}

	@RequestMapping(value = "/expenses/{expense-uid}/comments", method = POST)
	@ApiOperation(value = "Create a new comment", notes = "")
	@ResponseStatus(CREATED)
	public String createExpenseComment(@PathVariable ("expense-uid") String uid,@RequestBody CommentDto dto) {
		return "{\n  \"expenseCommentUid\": \""+commentService.createExpenseComment(uid, dto)+"\"\n}";
	}

	@RequestMapping(value = "/expenses/{expense-uid}", method = PUT)
	@ApiOperation(value = "Update the expense with the given uid. Use this method to assign an expense to a manager")
	@ResponseStatus(OK)
	public void updateExpense(@PathVariable("expense-uid") String uid, @RequestBody ExpenseDto dto) {
		expenseService.updateExpense(uid, dto);
	}

	@RequestMapping(value = "/expenses/{expense-uid}/expense-items", method = POST)
	@ApiOperation(value = "Create new expenseItem", notes = "Creates a new expenseItem for the specified expense.")
	@ResponseStatus(CREATED)
	public String createExpenseItem(@PathVariable("expense-uid") String uid, @RequestBody ExpenseItemDto dto) {
		return "{\n  \"expenseItemUid\": \""+expenseItemService.create(uid, dto)+"\"\n}";
	}

	@RequestMapping(value = "/expenses/{expense-uid}/expense-items", method = GET)
	@ApiOperation(value = "Find all expense-items of an expense for the currently logged in user")
	public Set<ExpenseItem> getAllExpenseItems(@PathVariable ("expense-uid") String uid) {
		return expenseItemService.findAllExpenseItemsByExpenseUid(uid);

	}

	@RequestMapping(value = "/expenses/expense-item/{expense-item-uid}", method = PUT)
	@ApiOperation(value = "Update the expenseItem with the given uid", notes = "Updates the expenseItem with the given uid.")
	@ResponseStatus(OK)
	public void updateExpenseItem(@PathVariable("expense-item-uid") String uid, @RequestBody ExpenseItemDto dto) {
		expenseItemService.updateExpenseItem(uid, dto);
	}

	@RequestMapping(value = "/expenses/expense-items/{expense-item-uid}/attachments", method = POST)
	@ApiOperation(value = "Upload a new expenseItemAttachment", notes = "")
	@ResponseStatus(CREATED)
	public String uploadExpenseItemAttachment(@PathVariable ("expense-item-uid") String uid,@RequestParam("file") MultipartFile file ) {
		return "{\n  \"expenseItemAttachmentUid\": \""+expenseItemService.setAttachment(uid, file)+"\"\n}";
	}

	@RequestMapping(value = "/expenses/expense-items/{expense-item-uid}/comments", method = POST)
	@ApiOperation(value = "Create a new comment", notes = "")
	@ResponseStatus(CREATED)
	public String createExpenseItemComment(@PathVariable ("expense-item-uid") String uid,@RequestBody CommentDto dto) {
		return "{\n  \"expenseItemCommentUid\": \""+commentService.createExpenseItemComment(uid, dto)+"\"\n}";
	}

	@RequestMapping(value = "/expenses/expense-items/{expense-item-uid}/attachments", method = GET)
	@ApiOperation(value = "Get a certain expenseItemAttachment", notes = "")
	@ResponseStatus(OK)
	public String getExpenseItemAttachment(@PathVariable ("expense-item-uid") String uid ) {
		// TODO move to a service
		Encoder encoder = Base64.getEncoder();
		String base64String = encoder.encodeToString(expenseItemService.getExpenseItemAttachment(uid));
		return base64String;
	}


	@RequestMapping(value = "/expenses/expense-items/{expense-item-uid}/attachments/token", method = POST)
	@ApiOperation(value = "Create a new expenseItemAttachment token for mobile access")
	public Token createExpenseItemAttachmentMobileToken(@PathVariable ("expense-item-uid") String uid) {
		return expenseItemService.createExpenseItemAttachmentMobileToken(uid);
	}

	@RequestMapping(value = "/costCategories", method = GET)
	@ApiOperation(value = "Find all costCategories", notes = "Finds all costCategories which are currently in the system.")
	public List<CostCategory> getAllCostCategories() {

		return costCategoryService.findAll();
	}

	@RequestMapping(value = "/accounts", method = GET)
	@ApiOperation(value = "Find all accounts", notes = "Finds all accounts which are currently in the system.")
	public List<Account> getAllAccounts() {

		return accountService.findAll();
	}
}
