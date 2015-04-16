package ch.uzh.csg.reimbursement.server.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.csg.reimbursement.server.example.Message;
import ch.uzh.csg.reimbursement.server.example.Person;
import ch.uzh.csg.reimbursement.server.example.UppercaseService;
import ch.uzh.csg.reimbursement.server.model.User;
import ch.uzh.csg.reimbursement.server.service.UserService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/user")
@Api(value = "User", description = "In this resource, all user-specific actions are depicted.")
public class UserResource {

	// EXAMPLE
	@Autowired
	private UppercaseService uppercaseService;

	// EXAMPLE
	@RequestMapping(value = "/remove", method = DELETE)
	@ApiOperation(value = "Remove a user", notes = "EXAMPLE: Removes an existing user")
	public Message removeUser(@RequestBody Person person) {
		return new Message("System", person.getFirstName() + " " + person.getLastName() + " removed");
	}

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/{uid}", method = GET, produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Find a user with an uid", notes = "Find a user by its uid.")
	//@ResponseStatus(OK)
	public User findUserByUid(@RequestParam String uid) {
		return userService.findByUid(uid);
	}

}