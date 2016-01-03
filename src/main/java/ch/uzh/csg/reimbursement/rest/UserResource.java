package ch.uzh.csg.reimbursement.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import ch.uzh.csg.reimbursement.dto.CroppingDto;
import ch.uzh.csg.reimbursement.model.Language;
import ch.uzh.csg.reimbursement.model.Signature;
import ch.uzh.csg.reimbursement.model.Token;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.service.UserService;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('USER')")
@Api(value = "User", description = "Authorized access for all users")
public class UserResource {

	// resource naming convention
	// http://www.restapitutorial.com/lessons/restfulresourcenaming.html

	@Autowired
	private UserService userService;

	@RequestMapping(method = GET)
	@ApiOperation(value = "Get logged in user", notes = "Returns the currently logged in user.")
	public User getLoggedInUser() {

		return userService.getLoggedInUser();
	}

	@PreAuthorize("hasRole('REGISTERED_USER')")
	@RequestMapping(value = "/emergency-email", method = POST)
	@ApiOperation(value = "Emergency Email", notes = "Provoke the sending of an emergency email by producing an NullpointerException")
	public void sendEmergencyEmail() {
		throw new NullPointerException();
	}

	@RequestMapping(value = "/signature", method = POST)
	@ApiOperation(value = "Upload new signature", notes = "Allowed file types are PNG, JPEG and GIF.")
	@ResponseStatus(CREATED)
	public void addSignature(@RequestParam("file") MultipartFile file) {

		userService.addSignature(file);
	}

	@RequestMapping(value = "/signature", method = GET)
	@ApiOperation(value = "Retrieve signature image", notes = "")
	public Signature getSignature(HttpServletResponse response) {

		return userService.getSignature();
	}

	@RequestMapping(value = "/settings/language", method = PUT)
	@ApiOperation(value = "Update language settings", notes = "Updates the logged in user's language settings.")
	@ResponseStatus(OK)
	public void updateSettingsLanguage(@RequestParam Language language) {

		userService.updateLanguage(language);
	}

	@RequestMapping(value = "/settings/personnel-number", method = PUT)
	@ApiOperation(value = "Update personnel number", notes = "Updates the logged in user's personnel number.")
	@ResponseStatus(OK)
	public void updateSettingsPersonnelNumber(@RequestParam("personnelNumber") String personnelNumber) {

		userService.updatePersonnelNumber(personnelNumber);
	}

	@RequestMapping(value = "/settings/phone-number", method = PUT)
	@ApiOperation(value = "Update phone number", notes = "Updates the logged in user's phone number.")
	@ResponseStatus(OK)
	public void updateSettingsPhoneNumber(@RequestParam("phoneNumber") String phoneNumber) {

		userService.updatePhoneNumber(phoneNumber);
	}

	@RequestMapping(value = "/settings/is-active", method = PUT)
	@ApiOperation(value = "Update active state", notes = "Updates the logged in user's active state.")
	@ResponseStatus(OK)
	public void updateSettingsIsActive(@RequestParam("isActive") Boolean isActive) {

		userService.updateIsActive(isActive);
	}

	@RequestMapping(value = "/signature/crop", method = POST)
	@ApiOperation(value = "Crop existing signature", notes = "Stores the cropping data and cropped image into the database.")
	@ResponseStatus(CREATED)
	public void uploadSignature(@RequestBody CroppingDto dto) {

		userService.addSignatureCropping(dto);
	}

	@RequestMapping(value = "/signature/token", method = POST)
	@ApiOperation(value = "Create token for mobile access", notes = "Creates a new signature token for mobile access.")
	@ResponseStatus(CREATED)
	public Token createSignatureMobileToken() {

		return userService.createSignatureMobileToken();
	}
}
