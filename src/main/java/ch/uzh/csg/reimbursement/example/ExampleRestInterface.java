package ch.uzh.csg.reimbursement.example;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/example")
@Api(value = "example", description = "Example Stuff")
public class ExampleRestInterface {

	@Autowired
	private UppercaseService uppercaseService;

	@RequestMapping(value = "/remove", method = DELETE)
	@ApiOperation(value = "Removes something", notes = "Removes something. E.g. a user or so.")
	public Message removeUser(@RequestBody Person person) {
		return new Message("System", person.getFirstName() + " " + person.getLastName() + " removed");
	}

	@RequestMapping(value = "/message/{name}", method = GET, produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get message", notes = "Returns a message")
	public Message message(@PathVariable String name) {
		return new Message(uppercaseService.up(name), "Hello " + name);
	}

}