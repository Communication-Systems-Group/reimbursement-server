package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class ValidationNotFoundException extends BusinessException {
	private final static String MESSAGE = "The Validation could not be found.";
	public ValidationNotFoundException() {
		super(MESSAGE);
	}
}
