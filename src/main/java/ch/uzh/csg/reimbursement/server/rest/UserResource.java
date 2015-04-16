package ch.uzh.csg.reimbursement.server.rest;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.csg.reimbursement.server.example.Message;
import ch.uzh.csg.reimbursement.server.example.Person;
import ch.uzh.csg.reimbursement.server.example.UppercaseService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/user")
@Api(value = "User", description = "In this resource, all user-specific actions are depicted.")
public class UserResource {

	@Autowired
	private UppercaseService uppercaseService;

	@RequestMapping(value = "/create", method = POST, produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Create a new user", notes = "Creates a new user")
	public Message createUser(@RequestBody Person person) {
		return new Message("System", person.getFirstName() + " " + person.getLastName() + " created");
	}

	@RequestMapping(value = "/remove", method = DELETE)
	@ApiOperation(value = "Remove a user", notes = "Removes an existing user")
	@ResponseStatus(OK)
	public void removeUser(@RequestBody Person person) {
		System.out.println(person.getFirstName() + " " + person.getLastName() + " removed");
	}

}