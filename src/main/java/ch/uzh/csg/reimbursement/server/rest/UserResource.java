package ch.uzh.csg.reimbursement.server.rest;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.csg.reimbursement.server.dto.UserDto;
import ch.uzh.csg.reimbursement.server.model.User;
import ch.uzh.csg.reimbursement.server.service.UserService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/user")
@Api(value = "User", description = "User related actions")
public class UserResource {

	@Autowired
	private UserService userService;

	@RequestMapping(method = POST)
	@ApiOperation(value = "Create new user", notes = "Creates a new user when provided with the necessary arguments.")
	@ResponseStatus(OK)
	public void createUser(@RequestBody UserDto dto) {

		User user = new User(dto.getFirstName(), dto.getLastName());
		userService.create(user);
	}

	@RequestMapping(method = GET)
	@ApiOperation(value = "Find all users", notes = "Finds all users which are currently in the system.")
	@ResponseStatus(OK)
	public List<User> getAllUsers() {

		return userService.findAll();
	}

	@RequestMapping(value = "/{uid}", method = GET)
	@ApiOperation(value = "Find one user with an uid", notes = "Finds exactly one user by its uid.")
	public User findUserByUid(@PathVariable("uid") String uid) {

		return userService.findByUid(uid);
	}

	@RequestMapping(value = "/{uid}", method = DELETE)
	@ApiOperation(value = "Remove a user", notes = "Removes the user with the specified uid")
	public void removeUser(@PathVariable("uid") String uid) {

		User user = userService.findByUid(uid);
		userService.delete(user);

	}

}