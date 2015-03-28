package ch.uzh.csg.reimbursement.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

	@Autowired
	private Person person;

	@Autowired
	private UppercaseService uppercaseService;

	public String getFirstNameInUppercase() {
		String firstName = person.getFirstName();
		return uppercaseService.up(firstName);
	}

	public String getConcatedNameInUppercase() {
		String firstName = person.getFirstName();
		String lastName = person.getLastName();
		return uppercaseService.up(firstName) + " " + uppercaseService.up(lastName);
	}

	public void sayHelloToPerson() {
		person.sayHello();
	}
}
