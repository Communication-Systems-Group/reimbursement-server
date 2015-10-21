package ch.uzh.csg.reimbursement.rest;

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

import ch.uzh.csg.reimbursement.dto.CroppingDto;
import ch.uzh.csg.reimbursement.mail.SimpleEmailService;
import ch.uzh.csg.reimbursement.model.Language;
import ch.uzh.csg.reimbursement.model.Signature;
import ch.uzh.csg.reimbursement.model.Token;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.service.UserService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('USER')")
@Api(value = "User", description = "Authorized access for all users.")
public class UserResource {

	// resource naming convention
	// http://www.restapitutorial.com/lessons/restfulresourcenaming.html

	@Autowired
	private UserService userService;

	@Autowired
	private SimpleEmailService emailService;

	@RequestMapping(method = GET)
	@ApiOperation(value = "Returns the currently logged in user")
	public User getLoggedInUser() {

		return userService.getLoggedInUser();
	}

	@PreAuthorize("hasRole('REGISTERED_USER')")
	@RequestMapping(value = "/email", method = POST)
	@ApiOperation(value = "send an Email")
	public void sendEmail() {
		emailService.sendEmail();
	}

	@RequestMapping(value = "/signature", method = POST)
	@ApiOperation(value = "Upload a new signature")
	public void addSignature(@RequestParam("file") MultipartFile file) {

		userService.addSignature(file);
	}

	@RequestMapping(value = "/signature", method = GET)
	@ApiOperation(value = "Retrieve the signature image")
	public Signature getSignature(HttpServletResponse response) {

		return userService.getSignature();
	}

	@RequestMapping(value = "/settings/language", method = PUT)
	@ApiOperation(value = "Update the logged in user's language settings.")
	@ResponseStatus(OK)
	public void updateSettingsLanguage(@RequestParam Language language) {

		userService.updateLanguage(language);
	}

	@RequestMapping(value = "/settings/personnel-number", method = PUT)
	@ApiOperation(value = "Update the logged in user's personnel number")
	@ResponseStatus(OK)
	public void updateSettingsPersonnelNumber(@RequestParam("personnelNumber") String personnelNumber) {

		userService.updatePersonnelNumber(personnelNumber);
	}

	@RequestMapping(value = "/settings/phone-number", method = PUT)
	@ApiOperation(value = "Update the logged in user's phone number.")
	@ResponseStatus(OK)
	public void updateSettingsPhoneNumber(@RequestParam("phoneNumber") String phoneNumber) {

		userService.updatePhoneNumber(phoneNumber);
	}

	@RequestMapping(value = "/settings/is-active", method = PUT)
	@ApiOperation(value = "Update the logged in user's active state")
	@ResponseStatus(OK)
	public void updateSettingsIsActive(@RequestParam("isActive") Boolean isActive) {

		userService.updateIsActive(isActive);
	}

	@RequestMapping(value = "/signature/crop", method = POST)
	@ApiOperation(value = "Crop the existing signature", notes = "Stores the cropping data and cropped image into the database.")
	public void uploadSignature(@RequestBody CroppingDto dto) {

		userService.addSignatureCropping(dto);
	}

	@RequestMapping(value = "/signature/token", method = POST)
	@ApiOperation(value = "Create a new signature token for mobile access")
	public Token createSignatureMobileToken() {

		return userService.createSignatureMobileToken();
	}
}
