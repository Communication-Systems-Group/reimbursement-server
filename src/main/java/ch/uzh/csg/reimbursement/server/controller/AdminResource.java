package ch.uzh.csg.reimbursement.server.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.csg.reimbursement.server.Message;
import ch.uzh.csg.reimbursement.server.UppercaseService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/admin")
@Api(value = "Administration", description = "All administration methods are listed here.")
public class AdminResource {

	@Autowired
	private UppercaseService uppercaseService;

	@RequestMapping(value = "/message/{name}", method = GET, produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get message", notes = "Returns message")
	public Message message(@PathVariable String name) {
		return new Message(uppercaseService.up(name), "Hello " + name);
	}

}