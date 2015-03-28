package ch.uzh.csg.reimbursement.server.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.csg.reimbursement.server.Message;
import ch.uzh.csg.reimbursement.server.UppercaseService;

@RestController
public class TestController {

	@Autowired
	private UppercaseService uppercaseService;

	@RequestMapping(value = "/message/{name}", method = GET)
	public Message message(@PathVariable String name) {
		return new Message(uppercaseService.up(name), "Hello "+name);
	}

}