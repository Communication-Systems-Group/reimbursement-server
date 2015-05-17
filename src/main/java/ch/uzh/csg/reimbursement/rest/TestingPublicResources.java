package ch.uzh.csg.reimbursement.rest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.csg.reimbursement.dto.CroppingDto;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.service.UserService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/testingpublic")
@Api(value = "testingpublic", description = "Public testing Resources")
public class TestingPublicResources {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestingPublicResources.class);

	@Autowired
	private UserService userService;

	@RequestMapping(method = GET)
	@ApiOperation(value = "Find all users", notes = "Finds all users which are currently in the system.")
	public List<User> getAllUsers() {
		return userService.findAll();
	}

	@RequestMapping(value = "/{uid}", method = GET)
	@ApiOperation(value = "Find one user with an uid", notes = "Finds exactly one user by its uid.")
	public User findUserByUid(@PathVariable("uid") String uid) {
		return userService.findByUid(uid);
	}

	@RequestMapping(value = "/string", method = POST)
	@ApiOperation(value = "Upload a String", notes = "Upload a String that is returned")
	public String uploadString(@RequestParam("string") String string) {
		LOGGER.info("string:"+string);
		return string;
	}

	@RequestMapping(value = "/croppingdto", method = POST)
	@ApiOperation(value = "Upload a CroppingDto", notes = "Upload a CroppingDto that is returned")
	public CroppingDto uploadCroppingDto(@RequestBody CroppingDto dto) {
		LOGGER.info("dto height:"+dto.getHeight() + "dto width: "+ dto.getWidth());
		return dto;
	}
}