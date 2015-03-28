package ch.uzh.csg.reimbursement.server;

import org.springframework.stereotype.Service;

@Service
public class UppercaseService {

	public String up(String text) {
		return text.toUpperCase();
	}

}
