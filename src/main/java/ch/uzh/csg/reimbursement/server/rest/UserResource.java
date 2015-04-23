package ch.uzh.csg.reimbursement.server.rest;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_GIF_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.server.dto.CroppingDto;
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

		userService.create(dto);
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
	@ApiOperation(value = "Remove a user", notes = "Removes the user with the specified uid.")
	public void removeUser(@PathVariable("uid") String uid) {

		userService.removeByUid(uid);
	}

	@RequestMapping(value = "/{uid}/first-name", method = PUT)
	@ResponseStatus(OK)
	@ApiOperation(value = "Update the first name of a user", notes = "Updates the first name of a given user.")
	public void updateFirstName(@PathVariable("uid") String uid, @RequestParam("firstName") String firstName) {

		userService.updateFirstName(uid, firstName);
	}

	@RequestMapping(value = "/{uid}/signature", method = POST)
	@ResponseStatus(OK)
	@ApiOperation(value = "Upload a new signature", notes = "Upload a new signature image")
	public void uploadSignature(@PathVariable("uid") String uid, @RequestParam("file") MultipartFile file) {

		userService.addSignature(uid, file);
	}

	@RequestMapping(value = "/{uid}/signature", method = GET, produces = { IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE, IMAGE_GIF_VALUE })
	@ApiOperation(value = "Retrieve the signature image", notes = "Returns the signature image.")
	public byte[] getSignature(@PathVariable("uid") String uid) {

		return userService.getSignature(uid);
	}

	@RequestMapping(value = "/{uid}/signature/crop", method = POST)
	@ResponseStatus(OK)
	@ApiOperation(value = "Crop the existing signature", notes = "Stores the cropping data into database.")
	public void uploadSignature(@PathVariable("uid") String uid, @RequestBody CroppingDto dto) {

		userService.addSignatureCropping(uid, dto);
	}

}