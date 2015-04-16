package ch.uzh.csg.reimbursement.example;

import org.springframework.stereotype.Service;

@Service
public class UppercaseService {

	public String up(String text) {
		return text.toUpperCase();
	}

}
