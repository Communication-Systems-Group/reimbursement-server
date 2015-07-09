package ch.uzh.csg.reimbursement.model;

import ch.uzh.csg.reimbursement.model.exception.SignatureException;

@SuppressWarnings("serial")
public class SignatureMaxFileSizeViolationException extends SignatureException {

	private final static String MESSAGE = "The file size exceeds the max allowed file size.";
	public SignatureMaxFileSizeViolationException() {
		super(MESSAGE);
	}

}
