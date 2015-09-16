package ch.uzh.csg.reimbursement.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
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

import ch.uzh.csg.reimbursement.dto.CostCategoryDto;
import ch.uzh.csg.reimbursement.model.CostCategory;
import ch.uzh.csg.reimbursement.model.Role;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.service.CostCategoryService;
import ch.uzh.csg.reimbursement.service.UserService;
import ch.uzh.csg.reimbursement.view.View;

import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/finance-admin")
@PreAuthorize("hasRole('FINANCE_ADMIN')")
@Api(value = "Finance-Admin", description = "Authorized access required, only for administrators")
public class FinanceAdminResource {

	@Autowired
	private CostCategoryService costCategoryService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/users", method = GET)
	@ApiOperation(value = "Find all users", notes = "Finds all users which are currently in the system.")
	public List<User> getAllUsers() {
		return userService.findAll();
	}

	@RequestMapping(value = "/users/{user-uid}", method = GET)
	@ApiOperation(value = "Find one user with an uid.", notes = "Finds exactly one user by its uid.")
	public User findUserByUid(@PathVariable("user-uid") String uid) {
		return userService.findByUid(uid);
	}

	@RequestMapping(value = "/roles", method = GET)
	@ApiOperation(value = "Find all defined roles.", notes = "Finds alle defined roles.")
	public Role[] getRoles() {
		return userService.getRoles();
	}

	@JsonView(View.SummaryWithUid.class)
	@RequestMapping(value = "/cost-categories", method = POST)
	@ApiOperation(value = "Create a new costCategory.")
	@ResponseStatus(CREATED)
	public CostCategory createCostCategory(@RequestBody CostCategoryDto dto) {
		return costCategoryService.create(dto);
	}

	@RequestMapping(value = "/cost-categories/{cost-category-uid}", method = PUT)
	@ApiOperation(value = "Update the costCategory with the given uid.")
	public void updateCostCategory(@PathVariable ("cost-category-uid") String uid, @RequestBody CostCategoryDto dto) {
		costCategoryService.updateCostCategory(uid, dto);
	}

	@RequestMapping(value = "/cost-categories/{cost-category-uid}", method = DELETE)
	@ApiOperation(value = "Delete the costCategory with the given uid.")
	public void deleteCostCategory(@PathVariable ("cost-category-uid") String uid) {
		costCategoryService.deleteCostCategory(uid);
	}
}
