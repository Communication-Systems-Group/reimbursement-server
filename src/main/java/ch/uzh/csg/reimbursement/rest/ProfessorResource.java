package ch.uzh.csg.reimbursement.rest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.service.ExpenseItemService;
import ch.uzh.csg.reimbursement.service.ExpenseService;
import ch.uzh.csg.reimbursement.service.UserService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/prof")
@PreAuthorize("hasRole('PROF')")
@Api(value = "Professor", description = "Authorized access for professors.")
public class ProfessorResource {

	// resource naming convention
	// http://www.restapitutorial.com/lessons/restfulresourcenaming.html

	@Autowired
	private ExpenseService expenseService;

	@Autowired
	private ExpenseItemService expenseItemService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/professors", method = GET)
	@ApiOperation(value = "Find all professors.")
	public List<User> getUserByRole() {

		return userService.getManagersWithoutMe();
	}
}