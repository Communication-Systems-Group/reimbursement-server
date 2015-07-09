package ch.uzh.csg.reimbursement.model;

import ch.uzh.csg.reimbursement.model.exception.SignatureException;

@SuppressWarnings("serial")
public class SignatureMinFileSizeViolationException extends SignatureException {

	private final static String MESSAGE = "The provided file is empty or does not reach the the min allowed file size.";
	public SignatureMinFileSizeViolationException() {
		super(MESSAGE);
	}

}
