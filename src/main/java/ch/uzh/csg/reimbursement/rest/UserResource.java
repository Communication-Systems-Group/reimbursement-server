package ch.uzh.csg.reimbursement.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.Base64;
import java.util.Base64.Encoder;
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

import ch.uzh.csg.reimbursement.dto.CroppingDto;
import ch.uzh.csg.reimbursement.dto.ExpenseDto;
import ch.uzh.csg.reimbursement.dto.ExpenseItemDto;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseItem;
import ch.uzh.csg.reimbursement.model.Token;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.service.ExpenseItemService;
import ch.uzh.csg.reimbursement.service.ExpenseService;
import ch.uzh.csg.reimbursement.service.UserService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('USER')")
@Api(value = "User", description = "Authorized access required.")
public class UserResource {

	@Autowired
	private UserService userService;

	@Autowired
	private ExpenseService expenseService;

	@Autowired
	private ExpenseItemService expenseItemService;

	@RequestMapping(method = GET)
	@ApiOperation(value = "Returns the current logged in user")
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

	@RequestMapping(value = "/expense", method = POST)
	@ApiOperation(value = "Create new expense")
	@ResponseStatus(CREATED)
	public void createExpense(@RequestBody ExpenseDto dto) {

		expenseService.create(dto);
	}

	@RequestMapping(value = "/expenses", method = GET)
	@ApiOperation(value = "Find all expenses for the current user")
	public Set<Expense> getAllExpenses(@PathVariable ("uid") String uid) {

		return expenseService.findAllByCurrentUser();
	}

	@RequestMapping(value = "/expense/{uid}", method = PUT)
	@ApiOperation(value = "Update the expense with the given uid")
	@ResponseStatus(OK)
	public void updateExpense(@PathVariable("uid") String uid, @RequestBody ExpenseDto dto) {

		expenseService.updateExpense(uid, dto);
	}

	//TODO dave move expense uid to url (pathvariable)
	@RequestMapping(value = "/expense/expense-item", method = POST)
	@ApiOperation(value = "Create new expenseItem", notes = "Creates a new expenseItem when called with the correct arguments.")
	@ResponseStatus(CREATED)
	public void createExpenseItem(@RequestBody ExpenseItemDto dto) {

		expenseItemService.create(dto);
	}

	@RequestMapping(value = "/expense/{uid}/expense-items", method = GET)
	@ApiOperation(value = "Find all expense-items of an expense for a given user")
	public Set<ExpenseItem> getAllExpenseItems(@PathVariable ("uid") String uid) {

		return expenseService.findAllExpenseItemsByUid(uid);

	}

	@RequestMapping(value = "/expense/expense-item/{uid}", method = PUT)
	@ApiOperation(value = "Update the expenseItem with the given uid", notes = "Updates the expenseItem with the given uid.")
	@ResponseStatus(OK)
	public void updateExpenseItem(@PathVariable("uid") String uid, @RequestBody ExpenseItemDto dto) {

		expenseItemService.updateExpenseItem(uid, dto);
	}
}
