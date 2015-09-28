package ch.uzh.csg.reimbursement.model.exception;


@SuppressWarnings("serial")
public class MaxFileSizeViolationException extends SignatureException {

	private final static String MESSAGE = "The file size exceeds the max allowed file size.";
	public MaxFileSizeViolationException() {
		super(MESSAGE);
	}

}
