package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class SignViolationException extends ExpenseException {
	private final static String MESSAGE = "The logged in user has no rights to sign this resource.";

	public SignViolationException() {
		super(MESSAGE);
	}
}
