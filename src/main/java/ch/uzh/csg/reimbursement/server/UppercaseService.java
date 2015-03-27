package ch.uzh.csg.reimbursement.server;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UppercaseService {

	@Autowired
	private Person person;

	@Autowired
	private Logger logger;

	public String getFirstNameInUppercase() {
		String firstName = person.getFirstName();
		return firstName.toUpperCase();
	}

	public String getConcatedNameInUppercase() {
		String firstName = person.getFirstName();
		String lastName = person.getLastName();
		return firstName.toUpperCase() + " " + lastName.toUpperCase();
	}

	public void logLastName() {
		logger.info(person.getLastName());
	}

}
