package ch.uzh.csg.reimbursement.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.csg.reimbursement.dto.ExpenseItemDto;
import ch.uzh.csg.reimbursement.service.ExpenseItemService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/expense/expenseItem")
@Api(value = "ExpenseItem", description = "ExpenseItem related actions")
public class ExpenseItemResource {

	@Autowired
	private ExpenseItemService expenseItemService;

	@RequestMapping(method = POST)
	@ApiOperation(value = "Create new expenseItem", notes = "Creates a new expenseItem when called with the correct arguments.")
	@ResponseStatus(CREATED)
	public void createExpenseItem(@RequestBody ExpenseItemDto dto) {
		expenseItemService.create(dto);
	}

	@RequestMapping(value = "/{uid}", method = PUT)
	@ApiOperation(value = "Update the expenseItem with the given uid", notes = "Updates the expenseItem with the given uid.")
	@ResponseStatus(OK)
	public void updateExpenseItem(@PathVariable("uid") String uid, @RequestBody ExpenseItemDto dto) {
		expenseItemService.updateExpenseItem(uid, dto);
	}
}