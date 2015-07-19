package ch.uzh.csg.reimbursement.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.csg.reimbursement.dto.AccountDto;
import ch.uzh.csg.reimbursement.dto.CostCategoryDto;
import ch.uzh.csg.reimbursement.model.Account;
import ch.uzh.csg.reimbursement.model.CostCategory;
import ch.uzh.csg.reimbursement.service.AccountService;
import ch.uzh.csg.reimbursement.service.CostCategoryService;
import ch.uzh.csg.reimbursement.view.View;

import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@Api(value = "Professor", description = "Authorized access required, only for administrators")
public class AdminResource {

	@Autowired
	private AccountService accountService;

	@Autowired
	private CostCategoryService costCategoryService;

	@JsonView(View.SummaryWithUid.class)
	@RequestMapping(value = "/costCategories", method = POST)
	@ApiOperation(value = "Create a new costCategory")
	@ResponseStatus(CREATED)
	public CostCategory createCostCategory(@RequestBody CostCategoryDto dto) {
		return costCategoryService.create(dto);
	}

	@RequestMapping(value = "/costCategories/{cost-category-uid}", method = PUT)
	@ApiOperation(value = "Update a the costCategory with the given uid")
	public void updateCostCategory(@PathVariable ("cost-category-uid") String uid, @RequestBody CostCategoryDto dto) {
		costCategoryService.updateCostCategory(uid, dto);
	}

	@RequestMapping(value = "/costCategories/{cost-category-uid}", method = DELETE)
	@ApiOperation(value = "Delete the costCategory with the given uid")
	public void deleteCostCategory(@PathVariable ("cost-category-uid") String uid) {
		costCategoryService.deleteCostCategory(uid);
	}
	@JsonView(View.SummaryWithUid.class)
	@RequestMapping(value = "/accounts", method = POST)
	@ApiOperation(value = "Create a new account")
	@ResponseStatus(CREATED)
	public Account createAccount(@RequestBody AccountDto dto) {
		return accountService.create(dto);
	}

	@RequestMapping(value = "/accounts/{account-uid}", method = DELETE)
	@ApiOperation(value = "Delete the account with the given uid")
	public void deleteAccount(@PathVariable("account-uid") String uid) {
		accountService.delete(uid);
	}
}
