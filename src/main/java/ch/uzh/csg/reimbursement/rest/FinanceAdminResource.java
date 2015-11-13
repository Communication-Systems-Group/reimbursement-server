package ch.uzh.csg.reimbursement.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.csg.reimbursement.application.validation.ValidationService;
import ch.uzh.csg.reimbursement.dto.CostCategoryDto;
import ch.uzh.csg.reimbursement.model.CostCategory;
import ch.uzh.csg.reimbursement.model.Role;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.model.exception.ValidationException;
import ch.uzh.csg.reimbursement.service.CostCategoryService;
import ch.uzh.csg.reimbursement.service.UserService;
import ch.uzh.csg.reimbursement.view.View.SummaryWithUid;

import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/finance-admin")
@PreAuthorize("hasRole('FINANCE_ADMIN')")
@Api(value = "Finance-Admin", description = "Authorized access for finance admins.")
public class FinanceAdminResource {

	@Autowired
	private CostCategoryService costCategoryService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private ValidationService validationService;

	@RequestMapping(value = "/users", method = GET)
	@ApiOperation(value = "Find all users", notes = "Finds all users which are currently in the system.")
	public List<User> getAllUsers() {

		return userService.getAll();
	}

	@RequestMapping(value = "/users/{user-uid}", method = GET)
	@ApiOperation(value = "Find one user with an uid.", notes = "Finds exactly one user by its uid.")
	public User findUserByUid(@PathVariable("user-uid") String uid) {

		return userService.getByUid(uid);
	}

	@RequestMapping(value = "/roles", method = GET)
	@ApiOperation(value = "Find all defined roles.", notes = "Finds alle defined roles.")
	public Role[] getRoles() {

		return userService.getRoles();
	}

	@RequestMapping(value = "/cost-categories", method = GET)
	@ApiOperation(value = "Find all cost-categories", notes = "Finds all cost-categories which are currently in the system.")
	public List<CostCategory> getAllCostCategories() {

		return costCategoryService.getAll();
	}

	@JsonView(SummaryWithUid.class)
	@RequestMapping(value = "/cost-categories", method = POST)
	@ApiOperation(value = "Create a new costCategory.")
	@ResponseStatus(CREATED)
	public CostCategory createCostCategory(@RequestBody CostCategoryDto dto) {

		String keyDescription = "admin.costCategories.description";
		String keyName = "admin.costCategories.name";
		String keyNumber = "admin.costCategories.number";
		if(this.validationService.matches(keyDescription, dto.getDescription().getDe()) &&
				this.validationService.matches(keyDescription, dto.getDescription().getEn()) &&
				this.validationService.matches(keyName, dto.getName().getDe()) &&
				this.validationService.matches(keyName, dto.getName().getEn()) &&
				this.validationService.matches(keyNumber, Integer.toString(dto.getAccountNumber()))) {
			return costCategoryService.createCostCategory(dto);
		} else {
			throw new ValidationException(keyDescription + " | " + keyName + " | " + keyNumber);
		}
	}

	@RequestMapping(value = "/cost-categories/{cost-category-uid}", method = PUT)
	@ApiOperation(value = "Update the costCategory with the given uid.")
	@ResponseStatus(OK)
	public void updateCostCategory(@PathVariable("cost-category-uid") String uid, @RequestBody CostCategoryDto dto) {

		costCategoryService.updateCostCategory(uid, dto);
	}

	@RequestMapping(value = "/cost-categories/{cost-category-uid}/deactivate", method = PUT)
	@ApiOperation(value = "Deactivate the costCategory with the given uid.")
	@ResponseStatus(OK)
	public void deactivateCostCategory(@PathVariable("cost-category-uid") String uid) {

		costCategoryService.deactivateCostCategory(uid);
	}

	@RequestMapping(value = "/cost-categories/{cost-category-uid}/activate", method = PUT)
	@ApiOperation(value = "Activate the costCategory with the given uid.")
	@ResponseStatus(OK)
	public void activateCostCategory(@PathVariable("cost-category-uid") String uid) {

		costCategoryService.activateCostCategory(uid);
	}
}
