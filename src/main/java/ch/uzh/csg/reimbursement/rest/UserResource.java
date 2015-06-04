package ch.uzh.csg.reimbursement.rest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.dto.CroppingDto;
import ch.uzh.csg.reimbursement.model.Token;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.service.UserService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/user")
@Api(value = "User", description = "User related actions")
public class UserResource {

	@Autowired
	private UserService userService;

	@RequestMapping(method = GET)
	@ApiOperation(value = "Find all users", notes = "Finds all users which are currently in the system.")
	public List<User> getAllUsers() {

		return userService.findAll();
	}

	@RequestMapping(value = "/current", method = GET)
	@ApiOperation(value = "Is the User Authenticated?", notes = "Return username or 401")
	public User getLoggedInUserObject(){

		return userService.getLoggedInUserObject();
	}

	@RequestMapping(value = "/{uid}", method = GET)
	@ApiOperation(value = "Find one user with an uid", notes = "Finds exactly one user by its uid.")
	public User findUserByUid(@PathVariable("uid") String uid) {

		return userService.findByUid(uid);
	}

	@RequestMapping(value = "/{uid}/signature", method = POST)
	@ApiOperation(value = "Upload a new signature", notes = "Upload a new signature image")
	public void uploadSignature(@PathVariable("uid") String uid, @RequestParam("file") MultipartFile file) {

		userService.addSignature(uid, file);
	}

	@RequestMapping(value = "/{uid}/signature", method = GET)
	@ApiOperation(value = "Retrieve the signature image", notes = "Returns the signature image.")
	public String getSignature(@PathVariable("uid") String uid, HttpServletResponse response){
		Encoder encoder = Base64.getEncoder();
		String base64String = encoder.encodeToString(userService.getSignature(uid));
		return base64String;
	}

	@RequestMapping(value = "/{uid}/signature/crop", method = POST)
	@ApiOperation(value = "Crop the existing signature", notes = "Stores the cropping data into database.")
	public void uploadSignature(@PathVariable("uid") String uid, @RequestBody CroppingDto dto) {

		userService.addSignatureCropping(uid, dto);
	}

	@RequestMapping(value = "/signature/token", method = POST)
	@ApiOperation(value = "Create a new signature token for mobile access")
	public Token createSignatureMobileToken() {

		return userService.createSignatureMobileToken();
	}
}
