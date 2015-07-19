package ch.uzh.csg.reimbursement.rest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.service.ExpenseService;
import ch.uzh.csg.reimbursement.service.UserService;
import ch.uzh.csg.reimbursement.view.ExpenseResourceView;

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
	private UserService userService;

	@Autowired
	private ExpenseService expenseService;

	@RequestMapping(value = "/users", method = GET)
	@ApiOperation(value = "Find all users", notes = "Finds all users which are currently in the system.")
	public List<User> getAllUsers() {
		return userService.findAll();
	}

	@RequestMapping(value = "/users/{user-uid}", method = GET)
	@ApiOperation(value = "Find one user with an uid", notes = "Finds exactly one user by its uid.")
	public User findUserByUid(@PathVariable("user-uid") String uid) {
		return userService.findByUid(uid);
	}

	@RequestMapping(value = "/expenses/user/{user-uid}", method = GET)
	@ApiOperation(value = "Find all expenses for a given user", notes = "Finds all expenses that were created by the user.")
	public ExpenseResourceView getAllExpenses(@PathVariable ("user-uid") String uid) {
		return expenseService.findAllByUser(uid);
	}
}